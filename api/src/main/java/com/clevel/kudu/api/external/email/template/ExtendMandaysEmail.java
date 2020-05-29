package com.clevel.kudu.api.external.email.template;

import com.clevel.kudu.api.exception.EmailException;
import com.clevel.kudu.api.external.email.EmailManager;
import com.clevel.kudu.api.model.SystemConfig;
import com.clevel.kudu.api.model.db.working.*;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class ExtendMandaysEmail extends EmailManager {
    @Inject
    Logger log;

    @Inject
    public ExtendMandaysEmail() {
    }

    @PostConstruct
    public void onCreation() {
        templateName = "extend_mandays";
    }

    public void sendMail(ProjectTask projectTask, ProjectTaskExt projectTaskExt) throws EmailException {
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

        BigDecimal planMD = projectTask.getPlanMD();
        BigDecimal totalExtendMD = projectTask.getExtendMD();
        map.put("PLAN_MANDAYS", planMD.toString());
        map.put("EXTEND_MANDAYS", projectTaskExt.getExtendMD().toString());
        map.put("TOTAL_EXTEND_MANDAYS", totalExtendMD.toString());
        map.put("TOTAL_PLAN_MANDAYS", planMD.add(totalExtendMD).toString());
        log.debug("sendMail.map = {}", map);

        String cc = app.getConfig(SystemConfig.AUTO_EMAIL_CC,"");

        super.sendMail(user.getEmail(), cc, map);
    }

}
