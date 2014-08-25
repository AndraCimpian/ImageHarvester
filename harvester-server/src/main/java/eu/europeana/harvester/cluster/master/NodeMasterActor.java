package eu.europeana.harvester.cluster.master;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.SmallestMailboxPool;
import eu.europeana.harvester.cluster.domain.NodeMasterConfig;
import eu.europeana.harvester.cluster.domain.messages.*;
import eu.europeana.harvester.cluster.domain.utils.ActorState;
import eu.europeana.harvester.cluster.slave.DownloaderSlaveActor;
import eu.europeana.harvester.cluster.slave.ProcesserSlaveActor;
import eu.europeana.harvester.cluster.slave.PingerSlaveActor;
import eu.europeana.harvester.domain.DocumentReferenceTaskType;
import eu.europeana.harvester.domain.ProcessingState;
import eu.europeana.harvester.httpclient.response.HttpRetrieveResponseFactory;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This actor decides which slave execute which task.
 */
public class NodeMasterActor extends UntypedActor {

    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    /**
     * The channel factory used by netty to build the channel.
     */
    private final ChannelFactory channelFactory;

    /**
     * An object which contains all the config information needed by this actor to start.
     */
    private final NodeMasterConfig nodeMasterConfig;

    /**
     * The wheel timer usually shared across all clients.
     */
    private final HashedWheelTimer hashedWheelTimer;

    /**
     * Reference to the cluster master actor.
     * We need this to send him back statistics about the download, error messages or any other type of message.
     */
    private ActorRef clusterMaster;

    /**
     * Reference to the ping master actor.
     * We need this to send him back statistics about the ping, error messages or any other type of message.
     */
    private ActorRef pingMaster;

    /**
     * List of unprocessed messages.
     */
    private final Queue<Object> messages = new LinkedList<Object>();

    /**
     * List of downloaderActors. (this is needed by our locally implemented router)
     */
    private final HashMap<ActorRef, ActorState> downloaderActors = new HashMap<ActorRef, ActorState>();

    /**
     * Router actor which sends messages to slaves which will do different operation on downloaded documents.
     */
    private ActorRef processerRouter;

    /**
     * Router actor which sends messages to slaves which will ping different servers
     * and create statistics on their response time.
     */
    private ActorRef pingerRouter;

    /**
     * List of jobs which was stopped by the clients.
     */
    private final Set<String> jobsToStop;

    public NodeMasterActor(final ChannelFactory channelFactory, final NodeMasterConfig nodeMasterConfig) {
        this.channelFactory = channelFactory;
        this.nodeMasterConfig = nodeMasterConfig;

        this.hashedWheelTimer = new HashedWheelTimer();
        this.jobsToStop = new HashSet<String>();
    }

