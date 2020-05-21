package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.view.UserMandays;
import com.clevel.kudu.api.model.db.view.UserMandays_;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

public class UserMandaysDAO extends GenericDAO<UserMandays, Long> {
    @Inject
    private Logger log;

    @Inject
    public UserMandaysDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<UserMandays> findByYear(long userId, int year) {
        log.debug("findByYear.");
        CriteriaQuery<UserMandays> criteria = createCriteriaQuery();

        criteria.where(
                cb.and(
                        cb.equal(root.get(UserMandays_.workYear), year),
                        cb.equal(root.get(UserMandays_.userId), userId)
                )
        ).orderBy(cb.asc(root.get(UserMandays_.project)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<UserMandays> userMandaysList = query.getResultList();
        if (userMandaysList.isEmpty()) {
            log.debug("UserMandays for user({}) year({}) not found!", userId, year);
            return new ArrayList<>();
        } else {
            return userMandaysList;
        }
    }
}
