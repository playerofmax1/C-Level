package com.clevel.kudu.api.system;

import com.clevel.kudu.api.dao.system.ConfigDAO;
import com.clevel.kudu.api.dao.system.SecurityAuditDAO;
import com.clevel.kudu.api.model.db.system.Config;
import com.clevel.kudu.api.model.db.system.SecurityAudit;
import com.clevel.kudu.api.rest.mapper.ConfigMapper;
import com.clevel.kudu.dto.working.ConfigDTO;
import com.clevel.kudu.model.SystemConfig;
import com.clevel.kudu.util.LookupUtil;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SystemManager {
    @Inject
    private Logger log;

    @Inject
    private ConfigDAO configDAO;

    @Inject
    private SecurityAuditDAO securityAuditDAO;

    @Inject
    private ConfigMapper configMapper;

    @Inject
    public SystemManager() {
    }

    public List<Config> loadConfiguration() {
        return configDAO.findAll();
    }

    public List<ConfigDTO> getConfigList() {
        log.debug("getConfigList");
        return configMapper.toDTO(configDAO.findAll().stream());
    }

    public List<ConfigDTO> getConfigList(List<SystemConfig> systemConfigList) {
        log.debug("getConfigList(systemConfigList: {})", systemConfigList);

        List<String> configNameList = new ArrayList<>();
        systemConfigList.forEach(systemConfig -> configNameList.add(systemConfig.getCode()));

        return configMapper.toDTO(configDAO.findByList(configNameList).stream());
    }

    public void audit(String requestURL, String clientIP, String userAgent, String referer, String sessionId, String request) {
        SecurityAudit audit = new SecurityAudit(requestURL, clientIP, userAgent, referer, sessionId, request);
        securityAuditDAO.persist(audit);
    }

    public List<ConfigDTO> saveConfigList(List<ConfigDTO> configDTOList) {
        List<Config> updateList = new ArrayList<>();
        List<Config> configList = configDAO.findAll();
        Config updateConfig;
        ConfigDTO configDTO;

        for (Config config : configList) {
            configDTO = LookupUtil.getObjById(configDTOList, config.getId());
            updateConfig = configMapper.updateFromDTO(configDTO, config);
            updateList.add(updateConfig);
        }

        configDAO.persist(configList);
        return configMapper.toDTO(configList.stream());
    }
}
