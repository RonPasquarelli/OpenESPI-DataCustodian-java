package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.service.ResourceService;
import org.springframework.dao.EmptyResultDataAccessException;

public class EntryProcessor {
    private ResourceLinker resourceLinker;
    private ResourceConverter resourceConverter;
    private ResourceService resourceService;

    public EntryProcessor() {
        this.resourceLinker = new ResourceLinker();
        this.resourceConverter = new ResourceConverter();
    }

    public EntryProcessor(ResourceLinker resourceLinker, ResourceConverter resourceConverter, ResourceService resourceService) {
        this.resourceLinker = resourceLinker;
        this.resourceConverter = resourceConverter;
        this.resourceService = resourceService;
    }

    public EntryType process(EntryType entry) {
        convert(entry);
        for(IdentifiedObject resource : entry.getContent().getResources()) {
            try {
                IdentifiedObject existingResource = resourceService.findByUUID(resource.getUUID(), resource.getClass());
                existingResource.merge(resource);
                link(existingResource);
            } catch (EmptyResultDataAccessException x) {
                link(resource);
            }
        }
        return entry;
    }

    public void link(IdentifiedObject resources) {
        resourceLinker.link(resources);
    }

    public void convert(EntryType entry) {
        resourceConverter.convert(entry);
    }
}
