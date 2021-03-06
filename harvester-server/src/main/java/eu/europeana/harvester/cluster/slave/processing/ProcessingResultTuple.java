package eu.europeana.harvester.cluster.slave.processing;

import eu.europeana.harvester.cluster.slave.processing.metainfo.MediaMetaInfoTuple;
import eu.europeana.harvester.domain.*;

import java.util.Collection;

public class ProcessingResultTuple {
    private final MediaMetaInfoTuple mediaMetaInfoTuple;
    private final Collection<MediaFile> generatedThumbnails;
    private final ProcessingJobSubTaskStats processingJobSubTaskStats;

    public ProcessingResultTuple(ProcessingJobSubTaskStats processingJobSubTaskStats,
                                 MediaMetaInfoTuple mediaMetaInfoTuple,
                                 Collection<MediaFile> generatedThumbnails
                                 ) {
        this.processingJobSubTaskStats = processingJobSubTaskStats;
        this.mediaMetaInfoTuple = mediaMetaInfoTuple;
        this.generatedThumbnails = generatedThumbnails;
    }

    public MediaMetaInfoTuple getMediaMetaInfoTuple() {
        return mediaMetaInfoTuple;
    }

    public Collection<MediaFile> getGeneratedThumbnails() {
        return generatedThumbnails;
    }

    public ProcessingJobSubTaskStats getProcessingJobSubTaskStats () {
        return processingJobSubTaskStats;
    }
}
