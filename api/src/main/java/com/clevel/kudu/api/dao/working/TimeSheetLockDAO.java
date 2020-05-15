package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.working.*;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Date;
import java.util.List;

public class TimeSheetLockDAO extends GenericDAO<TimeSheetLock, Long> {
    @Inject
    private Logger log;

    @Inject
    public TimeSheetLockDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<TimeSheetLock> findByDate(User user, Date date) {
        log.debug("findByDate(user: {}, date: {})", user, date);
        CriteriaQuery<TimeSheetLock> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(TimeSheetLock_.user), user),
                cb.lessThanOrEqualTo(root.get(TimeSheetLock_.startDate), date),
                cb.greaterThanOrEqualTo(root.get(TimeSheetLock_.endDate), date)
        ).orderBy(
                cb.asc(root.get(TimeSheetLock_.startDate))
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<TimeSheetLock> timeSheetLockList = query.getResultList();
        log.debug("findByDate. (result: {})", timeSheetLockList.size());
        return timeSheetLockList;
    }

    public void delete(List<TimeSheetLock> timeSheetLockList) {

        for (TimeSheetLock timeSheetLock : timeSheetLockList) {

            log.debug("delete. (timeSheetLock: {})", timeSheetLock.getId());
            CriteriaDelete<TimeSheetLock> criteria = createCriteriaDelete();

            /*TODO: need to use 'cb.in()' instead of for loop to improve performance when the feature LockTimeSheet day by day is added*/
            criteria.where(
                    cb.and(cb.equal(root.get(TimeSheetLock_.id), timeSheetLock.getId()))
            );
            Query query = em.createQuery(criteria);
            int result = query.executeUpdate();
            log.debug("delete. (result: {})", result);
        }

    }
}
