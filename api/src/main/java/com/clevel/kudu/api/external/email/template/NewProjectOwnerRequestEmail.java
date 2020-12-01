package com.clevel.kudu.api.external.email.template;

import com.clevel.kudu.api.exception.EmailException;
import com.clevel.kudu.api.external.email.EmailManager;
import com.clevel.kudu.api.model.db.working.Project;
import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.model.SystemConfig;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class NewProjectOwnerRequestEmail extends EmailManager {
    @Inject
    Logger log;

    @Inject
    public NewProjectOwnerRequestEmail() {
    }

    @PostConstruct
    public void onCreation() {
        templateName = "newprojectowner";
    }

    public void sendMail( Project project, List<User> approverList) throws EmailException {
        Map<String, String> map = new HashMap<>();
        User user = project.getUser();

        String description;
//        Task task;
        description = project.getDescription();

        String projectCode = project.getCode();

        map.put("USER_NAME", user.getName() + " " + user.getLastName());
//        map.put("TYPE", type.name());
        map.put("PROJECT_CODE", projectCode);
        map.put("PROJECT_NAME", project.getName());
//        map.put("TASK_CODE", task.getCode());
//        map.put("TASK_NAME", task.getName());
        map.put("TASK_DESCRIPTION", description);
//        map.put("PLAN_MANDAYS", mandaysRequest.getExtendMD().toString());
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
