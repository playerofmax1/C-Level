package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.model.db.working.*;
import com.clevel.kudu.model.RecordStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import java.util.List;

public class ProjectTaskDAO extends GenericDAO<ProjectTask, Long> {
    @Inject
    private Logger log;

    @Inject
    public ProjectTaskDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<ProjectTask> findByProject(Project project) {
        log.debug("findByProject. (projectId: {})", project.getId());
        CriteriaQuery<ProjectTask> criteria = createCriteriaQuery();

        criteria.where(
                cb.or(
                    cb.equal(root.get(ProjectTask_.status), RecordStatus.ACTIVE),
                    cb.equal(root.get(ProjectTask_.status), RecordStatus.LOCK)
                ),
                cb.equal(root.get(ProjectTask_.project), project)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<ProjectTask> projectTasks = query.getResultList();
        log.debug("findByProject. (result: {})", projectTasks.size());
        return projectTasks;
    }

    @SuppressWarnings("unchecked")
    public List<ProjectTask> findByProjectIdAndUser(long projectId,User user) {
        log.debug("findByProjectIdAndUser. (projectId: {}, userId: {})",projectId,user.getId());
        CriteriaQuery<ProjectTask> criteria = createCriteriaQuery();

        Join<ProjectTask, Task> taskJoin = root.join(ProjectTask_.task);

        criteria.where(
                cb.equal(root.get(ProjectTask_.status), RecordStatus.ACTIVE),
                cb.equal(root.get(ProjectTask_.project), projectId),
                cb.equal(root.get(ProjectTask_.user), user)
        ).orderBy(cb.asc(taskJoin.get(Task_.code)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<ProjectTask> projectTasks = query.getResultList();
        log.debug("findByProjectIdAndUser. (result: {})", projectTasks.size());
        return projectTasks;
    }

    @SuppressWarnings("unchecked")
    public List<ProjectTask> findByUser(User user) {
        log.debug("findByUser. (userId: {})",user.getId());
        CriteriaQuery<ProjectTask> criteria = createCriteriaQuery();

        Join<ProjectTask, Project> projectJoin = root.join(ProjectTask_.project);

        criteria.where(
                cb.equal(root.get(ProjectTask_.status), RecordStatus.ACTIVE),
                cb.equal(root.get(ProjectTask_.user), user),
                cb.equal(projectJoin.get(Project_.status),RecordStatus.ACTIVE)
        ).orderBy(cb.asc(projectJoin.get(Project_.code)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<ProjectTask> projectTasks = query.getResultList();
        log.debug("findByUser. (result: {})", projectTasks.size());
        return projectTasks;
    }

    @SuppressWarnings("unchecked")
    public List<ProjectTask> findByProjectAndUser(Project project, User user) {
        log.debug("findByProjectAndUser. (projectId: {}, userId: {})",project.getId(),user.getId());
        CriteriaQuery<ProjectTask> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(ProjectTask_.status), RecordStatus.ACTIVE),
                cb.equal(root.get(ProjectTask_.user), user),
                cb.equal(root.get(ProjectTask_.project), project)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<ProjectTask> projectTasks = query.getResultList();
        log.debug("findByProjectAndUser. (result: {})", projectTasks.size());
        return projectTasks;
    }

    @SuppressWarnings("unchecked")
    public ProjectTask findProjectTask(User user, long projectTaskId) throws RecordNotFoundException {
        log.debug("findProjectTask. (userId: {}, projectTaskId: {})",user.getId(),projectTaskId);
        CriteriaQuery<ProjectTask> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(ProjectTask_.status), RecordStatus.ACTIVE),
                cb.equal(root.get(ProjectTask_.user), user),
                cb.equal(root.get(ProjectTask_.id), projectTaskId)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<ProjectTask> projectTasks = query.getResultList();
        if (projectTasks.isEmpty()) {
            log.error("project task not found! (userId: {}, projectTaskId: {})",user.getId(),projectTaskId);
            throw new RecordNotFoundException("project task not found!");
        } else {
            log.debug("findProjectTask. (result: {})", projectTasks.get(0));
            return projectTasks.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public ProjectTask findByProjectAndTask(User user, Project project, Task task) throws RecordNotFoundException {
        log.debug("findByProjectAndTask. (userId: {}, projectId: {}, taskId: {})",user.getId(),project.getId(),task.getId());
        CriteriaQuery<ProjectTask> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(ProjectTask_.status), RecordStatus.ACTIVE),
                cb.equal(root.get(ProjectTask_.user), user),
                cb.equal(root.get(ProjectTask_.project), project),
                cb.equal(root.get(ProjectTask_.task), task)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<ProjectTask> projectTasks = query.getResultList();
        if (projectTasks.isEmpty()) {
            log.error("project task not found! (userId: {}, projectId: {}, taskId: {})",
                    user.getId(),project.getId(),task.getId());
            throw new RecordNotFoundException("project task not found!");
        } else {
            log.debug("findProjectTask. (result: {})", projectTasks.get(0));
            return projectTasks.get(0);
        }
    }

}
