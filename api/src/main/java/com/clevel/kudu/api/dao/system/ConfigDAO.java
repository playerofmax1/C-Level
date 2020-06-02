package com.clevel.kudu.api.dao.system;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.model.db.system.Config;
import com.clevel.kudu.api.model.db.system.Config_;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class ConfigDAO extends GenericDAO<Config, Long> {
    @Inject
    private Logger log;

    @Inject
    public ConfigDAO() {
    }

    @SuppressWarnings("unchecked")
    public Config findByName(String name) throws RecordNotFoundException {
        log.debug("getByName. (name: {})",name);

        CriteriaQuery<Config> criteria = createCriteriaQuery();

        criteria.where(cb.equal(root.get(Config_.name), name));

        Query query = em.createQuery(criteria);

        List<Config> configList = query.getResultList();
        if (configList.isEmpty()) {
            log.error("config not found! (name: {})", name);
            throw new RecordNotFoundException("name not found!");
        } else {
            return configList.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Config> findAll() {
        log.debug("findAll.");

        CriteriaQuery<Config> criteria = createCriteriaQuery();

        criteria.orderBy(cb.asc(root.get(Config_.name)));

        Query query = em.createQuery(criteria);

        return query.getResultList();
    }
}
