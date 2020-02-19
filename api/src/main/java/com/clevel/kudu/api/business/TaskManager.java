package com.clevel.kudu.api.business;

import com.clevel.kudu.api.dao.working.TaskDAO;
import com.clevel.kudu.api.dao.working.UserDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.model.db.working.Task;
import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.api.rest.mapper.TaskMapper;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.working.TaskDTO;
import com.clevel.kudu.model.RecordStatus;
import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Stateless
public class TaskManager {
    @Inject
    private Logger log;
    @Inject
    private Application app;
    @Inject
    private TaskDAO taskDAO;
    @Inject
    private UserDAO userDAO;
    @Inject
    private TaskMapper taskMapper;

    @Inject
    public TaskManager() {
    }

    public TaskDTO createNewTask(long userId, TaskDTO taskDTO) throws RecordNotFoundException, ValidationException {
        log.debug("createNewTask. (userId: {}, taskDTO: {})", userId, taskDTO);

        User user = userDAO.findById(userId);

        Task newTask = taskMapper.toEntity(taskDTO);
        log.debug("newTask: {}", newTask);

        Date now = DateTimeUtil.now();
        newTask.setCreateDate(now);
        newTask.setCreateBy(user);
        newTask.setModifyDate(now);
        newTask.setModifyBy(user);
        newTask.setStatus(RecordStatus.ACTIVE);

        newTask = taskDAO.persist(newTask);

        return taskMapper.toDTO(newTask);
    }

    public TaskDTO getTaskInfo(long userId, long taskId) throws RecordNotFoundException {
        log.debug("getTaskInfo. (userId: {}, taskId: {})", userId, taskId);

        // validate user
        userDAO.findById(userId);

        Task task = taskDAO.findById(taskId);

        return taskMapper.toDTO(task);
    }

    public void updateTaskInfo(long userId, TaskDTO taskDTO) throws RecordNotFoundException {
        log.debug("updateTaskInfo. (userId: {}, taskDTO: {})", userId, taskDTO);

        // validate user
        User user = userDAO.findById(userId);

        Task task = taskDAO.findById(taskDTO.getId());
        Task after = taskMapper.updateFromDTO(taskDTO, task);

        after.setModifyDate(DateTimeUtil.now());
        after.setModifyBy(user);

//        log.debug("AFTER: {}",after);
        taskDAO.merge(after);
    }

    public List<TaskDTO> getTaskList(long userId, boolean chargeable, boolean nonChargeable, boolean all) throws RecordNotFoundException {
        log.debug("getTaskList. (userId: {}, chargeable: {}, nonChargeable: {}, all: {})",
                userId,chargeable,nonChargeable,all);

        // validate user
        User user = userDAO.findById(userId);

        if (chargeable) {
            return taskMapper.toDTO(taskDAO.findAll(true).stream());
        }

        if (nonChargeable) {
            return taskMapper.toDTO(taskDAO.findAll(false).stream());
        }

        if (all) {
            return taskMapper.toDTO(taskDAO.findAll().stream());
        }

        return Collections.emptyList();
    }

    public void deleteTask(long userId, TaskDTO taskDTO) throws RecordNotFoundException {
        log.debug("deleteTask. (userId: {}, taskDTO: {})",userId,taskDTO);

        User user = userDAO.findById(userId);
        Task task = taskDAO.findById(taskDTO.getId());

        task.setStatus(RecordStatus.INACTIVE);
        task.setModifyDate(DateTimeUtil.now());
        task.setModifyBy(user);

        taskDAO.persist(task);
    }

}
