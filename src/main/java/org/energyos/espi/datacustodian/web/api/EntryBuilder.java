package org.energyos.espi.datacustodian.web.api;

import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.ContentType;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.LinkType;
import org.energyos.espi.datacustodian.utils.DateConverter;

public class EntryBuilder {

    private EntryType entry;

    public EntryBuilder() {
        entry = new EntryType();
    }

    public EntryType build(UsagePoint usagePoint) {
        buildMetadata(usagePoint);
        buildContent(usagePoint);

        return entry;
    }

    private void buildContent(UsagePoint usagePoint) {
        ContentType content = new ContentType();
        content.setUsagePoint(usagePoint);
        entry.setContent(content);
    }

    private void buildMetadata(UsagePoint usagePoint) {
        entry.setId("urn:uuid:" + usagePoint.getUUID().toString());
        entry.setTitle(usagePoint.getDescription());
        entry.setPublished(DateConverter.toDateTimeType(usagePoint.getCreated()));
        entry.setUpdated(DateConverter.toDateTimeType(usagePoint.getUpdated().getTime()));

        buildLinks(usagePoint);
    }

    private void buildLinks(UsagePoint usagePoint) {
        entry.getLinks().add(new LinkType("up", usagePoint.getUpHref()));
        entry.getLinks().add(new LinkType("self", usagePoint.getSelfHref()));

        for (LinkType link : usagePoint.getRelatedLinks()) {
            entry.getLinks().add(link);
        }
    }
}