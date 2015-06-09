package eu.europeana.crfmigration.dao;

import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import eu.europeana.crfmigration.domain.MongoConfig;
import eu.europeana.harvester.client.HarvesterClientConfig;
import eu.europeana.harvester.client.HarvesterClientImpl;
import eu.europeana.harvester.db.MorphiaDataStore;
import eu.europeana.harvester.domain.ProcessingJob;
import eu.europeana.harvester.domain.SourceDocumentReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.shared.utils.StringUtils;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class MigratorHarvesterDao {
    private static final Logger LOG = LogManager.getLogger(MigratorHarvesterDao.class.getName());

    private final HarvesterClientImpl harvesterClient;

    public MigratorHarvesterDao (MongoConfig mongoConfig) throws UnknownHostException {

        final MorphiaDataStore datastore =
                new MorphiaDataStore(mongoConfig.getHost(), mongoConfig.getPort(),mongoConfig.getdBName());


        if (StringUtils.isNotEmpty(mongoConfig.getdBUsername())  &&
                StringUtils.isNotEmpty(mongoConfig.getdBPassword())) {

            final Boolean auth = datastore.getMongo().getDB("admin").authenticate(mongoConfig.getdBUsername(),
                    mongoConfig.getdBPassword()
                            .toCharArray());

            if (!auth) {
                throw new MongoException("Cannot authenticate to mongo database");
            }
        }

        harvesterClient = new HarvesterClientImpl(datastore, new HarvesterClientConfig(WriteConcern.SAFE));

    }


    public void saveSourceDocumentReferences(final List<SourceDocumentReference> sourceDocumentReferences) throws MalformedURLException, UnknownHostException, InterruptedException, ExecutionException, TimeoutException {
        try{
            LOG.info("start saving sourceDocumentReferences");
            harvesterClient.createOrModifySourceDocumentReference(sourceDocumentReferences);
        }
        finally {
            LOG.info ("saving sourceDocumentReferences is done");
        }
    }

    public void saveProcessingJobs(final List<ProcessingJob> jobs) {
        LOG.info ("saving created jobs");
        try {
            for (final ProcessingJob processingJob : jobs) {
                harvesterClient.createOrModify(processingJob);
            }
        }
        finally {
            LOG.info ("done saving jobs");
        }
    }

}

