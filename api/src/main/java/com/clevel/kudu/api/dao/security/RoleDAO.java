package com.clevel.kudu.api.dao.security;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.security.Role;
import com.clevel.kudu.api.model.db.security.Role_;
import com.clevel.kudu.model.RecordStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class RoleDAO extends GenericDAO<Role, Long> {
    @Inject
    private Logger log;

    @Inject
    public RoleDAO() {
    }

    @SuppressWarnings("unchecked")
    public boolean isRoleCodeExist(String code) {
        log.debug("isRoleCodeExist. (code: {})", code);
        CriteriaQuery<Role> criteria = createCriteriaQuery();

        criteria.where(
                cb.and(
                        cb.equal(root.get(Role_.code), code),
                        cb.equal(root.get(Role_.status), RecordStatus.ACTIVE)
                )
        );

        Query query = em.createQuery(criteria);

        List<Role> list;
        list = query.getResultList();
        log.debug("isRoleCodeExist. (code: {}, result size: {})",code, ((list.isEmpty())?0:list.size()));
        return !list.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public List<Role> findAll() {
        log.debug("findAll.");

        CriteriaQuery<Role> criteria = createCriteriaQuery();

        criteria.where(
                cb.and(
                        cb.equal(root.get(Role_.status), RecordStatus.ACTIVE))
        ).orderBy(
                cb.asc(root.get(Role_.code))
        );
        Query query = em.createQuery(criteria);

        List<Role> result = query.getResultList();

        log.debug("findAll. (result: {})", result.size());
        return result;
    }

}
