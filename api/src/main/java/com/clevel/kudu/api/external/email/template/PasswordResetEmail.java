package com.clevel.kudu.api.external.email.template;

import com.clevel.kudu.api.exception.EmailException;
import com.clevel.kudu.api.external.email.EmailManager;
import com.clevel.kudu.api.model.db.working.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class PasswordResetEmail extends EmailManager {
    @Inject
    Logger log;

    @Inject
    public PasswordResetEmail() {
    }

    @PostConstruct
    public void onCreation() {
        templateName = "password_reset";
    }

    public void sendMail(User user, String newPassword) throws EmailException {
        if (StringUtils.isBlank(user.getEmail())) {
            log.error("invalid email address!");
            throw new EmailException("invalid email address!");
        }

        Map<String, String> map = new HashMap<>();
        map.put("USER_NAME", user.getName() + " " + user.getLastName());
        map.put("PASSWORD", newPassword);

        super.sendMail(user.getEmail(),"", map);
    }

}
