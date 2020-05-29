package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.api.model.db.working.UserTimeSheet;
import com.clevel.kudu.api.model.db.working.UserTimeSheet_;
import com.clevel.kudu.api.model.db.working.User_;
import com.clevel.kudu.model.RecordStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import java.util.List;

public class UserTimeSheetDAO extends GenericDAO<UserTimeSheet, Long> {
    @Inject
    private Logger log;

    @Inject
    public UserTimeSheetDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<UserTimeSheet> findByUser(User user) {
        log.debug("findByUser. (user: {})", user);
        CriteriaQuery<UserTimeSheet> criteria = createCriteriaQuery();

        graph.addAttributeNodes(UserTimeSheet_.timeSheetUser);

        Join<UserTimeSheet, User> userTimeSheetUserJoin = root.join(UserTimeSheet_.timeSheetUser);

        criteria.where(
                cb.and(
                        cb.equal(root.get(UserTimeSheet_.user), user),
                        cb.equal(userTimeSheetUserJoin.get(User_.status), RecordStatus.ACTIVE)
                )
        ).orderBy(cb.asc(userTimeSheetUserJoin.get(User_.name)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<UserTimeSheet> userTimeSheets = query.getResultList();
        log.debug("findByUser. (result: {})", userTimeSheets.size());
        return userTimeSheets;
    }

    public void deleteUserTimeSheet(User user) {
        log.debug("deleteUserTimeSheet. (userId: {})", user.getId());
        CriteriaDelete<UserTimeSheet> criteria = createCriteriaDelete();

        criteria.where(
                cb.and(cb.equal(root.get(UserTimeSheet_.user), user))
        );

        Query query = em.createQuery(criteria);

        int result = query.executeUpdate();

        log.debug("deleteUserTimeSheet. (result: {})", result);
    }

}
