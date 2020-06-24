package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.view.UserMandays;
import com.clevel.kudu.api.model.db.view.UserMandays_;
import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.api.model.db.working.User_;
import com.clevel.kudu.model.RecordStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
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
                        cb.equal(root.get(UserMandays_.user), userId)
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

    @SuppressWarnings("unchecked")
    public List<UserMandays> findByYear(int year) {
        log.debug("findByYear.");
        CriteriaQuery<UserMandays> criteria = createCriteriaQuery();

        Join<UserMandays, User> userJoin = root.join(UserMandays_.user);

        criteria.where(
                cb.and(
                        cb.equal(root.get(UserMandays_.workYear), year),
                        cb.equal(userJoin.get(User_.status), RecordStatus.ACTIVE)
                )
        ).orderBy(cb.asc(userJoin.get(User_.name)), cb.asc(root.get(UserMandays_.project)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<UserMandays> userMandaysList = query.getResultList();
        if (userMandaysList.isEmpty()) {
            log.debug("UserMandays for year({}) not found!", year);
            return new ArrayList<>();
        } else {
            return userMandaysList;
        }
    }

    @SuppressWarnings("unchecked")
    public List<UserMandays> findByUserList(List<User> viewableUserList, int year) {
        log.debug("findByUserList.");
        CriteriaQuery<UserMandays> criteria = createCriteriaQuery();

        Expression<User> parentExpression = root.get(UserMandays_.user);
        Predicate userListPredicate = parentExpression.in(viewableUserList);

        criteria.where(
                cb.and(
                        cb.equal(root.get(UserMandays_.workYear), year),
                        userListPredicate
                )
        ).orderBy(cb.asc(root.get(UserMandays_.user)), cb.asc(root.get(UserMandays_.project)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<UserMandays> userMandaysList = query.getResultList();
        if (userMandaysList.isEmpty()) {
            log.debug("UserMandays for year({}) userList({}) not found!", year, viewableUserList);
            return new ArrayList<>();
        } else {
            return userMandaysList;
        }
    }

}
