package com.clevel.kudu.front.system;

import com.clevel.dconvers.conf.VersionConfigFile;
import com.clevel.dconvers.format.VersionFormatter;

import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
@Named
public class Application {
    @Inject
    private Logger log;

    private String versionString;

    @Inject
    public Application() {
    }

    @PostConstruct
    public void onCreation() {
        log.trace("onCreation.");
        loadApplicationVersion();
    }

    private void loadApplicationVersion() {
        log.trace("loadApplicationVersion.");
        VersionFormatter formatter = new VersionFormatter();
        VersionConfigFile currentVersion = formatter.versionConfigFile("version.property");
        versionString = formatter.versionString(currentVersion);
        log.debug("loadApplicationVersion.currentVersion = {}", versionString);
    }

    public String getVersionString() {
        return versionString;
    }
}
