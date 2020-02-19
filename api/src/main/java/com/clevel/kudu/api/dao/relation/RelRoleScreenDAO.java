package com.clevel.kudu.api.dao.relation;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.relation.RelRoleScreen;
import com.clevel.kudu.api.model.db.relation.RelRoleScreen_;
import com.clevel.kudu.api.model.db.security.Role;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class RelRoleScreenDAO extends GenericDAO<RelRoleScreen, Long> {
    @Inject
    private Logger log;

    @Inject
    public RelRoleScreenDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<RelRoleScreen> getListByRole(Role role) {
        log.debug("getListByRole. (roleId: {})", role.getId());
        CriteriaQuery<RelRoleScreen> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(RelRoleScreen_.role), role)
        );

        Query query = em.createQuery(criteria);
        List<RelRoleScreen> result = query.getResultList();

        log.debug("getListByRole. (result: {})", result.size());
        return result;
    }

    public void deleteRole(Role role) {
        log.debug("deleteRole. (roleId: {})", role.getId());
        CriteriaDelete<RelRoleScreen> criteria = createCriteriaDelete();

        criteria.where(
                cb.and(cb.equal(root.get(RelRoleScreen_.role), role))
        );

        Query query = em.createQuery(criteria);

        int result = query.executeUpdate();

        log.debug("deleteRole. (result: {})", result);
    }

}
