package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.model.db.working.PerformanceYear;
import com.clevel.kudu.api.model.db.working.PerformanceYear_;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Date;
import java.util.List;

public class PerformanceYearDAO extends GenericDAO<PerformanceYear, Long> {

    @Inject
    private Logger log;

    @Inject
    public PerformanceYearDAO() {
    }

    @SuppressWarnings("unchecked")
    public PerformanceYear findByYear(int year) throws RecordNotFoundException {
        log.debug("findByYear. (year: {})", year);
        CriteriaQuery<PerformanceYear> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(PerformanceYear_.year), year)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<PerformanceYear> performanceYearList = query.getResultList();
        if (performanceYearList.isEmpty()) {
            String message = "performance year(" + year + ") not found!";
            log.warn(message);
            throw new RecordNotFoundException(message);
        } else {
            PerformanceYear performanceYear = performanceYearList.get(0);
            log.debug("findByYear. (result: {})", performanceYear);
            return performanceYear;
        }
    }

    @SuppressWarnings("unchecked")
    public PerformanceYear findByDate(Date date) throws RecordNotFoundException {
        log.debug("findByDate. (date: {})", date);
        CriteriaQuery<PerformanceYear> criteria = createCriteriaQuery();

        criteria.where(
                cb.and(
                        cb.lessThanOrEqualTo(root.get(PerformanceYear_.startDate), date),
                        cb.greaterThanOrEqualTo(root.get(PerformanceYear_.endDate), date)
                )
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<PerformanceYear> performanceYearList = query.getResultList();
        if (performanceYearList.isEmpty()) {
            String message = "performance year of date(" + date + ") not found!";
            log.warn(message);
            throw new RecordNotFoundException(message);
        } else {
            PerformanceYear performanceYear = performanceYearList.get(0);
            log.debug("findByDate. (result: {})", performanceYear);
            return performanceYear;
        }
    }
}
