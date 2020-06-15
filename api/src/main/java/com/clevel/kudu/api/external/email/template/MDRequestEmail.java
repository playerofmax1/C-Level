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
import java.util.List;
import java.util.Map;

@Stateless
public class MDRequestEmail extends EmailManager {
    @Inject
    Logger log;

    @Inject
    public MDRequestEmail() {
    }

    @PostConstruct
    public void onCreation() {
        templateName = "mdrequest";
    }

    public void sendMail(MandaysRequestType type, MandaysRequest mandaysRequest, List<User> approverList) throws EmailException {
        Map<String, String> map = new HashMap<>();
        User user = mandaysRequest.getUser();

        String description;
        Project project;
        Task task;
        if (MandaysRequestType.NEW.equals(type)) {
            description = mandaysRequest.getDescription();
            project = mandaysRequest.getProject();
            task = mandaysRequest.getTask();
        } else /*EXTEND*/ {
            ProjectTask projectTask = mandaysRequest.getProjectTask();
            description = projectTask.getDescription();
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
        map.put("TASK_DESCRIPTION", description);
        map.put("PLAN_MANDAYS", mandaysRequest.getExtendMD().toString());
        log.debug("sendMail.map = {}", map);

        String to = "";
        for (User approver : approverList) {
            to += ","+ approver.getEmail();
        }
        to = to.substring(1);

        String cc = app.getConfig(SystemConfig.AUTO_EMAIL_CC, "");

        super.sendMail(to, cc, map);
    }

}
