package eu.europeana.harvester.cluster.master.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import eu.europeana.harvester.domain.ProcessingJobRetrieveSubTaskState;
import eu.europeana.harvester.domain.ProcessingJobSubTaskState;
import eu.europeana.harvester.domain.ProcessingState;
import eu.europeana.harvester.httpclient.response.RetrievingState;
import eu.europeana.harvester.monitoring.LazyGauge;

import java.util.HashMap;
import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;

public class MasterMetrics {
    public static final String COUNTER = "counter";
    public static final String DURATION = "duration";

    public static final String TOTAL = "total";

    public static final String SEND_JOBS_SET_TO_SLAVE = "sendJobsSetToSlave";
    public static final String LOAD_JOBS_FROM_DB = "loadJobsFromDB";
    public static final String LOAD_JOBS_TASKS_FROM_DB = "loadJobsTasksFromDB";
    public static final String LOAD_JOBS_RESOURCES_FROM_DB = "loadJobsResourcesFromDB";
    public static final String LOAD_FASTLANEJOBS_FROM_DB = "loadFastLaneJobsFromDB";
    public static final String LOAD_FASTLANEJOBS_TASKS_FROM_DB = "loadFastLaneJobsTasksFromDB";
    public static final String LOAD_FASTLANEJOBS_RESOURCES_FROM_DB = "loadFastLaneJobsResourcesFromDB";

    public static final String DONE_DOWNLOAD = "doneDownload";
    public static final String JOBS_PERSISTENCE = "jobsPersistence";

    public static final String DONE_PROCESSING = "doneProcessing";

    public static final String DONE_PROCESSING_RETRIEVE = "doneProcessing.retrieve";
    public static final String DONE_PROCESSING_COLOR_EXTRACTION = "doneProcessing.colorExtraction";
    public static final String DONE_PROCESSING_META_EXTRACTION = "doneProcessing.metaExtraction";
    public static final String DONE_PROCESSING_THUMBNAIL_GENERATION = "doneProcessing.thumbnailGeneration";
    public static final String DONE_PROCESSING_THUMBNAIL_STORAGE = "doneProcessing.thumbnailStorage";

    public static final String UNREACHABLE_NODES_IN_CLUSTER = "unreachableNodesInCluster";
    public static final String CONNECTED_NODES_IN_CLUSTER = "connectedNodesInCluster";

    public static final String JOBS_UNIQUE_IPS = "jobsUniqueIPs";
    public static final String IP_LIMIT_GRANTED_SLOT_REQUEST = "ipLimitGrantedSlotRequest";
    public static final String IP_LIMIT_NOT_GRANTED_SLOT_REQUEST = "ipLimitNotGrantedSlotRequest";
    public static final String IP_LIMIT_RETURNED_GRANTED_SLOT_REQUEST = "ipLimitReturnedGrantedSlotRequest";

    public static final String JOBS_FAST_LANE_WAITING = "jobsFastLaneWaiting";
    public static final String JOBS_NORMAL_LANE_WAITING = "jobsNormalLaneWaiting";
    public static final String JOBS_ALL_STARTED_TASKS = "jobsAllStarted";
    public static final String JOBS_ALL_RECLAIMED_TASKS = "jobsAllReclaimed";

    public static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    public static class MasterDatabase {
        public static final String NAME = "MasterMetrics" + "." + "MasterDatabase";
        public static final LazyGauge HistoricalProcessingJobCollectionSize = new LazyGauge(METRIC_REGISTRY, name(Master.NAME,"HistoricalProcessingJobCollectionSize",COUNTER));
        public static final LazyGauge ProcessingJobCollectionSize = new LazyGauge(METRIC_REGISTRY, name(Master.NAME,"ProcessingJobCollectionSize",COUNTER));
        public static final LazyGauge LastSourceDocumentProcessingStatisticsCollectionSize = new LazyGauge(METRIC_REGISTRY, name(Master.NAME,"LastSourceDocumentProcessingStatisticsCollectionSize",COUNTER));
        public static final LazyGauge MachineResourceReferenceCollectionSize = new LazyGauge(METRIC_REGISTRY, name(Master.NAME,"MachineResourceReferenceCollectionSize",COUNTER));
        public static final LazyGauge SourceDocumentProcessingStatisticsCollectionSize = new LazyGauge(METRIC_REGISTRY, name(Master.NAME,"SourceDocumentProcessingStatisticsCollectionSize",COUNTER));
        public static final LazyGauge SourceDocumentReferenceCollectionSize = new LazyGauge(METRIC_REGISTRY, name(Master.NAME,"SourceDocumentReferenceCollectionSize",COUNTER));
        public static final LazyGauge SourceDocumentReferenceMetaInfoCollectionSize = new LazyGauge(METRIC_REGISTRY, name(Master.NAME,"SourceDocumentReferenceMetaInfoCollectionSize",COUNTER));
        public static final LazyGauge SourceDocumentReferenceProcessingProfileCollectionSize = new LazyGauge(METRIC_REGISTRY, name(Master.NAME,"SourceDocumentReferenceProcessingProfileCollectionSize",COUNTER));
    }

