package com.clevel.kudu.api.external.email.template;

import com.clevel.kudu.api.exception.EmailException;
import com.clevel.kudu.api.external.email.EmailManager;
import com.clevel.kudu.api.model.db.working.*;
import com.clevel.kudu.model.MandaysRequestType;
import com.clevel.kudu.model.SystemConfig;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class RejectedMDRequestEmail extends EmailManager {
    @Inject
    Logger log;

    @Inject
    public RejectedMDRequestEmail() {
    }

    @PostConstruct
    public void onCreation() {
        templateName = "reject_mdrequest";
    }

    public void sendMail(MandaysRequestType type, MandaysRequest mandaysRequest) throws EmailException {
        Map<String, String> map = new HashMap<>();
        User user = mandaysRequest.getUser();

        Project project;
        Task task;
        if (MandaysRequestType.NEW.equals(type)) {
            project = mandaysRequest.getProject();
            task = mandaysRequest.getTask();
        } else /*EXTEND*/ {
            ProjectTask projectTask = mandaysRequest.getProjectTask();
            project = projectTask.getProject();
            task = projectTask.getTask();
        }

        String projectCode = project.getCode();

        map.put("USER_NAME", user.getName() + " " + user.getLastName());
        map.put("TYPE", type.name());
        map.put("PROJECT_CODE", projectCode);
        map.put("PROJECT_NAME", project.getName());
        map.put("TASK_CODE", task.getCode());
        map.put("TASK_NAME", task.getName());
        map.put("TASK_DESCRIPTION", task.getDescription());
        map.put("DESCRIPTION", mandaysRequest.getDescription());
        map.put("PLAN_MANDAYS", mandaysRequest.getExtendMD().toString());
        log.debug("sendMail.map = {}", map);

        String cc = app.getConfig(SystemConfig.AUTO_EMAIL_CC, "");

        super.sendMail(user.getEmail(), cc, map);
    }

}
