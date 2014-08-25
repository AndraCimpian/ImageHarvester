package eu.europeana.harvester.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Contains all information about an owner.
 */
public class ReferenceOwner implements Serializable {

    /**
     * The provider this job refers to. Useful for querying and stats.
     */
    private final String providerId;

    /**
     * The collection this job refers to. Useful for querying and stats.
     */
    private final String collectionId;

    /**
     * The record this job refers to. Useful for querying and stats.
     */
    private final String recordId;

    public ReferenceOwner() {
        this.providerId = null;
        this.collectionId = null;
        this.recordId = null;
    }

    public ReferenceOwner(final String providerId, final String collectionId, final String recordId) {
        this.providerId = providerId;
        this.collectionId = collectionId;
        this.recordId = recordId;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public String getRecordId() {
        return recordId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(providerId).append(collectionId).append(recordId).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof  ReferenceOwner)) {
            return false;
        }
        if(obj == this) {
            return true;
        }

        final ReferenceOwner referenceOwner = (ReferenceOwner)obj;

        return new EqualsBuilder().append(providerId, referenceOwner.getProviderId()).
                append(collectionId, referenceOwner.getCollectionId()).
                append(recordId, referenceOwner.getRecordId()).isEquals();
    }
}