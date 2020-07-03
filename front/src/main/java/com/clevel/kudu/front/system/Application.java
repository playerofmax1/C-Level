package com.clevel.kudu.front.system;

import com.clevel.dconvers.conf.VersionConfigFile;
import com.clevel.dconvers.format.VersionFormatter;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.working.ConfigDTO;
import com.clevel.kudu.model.KuduEvent;
import com.clevel.kudu.model.KuduEventListener;
import com.clevel.kudu.model.SystemConfig;
import com.clevel.kudu.resource.APIService;
import com.clevel.kudu.util.DateTimeUtil;
import com.clevel.kudu.util.FacesUtil;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ApplicationScoped
@Named
public class Application {
    @Inject
    private Logger log;

    @Inject
    protected APIService apiService;

    private HashMap<KuduEvent, List<KuduEventListener>> eventListenerMap;

    private VersionConfigFile version;
    private String versionString;

    private boolean cssForceReload;
    private String cssFileName;

    private int currentYear;

    private String appPath;

    @Inject
    public Application() {
    }

    @PostConstruct
    public void onCreation() {
        log.trace("onCreation.");

        initKuduEvent();
        fireEvent(KuduEvent.SYSTEM_CONFIG_CHANGED);
    }

    private void initKuduEvent() {/*  */
        eventListenerMap = new HashMap<>();

        addEventListener(KuduEvent.SYSTEM_CONFIG_CHANGED, new KuduEventListener() {
            public void eventPerformed(Object... params) {
                loadSystemConfig();
                loadApplicationVersion();
            }
        });
    }

    public void addEventListener(KuduEvent event, KuduEventListener eventListener) {
        List<KuduEventListener> eventListenerList = eventListenerMap.computeIfAbsent(event, k -> new ArrayList<>());
        eventListenerList.add(eventListener);
    }

    public void fireEvent(KuduEvent event, Object... params) {
        List<KuduEventListener> eventListenerList = eventListenerMap.get(event);
        for (KuduEventListener eventListener : eventListenerList) {
            eventListener.eventPerformed(params);
        }
    }

    private void loadSystemConfig() {
        List<SystemConfig> configNameList = new ArrayList<>();
        configNameList.add(SystemConfig.FORCE_RELOAD_CSS);
        configNameList.add(SystemConfig.PF_YEAR);

        ServiceRequest<List<SystemConfig>> request = new ServiceRequest<>(configNameList);
        Response response = apiService.getSystemResource().getSpecifiedConfigList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ConfigDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ConfigDTO>>>() {
            });
            List<ConfigDTO> configDTOList = serviceResponse.getResult();
            log.debug("configDTOList: {}", configDTOList);
            setConfigVariable(configDTOList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
            setConfigVariable(null);
        }
    }

    private void setConfigVariable(List<ConfigDTO> configDTOList) {
        /*defaults*/
        currentYear = DateTimeUtil.getYear(DateTimeUtil.currentDate());
        cssForceReload = true;

        if (configDTOList == null) {
            return;
        }

        for (ConfigDTO configDTO : configDTOList) {
            if (SystemConfig.PF_YEAR.equals(configDTO.getSystemConfig())) {
                currentYear = Integer.parseInt(configDTO.getValue());
            } else if(SystemConfig.FORCE_RELOAD_CSS.equals(configDTO.getSystemConfig())) {
                cssForceReload = Boolean.parseBoolean(configDTO.getValue());
            }
        }
    }

    private void loadApplicationVersion() {
        log.trace("loadApplicationVersion.");
        VersionFormatter formatter = new VersionFormatter();
        version = formatter.versionConfigFile("version.property");
        versionString = formatter.versionString(version);
        log.debug("loadApplicationVersion.version = {}", versionString);

        if (cssForceReload) {
            cssFileName = "kudu." + version.getVersionNumber() + "." + version.getRevisionNumber() + "." + version.getBuildNumber() + ".css";
        } else {
            cssFileName = "kudu.css";
        }
        log.debug("cssFileName = {}", cssFileName);
    }

    public String getVersionString() {
        return versionString;
    }

    public VersionConfigFile getVersion() {
        return version;
    }

    public String getCssFileName() {
        return cssFileName;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public int getCurrentYear() {
        return currentYear;
    }
}
