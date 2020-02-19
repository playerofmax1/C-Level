package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.model.db.master.Rate;
import com.clevel.kudu.api.model.db.master.Rate_;
import com.clevel.kudu.model.RecordStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class RateDAO extends GenericDAO<Rate, Long> {
    @Inject
    private Logger log;

    @Inject
    public RateDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<Rate> findAll() {
        log.debug("findAll.");
        CriteriaQuery<Rate> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(Rate_.status), RecordStatus.ACTIVE)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<Rate> rates = query.getResultList();
        log.debug("findAll. (result: {})",rates.size());
        return rates;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Rate findById(Long id) throws RecordNotFoundException {
        log.debug("findById. (id: {})", id);

        CriteriaQuery<Rate> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(Rate_.id), id)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<Rate> rates = query.getResultList();
        if (rates.isEmpty()) {
            log.error("rate not found! (customerId: {})", id);
            throw new RecordNotFoundException("rate not found!");
        } else {
            return rates.get(0);
        }
    }

}