    public static class Master {
        public static final String NAME = "MasterMetrics" + "." + "Master";
        public static final Timer sendJobSetToSlaveDuration = METRIC_REGISTRY.timer(name(Master.NAME, SEND_JOBS_SET_TO_SLAVE, DURATION));
        public static final Counter sendJobSetToSlaveCounter = METRIC_REGISTRY.counter(name(Master.NAME, SEND_JOBS_SET_TO_SLAVE, COUNTER));
        public static final Timer loadJobFromDBDuration = METRIC_REGISTRY.timer(name(Master.NAME, LOAD_JOBS_FROM_DB, COUNTER));

        public static final Timer loadJobTasksFromDBDuration = METRIC_REGISTRY.timer(name(Master.NAME, LOAD_JOBS_TASKS_FROM_DB, COUNTER));
        public static final Timer loadJobResourcesFromDBDuration = METRIC_REGISTRY.timer(name(Master.NAME, LOAD_JOBS_RESOURCES_FROM_DB, COUNTER));

        public static final Timer loadFastLaneJobTasksFromDBDuration = METRIC_REGISTRY.timer(name(Master.NAME, LOAD_FASTLANEJOBS_TASKS_FROM_DB, COUNTER));
        public static final Timer loadFastLaneJobResourcesFromDBDuration = METRIC_REGISTRY.timer(name(Master.NAME, LOAD_FASTLANEJOBS_RESOURCES_FROM_DB, COUNTER));

        public static final LazyGauge unreachableNodesInClusterCount = new LazyGauge(METRIC_REGISTRY, name(Master.NAME, UNREACHABLE_NODES_IN_CLUSTER, COUNTER));
        public static final LazyGauge connectedNodesInClusterCount = new LazyGauge(METRIC_REGISTRY, name(Master.NAME, CONNECTED_NODES_IN_CLUSTER, COUNTER));

        public static final LazyGauge jobsUniqueIPsCount = new LazyGauge(METRIC_REGISTRY, name(Master.NAME, JOBS_UNIQUE_IPS, COUNTER));

        public static final LazyGauge jobAccountantFastLaneWaitingCount = new LazyGauge(METRIC_REGISTRY, name(Master.NAME, JOBS_FAST_LANE_WAITING, COUNTER));
        public static final LazyGauge jobAccountantNormalLaneWaitingCount = new LazyGauge(METRIC_REGISTRY, name(Master.NAME, JOBS_NORMAL_LANE_WAITING, COUNTER));
        public static final LazyGauge jobAccountantAllStartedCount = new LazyGauge(METRIC_REGISTRY, name(Master.NAME, JOBS_ALL_STARTED_TASKS, COUNTER));
        public static final LazyGauge jobAccountantAllReclaimedCount = new LazyGauge(METRIC_REGISTRY, name(Master.NAME, JOBS_ALL_RECLAIMED_TASKS, COUNTER));

        public static final Counter ipLimitGrantedSlotRequestCounter = METRIC_REGISTRY.counter(name(Master.NAME, IP_LIMIT_GRANTED_SLOT_REQUEST, COUNTER));
        public static final Counter ipLimitNotGrantedSlotRequestCounter = METRIC_REGISTRY.counter(name(Master.NAME, IP_LIMIT_NOT_GRANTED_SLOT_REQUEST, COUNTER));
        public static final Counter ipLimitReturnedGrantedSlotRequestCounter = METRIC_REGISTRY.counter(name(Master.NAME, IP_LIMIT_RETURNED_GRANTED_SLOT_REQUEST, COUNTER));

