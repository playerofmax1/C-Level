package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.working.*;
import com.clevel.kudu.model.RecordStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;

public class ProjectTaskExtDAO extends GenericDAO<ProjectTaskExt, Long> {
    @Inject
    private Logger log;

    @Inject
    public ProjectTaskExtDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<ProjectTaskExt> findByProjectTaskId(long projectTaskId) {
        log.debug("findByProjectTaskId. (projectTaskId: {})",projectTaskId);
        CriteriaQuery<ProjectTaskExt> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(ProjectTaskExt_.status), RecordStatus.ACTIVE),
                cb.equal(root.get(ProjectTaskExt_.parent), projectTaskId)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<ProjectTaskExt> projectTaskExtList = query.getResultList();
        log.debug("findByProjectTaskId. (result: {})", projectTaskExtList.size());
        return projectTaskExtList;
    }

    @SuppressWarnings("unchecked")
    public List<ProjectTaskExt> findByProject(Project project) {
        log.debug("findByProject. (projectId: {})",project.getId());

        CriteriaQuery<ProjectTaskExt> criteria = createCriteriaQuery();

        Join<ProjectTaskExt, ProjectTask> taskJoin = root.join(ProjectTaskExt_.parent);

        criteria.where(
                cb.equal(root.get(ProjectTaskExt_.status), RecordStatus.ACTIVE),
                cb.equal(taskJoin.get(ProjectTask_.project), project)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<ProjectTaskExt> projectTaskExtList = query.getResultList();
        log.debug("findByProject. (result: {})", projectTaskExtList.size());
        return projectTaskExtList;
    }

    @SuppressWarnings("unchecked")
    public List<ProjectTaskExt> findByProjectAndUser(Project project, User user) {
        log.debug("findByProjectAndUser. (projectId: {}, userId: {})",project.getId(),user.getId());

        CriteriaQuery<ProjectTaskExt> criteria = createCriteriaQuery();

        Join<ProjectTaskExt, ProjectTask> taskJoin = root.join(ProjectTaskExt_.parent);

        criteria.where(
                cb.equal(root.get(ProjectTaskExt_.status), RecordStatus.ACTIVE),
                cb.equal(taskJoin.get(ProjectTask_.project), project),
                cb.equal(taskJoin.get(ProjectTask_.user), user)
        );

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<ProjectTaskExt> projectTaskExtList = query.getResultList();
        log.debug("findByProjectAndUser. (result: {})", projectTaskExtList.size());
        return projectTaskExtList;
    }
}
