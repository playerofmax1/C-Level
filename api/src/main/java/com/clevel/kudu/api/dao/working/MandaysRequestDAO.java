package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.working.MandaysRequest;
import com.clevel.kudu.api.model.db.working.MandaysRequest_;
import com.clevel.kudu.model.RequestStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MandaysRequestDAO extends GenericDAO<MandaysRequest, Long> {
    @Inject
    private Logger log;

    @Inject
    public MandaysRequestDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<MandaysRequest> findByStatus(long userId, RequestStatus status, Date startDate, Date endDate) {
        log.debug("findByStatus.");
        CriteriaQuery<MandaysRequest> criteria = createCriteriaQuery();


        List<Predicate> predicates = new ArrayList<>();

        if (userId > 0) {
            predicates.add(cb.equal(root.get(MandaysRequest_.user), userId));
        }

        if (status != null) {
            predicates.add(cb.equal(root.get(MandaysRequest_.status), status));
        }

        if (startDate != null && endDate != null) {
            predicates.add(cb.between(root.get(MandaysRequest_.createDate), startDate, endDate));
        }

        if (predicates.size() > 0) {
            criteria.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        criteria.orderBy(cb.asc(root.get(MandaysRequest_.project)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<MandaysRequest> mandaysRequestList = query.getResultList();
        if (mandaysRequestList.isEmpty()) {
            log.debug("MandaysRequest for user({}) status({}) not found!", userId, status);
            return new ArrayList<>();
        } else {
            return mandaysRequestList;
        }
    }
}