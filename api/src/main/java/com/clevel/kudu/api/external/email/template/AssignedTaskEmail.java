package com.clevel.kudu.api.external.email.template;

import com.clevel.kudu.api.exception.EmailException;
import com.clevel.kudu.api.external.email.EmailManager;
import com.clevel.kudu.api.model.db.working.*;
import com.clevel.kudu.model.SystemConfig;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class AssignedTaskEmail extends EmailManager {
    @Inject
    Logger log;

    @Inject
    public AssignedTaskEmail() {
    }

    @PostConstruct
    public void onCreation() {
        templateName = "assigned_task";
    }

    public void sendMail(ProjectTask projectTask, MandaysRequest mandaysRequest) throws EmailException {
        Map<String, String> map = new HashMap<>();
        User user = projectTask.getUser();
        Project project = projectTask.getProject();
        Task task = projectTask.getTask();
        String projectCode = project.getCode();

        String description = projectTask.getDescription();
        String comment = null;
        if (mandaysRequest != null) {
            comment = mandaysRequest.getComment();
            if (comment != null && !comment.isEmpty()) {
                String modifiedUser = mandaysRequest.getModifyBy().getName();
                comment = "Commented by " + modifiedUser + ": " + comment;
            }
        }
        if (comment == null) {
            comment = "";
        }

        map.put("USER_NAME", user.getName() + " " + user.getLastName());
        map.put("PROJECT_CODE", projectCode);
        map.put("PROJECT_NAME", project.getName());
        map.put("TASK_CODE", task.getCode());
        map.put("TASK_NAME", task.getName());
        map.put("TASK_DESCRIPTION", description);
        map.put("TASK_COMMENT", comment);
        map.put("PLAN_MANDAYS", projectTask.getPlanMD().toString());
        log.debug("sendMail.map = {}", map);

        String cc = app.getConfig(SystemConfig.AUTO_EMAIL_CC, "");

        super.sendMail(user.getEmail(), cc, map);
    }

}