        public static final Map<RetrievingState, Counter> doneDownloadStateCounters = new HashMap();

        static {
            for (final RetrievingState state : RetrievingState.values()) {
                doneDownloadStateCounters.put(state, METRIC_REGISTRY.counter(name(Master.NAME, DONE_DOWNLOAD, state.name(), COUNTER)));
            }
        }

        public static final Counter doneDownloadTotalCounter = METRIC_REGISTRY.counter(name(Master.NAME, DONE_DOWNLOAD, TOTAL, COUNTER));

        public static final Map<ProcessingState, Counter> doneProcessingStateCounters = new HashMap();

        static {
            for (final ProcessingState state : ProcessingState.values()) {
                doneProcessingStateCounters.put(state, METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING, state.name(), COUNTER)));
            }
        }

        public static final Counter doneProcessingTotalCounter = METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING, TOTAL, COUNTER));

        // Sub tasks counters

        /* RETRIEVE SUB TASK */
        public static final Map<ProcessingJobRetrieveSubTaskState, Counter> doneProcessingRetrieveStateCounters = new HashMap();

        static {
            for (final ProcessingJobRetrieveSubTaskState state : ProcessingJobRetrieveSubTaskState.values()) {
                doneProcessingRetrieveStateCounters.put(state, METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING_RETRIEVE, state.name(), COUNTER)));
            }
        }

        public static final Counter doneProcessingRetrieveTotalCounter = METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING_RETRIEVE, TOTAL, COUNTER));

        /* COLOR EXTRACTION SUB TASK */
        public static final Map<ProcessingJobSubTaskState, Counter> doneProcessingColorExtractionStateCounters = new HashMap();

        static {
            for (final ProcessingJobSubTaskState state : ProcessingJobSubTaskState.values()) {
                doneProcessingColorExtractionStateCounters.put(state, METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING_COLOR_EXTRACTION, state.name(), COUNTER)));
            }
        }

        public static final Counter doneProcessingColorExtractionTotalCounter = METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING_COLOR_EXTRACTION, TOTAL, COUNTER));

        /* META INFO EXTRACTION SUB TASK */

        public static final Map<ProcessingJobSubTaskState, Counter> doneProcessingMetaExtractionStateCounters = new HashMap();

        static {
            for (final ProcessingJobSubTaskState state : ProcessingJobSubTaskState.values()) {
                doneProcessingMetaExtractionStateCounters.put(state, METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING_META_EXTRACTION, state.name(), COUNTER)));
            }
        }

        public static final Counter doneProcessingMetaExtractionTotalCounter = METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING_META_EXTRACTION, TOTAL, COUNTER));

        /* THUMBNAIL GENERATION SUB TASK */

        public static final Map<ProcessingJobSubTaskState, Counter> doneProcessingThumbnailGenerationStateCounters = new HashMap();

        static {
            for (final ProcessingJobSubTaskState state : ProcessingJobSubTaskState.values()) {
                doneProcessingThumbnailGenerationStateCounters.put(state, METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING_THUMBNAIL_GENERATION, state.name(), COUNTER)));
            }
        }

        public static final Counter doneProcessingThumbnailGenerationTotalCounter = METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING_THUMBNAIL_GENERATION, TOTAL, COUNTER));


        /* THUMBNAIL STORAGE SUB TASK */

        public static final Map<ProcessingJobSubTaskState, Counter> doneProcessingThumbnailStorageStateCounters = new HashMap();

        static {
            for (final ProcessingJobSubTaskState state : ProcessingJobSubTaskState.values()) {
                doneProcessingThumbnailStorageStateCounters.put(state, METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING_THUMBNAIL_STORAGE, state.name(), COUNTER)));
            }
        }

        public static final Counter doneProcessingThumbnailStorageTotalCounter = METRIC_REGISTRY.counter(name(Master.NAME, DONE_PROCESSING_THUMBNAIL_STORAGE, TOTAL, COUNTER));


    }
}
