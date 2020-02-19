package com.clevel.kudu.api.dao.system;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.system.SecurityAudit;
import org.slf4j.Logger;

import javax.inject.Inject;

public class SecurityAuditDAO extends GenericDAO<SecurityAudit,Long> {
    @Inject
    private Logger log;

    @Inject
    public SecurityAuditDAO() {
    }
}
