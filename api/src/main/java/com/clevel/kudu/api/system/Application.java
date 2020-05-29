package com.clevel.kudu.api.system;

import com.clevel.kudu.api.model.SystemConfig;
import com.clevel.kudu.api.model.db.system.Config;
import com.clevel.kudu.util.Util;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@Named
public class Application {
    @Inject
    private Logger log;
    private String realPath;
    private Map<SystemConfig,String> configMap;

    @Inject
    SystemManager systemManager;

    @Inject
    public Application() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");
    }

    public void loadConfiguration() {
        log.debug("loadConfiguration.");
        List<Config> configList = systemManager.loadConfiguration();
        configMap = new ConcurrentHashMap<>();
        for (Config config : configList) {
            configMap.put(SystemConfig.lookup(config.getName()), config.getValue());
        }
        log.debug("===== current configuration set (count: {}) =====", configMap.size());
        Util.listFields(configMap);
        log.debug("===== current configuration set (count: {}) =====", configMap.size());
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public String getConfig(SystemConfig config) {
        return configMap.get(config);
    }

    public String getConfig(SystemConfig config, String defaultValue) {
        String value = configMap.get(config);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
