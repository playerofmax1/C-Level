package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.working.*;
import com.clevel.kudu.model.RecordStatus;
import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Date;
import java.util.List;

public class TimeSheetDAO extends GenericDAO<TimeSheet, Long> {
    @Inject
    private Logger log;

    @Inject
    public TimeSheetDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<TimeSheet> findByMonth(User user, Date firstDate,Date lastDate) {
        log.debug("findByMonth. (user: {}, firstDate: {}, lastDate: {})",user,firstDate,lastDate);
        CriteriaQuery<TimeSheet> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(TimeSheet_.status), RecordStatus.ACTIVE),
                cb.equal(root.get(TimeSheet_.user), user),
                cb.between(root.get(TimeSheet_.workDate), firstDate,lastDate)
        ).orderBy(
                cb.asc(root.get(TimeSheet_.workDate)),
                cb.asc(root.get(TimeSheet_.sortOrder))
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<TimeSheet> timeSheets = query.getResultList();
        log.debug("findByMonth. (result: {})", timeSheets.size());
        return timeSheets;
    }

    public List<TimeSheet> findByProject(Project project) {
        log.debug("findByProject. (projectId: {})",project.getId());

        CriteriaQuery<TimeSheet> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(TimeSheet_.status), RecordStatus.ACTIVE),
                cb.equal(root.get(TimeSheet_.project), project)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<TimeSheet> timeSheets = query.getResultList();
        log.debug("findByProject. (result: {})", timeSheets.size());
        return timeSheets;
    }

    public List<TimeSheet> findByProjectAndUser(Project project, User user) {
        log.debug("findByProjectAndUser. (projectId: {}, userId: {})",project.getId(),user.getId());

        CriteriaQuery<TimeSheet> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(TimeSheet_.status), RecordStatus.ACTIVE),
                cb.equal(root.get(TimeSheet_.project), project),
                cb.equal(root.get(TimeSheet_.user), user)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<TimeSheet> timeSheets = query.getResultList();
        log.debug("findByProjectAndUser. (result: {})", timeSheets.size());
        return timeSheets;
    }

//    @SuppressWarnings("unchecked")
//    public long countRecordByDate(Date date) {
//        log.debug("countRecordByDate. (date: {})", date);
//
//        CriteriaQuery<Long> criteria = createCriteriaCount();
//
//        criteria.select(cb.count(root))
//                .where(
//                        cb.equal(root.get(TimeSheet_.status), RecordStatus.ACTIVE),
//                        cb.equal(root.get(TimeSheet_.workDate), date)
//                );
//
//        Query query = em.createQuery(criteria);
//
//        long result = (long) query.getSingleResult();
//        log.debug("countRecordByDate. (result: {})",result);
//        return result;
//    }

    @SuppressWarnings("unchecked")
    public int maxSortOrderByDate(Date date) {
        log.debug("maxSortOrderByDate. (date: {})", date);

        CriteriaQuery<Integer> criteria = createCriteriaCount();

        criteria.select(cb.max(root.get(TimeSheet_.sortOrder)))
                .where(
                        cb.equal(root.get(TimeSheet_.status), RecordStatus.ACTIVE),
                        cb.equal(root.get(TimeSheet_.workDate), date)
                );

        Query query = em.createQuery(criteria);

        int result = (int) query.getSingleResult();
        log.debug("maxSortOrderByDate. (result: {})",result);
        return result;
    }

    public void delete(TimeSheet timeSheet) {
        log.debug("delete. (timeSheet: {})", timeSheet.getId());
        CriteriaDelete<TimeSheet> criteria = createCriteriaDelete();

        criteria.where(
                cb.and(cb.equal(root.get(TimeSheet_.id), timeSheet.getId()))
        );

        Query query = em.createQuery(criteria);

        int result = query.executeUpdate();

        log.debug("delete. (result: {})", result);
    }

    @SuppressWarnings("unchecked")
    public long sumManDays(long updateTimeSheetId, ProjectTask projectTask) {
        log.debug("sumManDays. (updateTimeSheetId: {}, projectTask: {})",
                updateTimeSheetId, projectTask);

        CriteriaQuery<Long> criteria = createCriteriaCount();

        criteria.select(cb.sum(root.get(TimeSheet_.chargeMinute)))
                .where(
                        cb.equal(root.get(TimeSheet_.status), RecordStatus.ACTIVE),
                        cb.equal(root.get(TimeSheet_.projectTask), projectTask),
                        cb.notEqual(root.get(TimeSheet_.id),updateTimeSheetId)
                );

        Query query = em.createQuery(criteria);

        long result = 0;
        try {
            result = (long) query.getSingleResult();
        } catch (Exception e) {
            log.debug("no record found.");
            return 0L;
        }
        log.debug("sumManDays. (result: {})",result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public long sumActualManDays(ProjectTask projectTask) {
        log.debug("sumActualManDays. (projectTask: {})", projectTask);

        CriteriaQuery<Long> criteria = createCriteriaCount();

        criteria.select(cb.sum(root.get(TimeSheet_.chargeMinute)))
                .where(
                        cb.equal(root.get(TimeSheet_.status), RecordStatus.ACTIVE),
                        cb.equal(root.get(TimeSheet_.projectTask), projectTask)
                );

        Query query = em.createQuery(criteria);

        long result = 0;
        try {
            result = (long) query.getSingleResult();
        } catch (Exception e) {
            log.debug("no record found.");
            return 0L;
        }
        log.debug("sumActualManDays. (result: {})",result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public long sumChargeMinute(User user, Date month) {
        log.debug("sumChargeMinute. (userId: {}, month: {})",user.getId(), month);

        CriteriaQuery<Long> criteria = createCriteriaCount();

        criteria.select(cb.sum(root.get(TimeSheet_.chargeMinute)))
                .where(
                        cb.equal(root.get(TimeSheet_.status), RecordStatus.ACTIVE),
                        cb.equal(root.get(TimeSheet_.user), user),
                        cb.isNotNull(root.get(TimeSheet_.project)),
                        cb.between(root.get(TimeSheet_.workDate),
                                DateTimeUtil.getFirstDateOfMonth(month),
                                DateTimeUtil.getLastDateOfMonth(month))
                );

        Query query = em.createQuery(criteria);

        long result = 0;
        try {
            result = (long) query.getSingleResult();
        } catch (Exception e) {
            log.debug("no record found.");
            return 0L;
        }
        log.debug("sumChargeMinute. (result: {})",result);
        return result;
    }

    public List<TimeSheet> findForMigrateWorkHour() {
        log.debug("findForMigrateWorkHour");
        CriteriaQuery<TimeSheet> criteria = createCriteriaQuery();

        criteria.where(
                cb.notEqual(root.get(TimeSheet_.workHourMinute), 0)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<TimeSheet> timeSheets = query.getResultList();
        log.debug("findForMigrateWorkHour. (result: {})",timeSheets.size());
        return timeSheets;
    }
}
