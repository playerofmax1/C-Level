package com.clevel.kudu.api.dao.relation;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.relation.RelRoleFunction;
import com.clevel.kudu.api.model.db.relation.RelRoleFunction_;
import com.clevel.kudu.api.model.db.security.Role;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class RelRoleFunctionDAO extends GenericDAO<RelRoleFunction, Long> {
    @Inject
    private Logger log;

    @Inject
    public RelRoleFunctionDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<RelRoleFunction> getListByRole(Role role) {
        log.debug("getListByRole. (roleId: {})",role.getId());
        CriteriaQuery<RelRoleFunction> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(RelRoleFunction_.role), role)
        );

        Query query = em.createQuery(criteria);
        List<RelRoleFunction> result = query.getResultList();

        log.debug("getListByRole. (result: {})", result.size());
        return result;
    }

    public void deleteRole(Role role) {
        log.debug("deleteRole. (roleId: {})", role.getId());
        CriteriaDelete<RelRoleFunction> criteria = createCriteriaDelete();

        criteria.where(
                cb.and(cb.equal(root.get(RelRoleFunction_.role), role))
        );

        Query query = em.createQuery(criteria);

        int result = query.executeUpdate();

        log.debug("deleteRole. (result: {})", result);
    }
}
