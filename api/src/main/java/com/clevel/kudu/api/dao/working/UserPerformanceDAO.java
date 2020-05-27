package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.working.PerformanceYear;
import com.clevel.kudu.api.model.db.working.PerformanceYear_;
import com.clevel.kudu.api.model.db.working.UserPerformance;
import com.clevel.kudu.api.model.db.working.UserPerformance_;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import java.util.List;

public class UserPerformanceDAO extends GenericDAO<UserPerformance, Long> {

    @Inject
    private Logger log;

    @Inject
    public UserPerformanceDAO() {
    }
    
    @SuppressWarnings("unchecked")
    public UserPerformance findByYear(long userId, int year) {
        log.debug("findByYear.");
        CriteriaQuery<UserPerformance> criteria = createCriteriaQuery();
        Join<UserPerformance, PerformanceYear> performanceYearJoin = root.join(UserPerformance_.performanceYear);

        criteria.where(
                cb.and(
                        cb.equal(root.get(UserPerformance_.userId), userId),
                        cb.equal(performanceYearJoin.get(PerformanceYear_.year), year)
                )
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<UserPerformance> UserPerformanceList = query.getResultList();
        if (UserPerformanceList.isEmpty()) {
            log.debug("UserPerformance for user({}) year({}) not found!", userId, year);
            return null;
        } else {
            return UserPerformanceList.get(0);
        }
    }

}
