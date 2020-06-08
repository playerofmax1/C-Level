package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.model.db.working.Holiday;
import com.clevel.kudu.api.model.db.working.Holiday_;
import com.clevel.kudu.model.RecordStatus;
import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Date;
import java.util.List;

public class HolidayDAO extends GenericDAO<Holiday, Long> {
    @Inject
    private Logger log;

    @Inject
    public HolidayDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<Holiday> findAll() {
        log.debug("findAll.");
        CriteriaQuery<Holiday> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(Holiday_.status), RecordStatus.ACTIVE)
        ).orderBy(cb.asc(root.get(Holiday_.holidayDate)));
        ;

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<Holiday> holidays = query.getResultList();
        log.debug("findAll. (result: {})", holidays.size());
        return holidays;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Holiday findById(Long id) throws RecordNotFoundException {
        log.debug("findById. (id: {})", id);

        CriteriaQuery<Holiday> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(Holiday_.id), id)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<Holiday> holidays = query.getResultList();
        if (holidays.isEmpty()) {
            log.error("holiday not found! (holidayId: {})", id);
            throw new RecordNotFoundException("holiday not found!");
        } else {
            return holidays.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public long countHoliday(Date month) {
        log.debug("countHoliday. (month: {})", month);

        CriteriaQuery<Long> criteria = createCriteriaCount();

        criteria.select(cb.count(root))
                .where(cb.and(
                        cb.equal(root.get(Holiday_.status), RecordStatus.ACTIVE),
                        cb.between(root.get(Holiday_.holidayDate),
                                DateTimeUtil.getFirstDateOfMonth(month),
                                DateTimeUtil.getLastDateOfMonth(month))
                        )
                );

        Query query = em.createQuery(criteria);

        long result = (long) query.getSingleResult();
        log.debug("countHoliday. (result: {})", result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public long countHoliday(Date startDate, Date endDate) {
        log.debug("countHoliday. (start: {}, end: {})", startDate, endDate);

        CriteriaQuery<Long> criteria = createCriteriaCount();

        criteria.select(cb.count(root))
                .where(
                        cb.and(
                                cb.equal(root.get(Holiday_.status), RecordStatus.ACTIVE),
                                cb.between(root.get(Holiday_.holidayDate), startDate, endDate)
                        )
                );

        Query query = em.createQuery(criteria);

        long result = (long) query.getSingleResult();
        log.debug("countHoliday. (result: {})", result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Holiday> findByMonth(Date month) {
        log.debug("findByMonth. (month: {})", month);

        CriteriaQuery<Holiday> criteria = createCriteriaQuery();

        criteria.where(cb.and(
                cb.equal(root.get(Holiday_.status), RecordStatus.ACTIVE),
                cb.between(root.get(Holiday_.holidayDate),
                        DateTimeUtil.getFirstDateOfMonth(month),
                        DateTimeUtil.getLastDateOfMonth(month))
                )
        );

        Query query = em.createQuery(criteria);

        List<Holiday> result = query.getResultList();
        log.debug("findByMonth. (result: {})", result.size());
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Holiday> findByStartEnd(Date startDate, Date endDate) {
        log.debug("findByStartEnd(startDate: {}, endDate: {})", startDate, endDate);

        CriteriaQuery<Holiday> criteria = createCriteriaQuery();

        criteria.where(cb.and(
                cb.equal(root.get(Holiday_.status), RecordStatus.ACTIVE),
                cb.between(root.get(Holiday_.holidayDate), startDate, endDate)
                )
        );

        Query query = em.createQuery(criteria);

        List<Holiday> result = query.getResultList();
        log.debug("findByStartEnd.result.size={}", result.size());
        return result;
    }

}
