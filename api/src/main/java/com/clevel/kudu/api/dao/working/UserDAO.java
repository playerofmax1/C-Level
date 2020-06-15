package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.model.db.security.Role;
import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.api.model.db.working.User_;
import com.clevel.kudu.model.RecordStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends GenericDAO<User, Long> {
    @Inject
    private Logger log;

    @Inject
    public UserDAO() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public User findById(Long id) throws RecordNotFoundException {
        log.debug("findById. (id: {})", id);

        CriteriaQuery<User> criteria = createCriteriaQuery();

        graph.addAttributeNodes(User_.role);
        graph.addAttributeNodes(User_.rate);

        criteria.where(
                cb.equal(root.get(User_.id), id)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<User> users = query.getResultList();
        if (users.isEmpty()) {
            log.error("user not found! (userId: {})", id);
            throw new RecordNotFoundException("user not found!");
        } else {
            return users.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> findAll() {
        log.debug("findAll.");
        CriteriaQuery<User> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(User_.status), RecordStatus.ACTIVE)
        ).orderBy(cb.asc(root.get(User_.name)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<User> users = query.getResultList();
        log.debug("findAll. (result: {})", users.size());
        return users;
    }

    public List<User> findByRoleList(List<Role> roleList) {
        log.debug("findByRoleList.");
        CriteriaQuery<User> criteria = createCriteriaQuery();

        Expression<Role> parentExpression = root.get(User_.role);
        Predicate roleListPredicate = parentExpression.in(roleList);

        criteria.where(
                cb.and(
                        cb.equal(root.get(User_.status), RecordStatus.ACTIVE),
                        roleListPredicate
                )
        ).orderBy(cb.asc(root.get(User_.role)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<User> users = query.getResultList();
        log.debug("findByRoleList. (result: {})", users.size());
        return users;
    }

    @SuppressWarnings("unchecked")
    public User findByLoginName(String loginName) {
        log.debug("findByLoginName. (loginName: {})", loginName);
        CriteriaQuery<User> criteria = createCriteriaQuery();

        criteria.where(cb.and(
                cb.equal(root.get(User_.loginName), loginName),
                cb.or(
                        cb.equal(root.get(User_.status), RecordStatus.ACTIVE),
                        cb.equal(root.get(User_.status), RecordStatus.FORCE_CHANGE_PWD)
                )
        ));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        User user;
        try {
            user = (User) query.getSingleResult();
        } catch (Exception e) {
            log.debug("{}", e.getMessage());
            return null;
        }
        log.debug("findByLoginNameAndCompany. (user: {})", user);
        return user;
    }

    @SuppressWarnings("unchecked")
    public List<User> searchUser(String name, String lastName, String loginName, String phoneNumber, String email) {
        log.debug("searchUser. ( name: {}, lastName: {}, loginName: {}, phoneNumber: {}, email: {})",
                name, lastName, loginName, phoneNumber, email);
        CriteriaQuery<User> criteria = createCriteriaQuery();

        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(root.get(User_.name), name + "%"));
        }

        if (lastName != null && !lastName.isEmpty()) {
            predicates.add(cb.like(root.get(User_.lastName), lastName + "%"));
        }

        if (loginName != null && !loginName.isEmpty()) {
            predicates.add(cb.like(root.get(User_.loginName), loginName + "%"));
        }

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            predicates.add(cb.like(root.get(User_.phoneNumber), phoneNumber + "%"));
        }

        if (email != null && !email.isEmpty()) {
            predicates.add(cb.like(root.get(User_.email), email + "%"));
        }

        if (predicates.size() == 0) {
            criteria.where(
                    cb.or(cb.equal(root.get(User_.status), RecordStatus.ACTIVE),
                            cb.equal(root.get(User_.status), RecordStatus.FORCE_CHANGE_PWD))
            ).distinct(true);
        } else if (predicates.size() == 1) {
            criteria.where(
                    cb.and(
                            predicates.get(0),
                            cb.or(cb.equal(root.get(User_.status), RecordStatus.ACTIVE),
                                    cb.equal(root.get(User_.status), RecordStatus.FORCE_CHANGE_PWD))
                    )
            ).distinct(true);
        } else {
            criteria.where(
                    cb.and(
                            cb.and(predicates.toArray(new Predicate[0])
                            ),
                            cb.or(cb.equal(root.get(User_.status), RecordStatus.ACTIVE),
                                    cb.equal(root.get(User_.status), RecordStatus.FORCE_CHANGE_PWD))
                    )
            ).distinct(true);
        }

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<User> result = query.getResultList();

        log.debug("searchUser. (result size: {})", result.size());
        return result;
    }

    @SuppressWarnings("unchecked")
    public long getUserCount() {
        log.debug("getUserCount.");

        CriteriaQuery<Long> criteria = createCriteriaCount();

        criteria.select(cb.count(root))
                .where(
                        cb.or(
                                cb.equal(root.get(User_.status), RecordStatus.ACTIVE),
                                cb.equal(root.get(User_.status), RecordStatus.FORCE_CHANGE_PWD)
                        )
                );

        Query query = em.createQuery(criteria);

        return (long) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public long getUserByRoleCount(Role role) {
        log.debug("getUserByRoleCount. (roleId: {})", role.getId());

        CriteriaQuery<Long> criteria = createCriteriaCount();

        criteria.select(cb.count(root))
                .where(cb.and(
                        cb.or(
                                cb.equal(root.get(User_.status), RecordStatus.ACTIVE),
                                cb.equal(root.get(User_.status), RecordStatus.FORCE_CHANGE_PWD)
                        ),
                        cb.equal(root.get(User_.role), role)
                        )
                );

        Query query = em.createQuery(criteria);

        long result = (long) query.getSingleResult();
        log.debug("getUserByRoleCount. (result: {})", result);
        return result;
    }

    public int delete(long id) {
        log.debug("delete. (id: {})", id);

        CriteriaDelete<User> criteria = createCriteriaDelete();

        criteria.where(
                cb.equal(root.get(User_.id), id)
        );

        Query query = em.createQuery(criteria);

        int rows = query.executeUpdate();

        log.debug("delete. (delete record: {})", rows);
        return rows;
    }

}
