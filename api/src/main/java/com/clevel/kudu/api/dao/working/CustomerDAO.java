package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.model.db.working.Customer;
import com.clevel.kudu.api.model.db.working.Customer_;
import com.clevel.kudu.model.RecordStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class CustomerDAO extends GenericDAO<Customer, Long> {
    @Inject
    private Logger log;

    @Inject
    public CustomerDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<Customer> findAll() {
        log.debug("findAll.");
        CriteriaQuery<Customer> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(Customer_.status), RecordStatus.ACTIVE)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<Customer> customers = query.getResultList();
        log.debug("findAll. (result: {})",customers.size());
        return customers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Customer findById(Long id) throws RecordNotFoundException {
        log.debug("findById. (id: {})", id);

        CriteriaQuery<Customer> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(Customer_.id), id)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<Customer> customers = query.getResultList();
        if (customers.isEmpty()) {
            log.error("customer not found! (customerId: {})", id);
            throw new RecordNotFoundException("customer not found!");
        } else {
            return customers.get(0);
        }
    }

}
