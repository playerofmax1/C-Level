package com.clevel.kudu.api.external.email.template;

import com.clevel.kudu.api.exception.EmailException;
import com.clevel.kudu.api.external.email.EmailManager;
import com.clevel.kudu.api.model.SystemConfig;
import com.clevel.kudu.api.model.db.working.Project;
import com.clevel.kudu.api.model.db.working.ProjectTask;
import com.clevel.kudu.api.model.db.working.Task;
import com.clevel.kudu.api.model.db.working.User;
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
        emailTemplate = "/WEB-INF/email/assigned_task.html";
    }

    public void sendMail(ProjectTask projectTask) throws EmailException {
        Map<String, String> map = new HashMap<>();
        User user = projectTask.getUser();
        Project project = projectTask.getProject();
        Task task = projectTask.getTask();
        String projectCode = project.getCode();

        map.put("USER_NAME", user.getName() + " " + user.getLastName());
        map.put("PROJECT_CODE", projectCode);
        map.put("PROJECT_NAME", project.getName());
        map.put("TASK_CODE", task.getCode());
        map.put("TASK_NAME", task.getName());
        map.put("TASK_DESCRIPTION", projectTask.getDescription());
        map.put("PLAN_MANDAYS", projectTask.getPlanMD().toString());
        log.debug("sendMail.map = {}", map);

        String cc = app.getConfig(SystemConfig.AUTO_EMAIL_CC);
        if (cc == null) {
            cc = "";
        }

        super.sendMail(user.getEmail(), projectCode + ": You got new task", cc, map);
    }

}
