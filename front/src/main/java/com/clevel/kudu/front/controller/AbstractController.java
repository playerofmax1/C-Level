package com.clevel.kudu.front.controller;

import com.clevel.dconvers.conf.VersionConfigFile;
import com.clevel.kudu.front.system.Application;
import com.clevel.kudu.model.CurrentUser;
import com.clevel.kudu.model.UserDetail;
import com.clevel.kudu.resource.APIService;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.Serializable;

public class AbstractController implements Serializable {
    @Inject
    protected Logger log;

    @Inject
    protected APIService apiService;

    @Inject
    @CurrentUser
    protected UserDetail userDetail;

    @Inject
    protected Application application;

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public VersionConfigFile getApplicationVersion() {
        return application.getVersion();
    }

    public String getApplicationVersionString() {
        return application.getVersionString();
    }

    public String getCssFileName() {
        return application.getCssFileName();
    }
}