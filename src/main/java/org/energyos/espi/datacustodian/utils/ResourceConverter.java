package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.LinkType;

public class ResourceConverter {
    public void convert(EntryType entry) {
        for(IdentifiedObject resource : entry.getContent().getResources()) {
            resource.setMRID(entry.getId());
            for(LinkType link : entry.getLinks()) {
                if (link.getRel().equals(LinkType.SELF))
                    resource.setSelfLink(link.getHref());
                if (link.getRel().equals(LinkType.UP))
                    resource.setUpLink(link);
                if (link.getRel().equals(LinkType.RELATED))
                    resource.getRelatedLinks().add(link);
            }
            resource.setDescription(entry.getTitle());
            resource.setPublished(entry.getPublished().getValue().toGregorianCalendar());
            resource.setUpdated(entry.getUpdated().getValue().toGregorianCalendar());
        }
    }
}
