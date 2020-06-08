package com.clevel.kudu.front.controller;

import com.clevel.kudu.model.CurrentUser;
import com.clevel.kudu.model.Function;
import com.clevel.kudu.model.Screen;
import com.clevel.kudu.model.UserDetail;
import com.clevel.kudu.util.FacesUtil;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@ViewScoped
@Named("accessCtl")
public class PageAccessControl extends AbstractController {
    @Inject
    @CurrentUser
    UserDetail userDetail;

    public PageAccessControl() {
    }

    public void onCreation() {
        log.debug("onCreation.");
    }

    public void checkAccess(Screen screen) {
        if (!userDetail.getScreenList().contains(screen)) {
            log.debug("access denied.");
            FacesUtil.redirect("/site/welcome.jsf");
        }
    }

    public boolean functionEnable(Function function) {
        return userDetail.getFunctionList().contains(function);
    }

    public boolean isVisible(Screen screen) {
        return userDetail.getScreenList().contains(screen);
    }

    public boolean isVisible(Screen... screens) {
        List<Screen> screenList = userDetail.getScreenList();
        for (Screen screen : screens) {
            if (screenList.contains(screen)) {
                return true;
            }
        }
        return false;
    }

}
