package com.clevel.kudu.api.external.email.template;

import com.clevel.kudu.api.external.email.EmailManager;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class PasswordResetEmail extends EmailManager {
    @Inject
    Logger log;

    @Inject
    public PasswordResetEmail() {
    }

    @PostConstruct
    public void onCreation() {
        emailTemplate = "/WEB-INF/email/password_reset.html";
    }

}
