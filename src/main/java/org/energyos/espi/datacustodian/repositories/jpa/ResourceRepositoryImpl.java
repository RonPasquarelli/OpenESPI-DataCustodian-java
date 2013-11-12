package org.energyos.espi.datacustodian.repositories.jpa;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.domain.Linkable;
import org.energyos.espi.datacustodian.repositories.ResourceRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
class ResourceRepositoryImpl implements ResourceRepository {
    @PersistenceContext
    protected EntityManager em;

    @Override
    public void persist(IdentifiedObject resource) {
        em.persist(resource);
    }

    @Override
    public IdentifiedObject findByRelatedHref(String href, Linkable object) {
        return (IdentifiedObject)em.createNamedQuery(object.getRelatedLinkQuery()).setParameter("href", href).getSingleResult();
    }
}