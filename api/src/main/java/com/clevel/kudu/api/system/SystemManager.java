package com.clevel.kudu.api.system;

import com.clevel.kudu.api.dao.system.ConfigDAO;
import com.clevel.kudu.api.dao.system.SecurityAuditDAO;
import com.clevel.kudu.api.model.db.system.Config;
import com.clevel.kudu.api.model.db.system.SecurityAudit;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class SystemManager {
    @Inject
    private Logger log;

    @Inject
    private ConfigDAO configDAO;

    @Inject
    SecurityAuditDAO securityAuditDAO;

    @Inject
    public SystemManager() {
    }

    public List<Config> loadConfiguration() {
        return configDAO.findAll();
    }

    public void audit(String requestURL, String clientIP, String userAgent, String referer, String sessionId, String request) {
        SecurityAudit audit = new SecurityAudit(requestURL,clientIP,userAgent,referer,sessionId,request);
        securityAuditDAO.persist(audit);
    }
}