    @Override
    public void preStart() throws Exception {
        LOG.info("Started node master");

        // Slaves for download
        final HttpRetrieveResponseFactory httpRetrieveResponseFactory = new HttpRetrieveResponseFactory();
        final ExecutorService service = Executors.newCachedThreadPool();

        for(int i = 0; i < nodeMasterConfig.getNrOfDownloaderSlaves(); i++) {
            final ActorRef newActor = getContext().system().actorOf(Props.create(DownloaderSlaveActor.class,
                    channelFactory, hashedWheelTimer, httpRetrieveResponseFactory, nodeMasterConfig.getResponseType(),
                    nodeMasterConfig.getPathToSave(), service));
            downloaderActors.put(newActor, ActorState.READY);
        }

        // Slaves for extracting meta info
        final int maxNrOfRetries = 5;
        final SupervisorStrategy strategy =
                new OneForOneStrategy(maxNrOfRetries, scala.concurrent.duration.Duration.create(1, TimeUnit.MINUTES),
                        Collections.<Class<? extends Throwable>>singletonList(Exception.class));

        processerRouter = getContext().actorOf(
                new SmallestMailboxPool(nodeMasterConfig.getNrOfExtractorSlaves())
                        .withSupervisorStrategy(strategy)
                        .props(Props.create(ProcesserSlaveActor.class, nodeMasterConfig.getResponseType())),
                "processerRouter");

        // Slaves for pinging
        pingerRouter = getContext().actorOf(
                new SmallestMailboxPool(nodeMasterConfig.getNrOfPingerSlaves())
                        .withSupervisorStrategy(strategy)
                        .props(Props.create(PingerSlaveActor.class)),
                "pingerRouter");


        // only for debug
        final TimerTask timerTask = new TimerTask() {
            public void run(final Timeout timeout) throws Exception {
                int nr = 0;
                for(ActorRef actorRef : downloaderActors.keySet()) {
                    if(downloaderActors.get(actorRef).equals(ActorState.BUSY)) {
                        nr++;
                    }
                }
                LOG.debug("Messages: {}", messages.size());
                LOG.debug("All downloaderActors: {} working: {}", downloaderActors.size(), nr);
                hashedWheelTimer.newTimeout(this, 1000, TimeUnit.MILLISECONDS);
            }
        };
        hashedWheelTimer.newTimeout(timerTask, 1000, TimeUnit.MILLISECONDS);

        sendMessage();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof RetrieveUrl) {
            messages.add(message);

            clusterMaster = getSender();
            return;
        }
        if(message instanceof DoneDownload) {
            final DoneDownload doneDownload = (DoneDownload) message;
            final String jobId = doneDownload.getJobId();
            downloaderActors.put(getSender(), ActorState.READY);

            if(!jobsToStop.contains(jobId)) {
                if(doneDownload.getTaskType().equals(DocumentReferenceTaskType.CHECK_LINK) ||
                        doneDownload.getProcessingState().equals(ProcessingState.ERROR)) {

                    clusterMaster.tell(new DoneProcessing(doneDownload, null, null, null), getSelf());
                    deleteFile(doneDownload.getReferenceId());
                } else {
                    processerRouter.tell(message, getSelf());
                }
            }

            return;
        }
        if(message instanceof DoneProcessing) {
            final DoneProcessing doneProcessing = (DoneProcessing)message;
            final String jobId = doneProcessing.getJobId();

            // In the case of images their were thumbnails generated
            //TODO: why are we downloading documents if we delete them?
            if(doneProcessing.getImageMetaInfo() == null) {
                deleteFile(doneProcessing.getReferenceId());
            }

            if(!jobsToStop.contains(jobId)) {
                clusterMaster.tell(message, getSelf());
            }

            return;
        }
        if(message instanceof StartPing) {
            pingMaster = getSender();

            pingerRouter.tell(message, getSelf());

            return;
        }
        if(message instanceof DonePing) {
            pingMaster.tell(message, getSelf());

            return;
        }
        if(message instanceof ChangeJobState) {
            final ChangeJobState changeJobState = (ChangeJobState) message;
            LOG.debug("Changing job state to: {}", changeJobState.getNewState());
            switch (changeJobState.getNewState()) {
                case PAUSE:
                    jobsToStop.add(changeJobState.getJobId());
                    break;
                case RESUME:
                    jobsToStop.remove(changeJobState.getJobId());
                    break;
                case RUNNING:
                    jobsToStop.remove(changeJobState.getJobId());
            }

            return;
        }
        if(message instanceof Clean) {
            hashedWheelTimer.stop();
            context().system().stop(getSelf());

            return;
        }
    }

    /**
     * Sends check link or download task to free downloader actors.
     */
    private void sendMessage() {
        final TimerTask timerTask = new TimerTask() {
            public void run(final Timeout timeout) throws Exception {
                boolean readyActor = true;
                while(messages.size() != 0 && readyActor) {
                    readyActor = false;
                    for(final ActorRef actorRef : downloaderActors.keySet()) {
                        if(downloaderActors.get(actorRef).equals(ActorState.READY)) {
                            final Object message = messages.poll();
                            if(message != null) {
                                actorRef.tell(message, getSelf());
                                downloaderActors.put(actorRef, ActorState.BUSY);
                                readyActor = true;
                                break;
                            }
                        }
                    }
                }
                hashedWheelTimer.newTimeout(this, 1000, TimeUnit.MILLISECONDS);
            }
        };

        hashedWheelTimer.newTimeout(timerTask, 1000, TimeUnit.MILLISECONDS);
    }

    private void deleteFile(String fileName) {
        final String path = nodeMasterConfig.getPathToSave() + "/" + fileName;
        final File file = new File(path);
        file.delete();
    }

}