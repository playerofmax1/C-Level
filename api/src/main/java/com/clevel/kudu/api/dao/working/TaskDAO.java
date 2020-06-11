package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.working.Task;
import com.clevel.kudu.api.model.db.working.Task_;
import com.clevel.kudu.model.RecordStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class TaskDAO extends GenericDAO<Task, Long> {
    @Inject
    private Logger log;

    @Inject
    public TaskDAO() {
    }

    @SuppressWarnings("unchecked")
    public List<Task> findAll() {
        log.debug("findAll.");
        CriteriaQuery<Task> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(Task_.status), RecordStatus.ACTIVE)
        ).orderBy(cb.asc(root.get(Task_.code)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<Task> tasks = query.getResultList();
        log.debug("findAll. (result: {})", tasks.size());
        return tasks;
    }

    @SuppressWarnings("unchecked")
    public List<Task> findAll(boolean chargeable) {
        log.debug("findAll. (chargeable: {})", chargeable);
        CriteriaQuery<Task> criteria = createCriteriaQuery();

        criteria.where(
                cb.equal(root.get(Task_.status), RecordStatus.ACTIVE),
                cb.equal(root.get(Task_.chargeable), chargeable)
        ).orderBy(cb.asc(root.get(Task_.code)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<Task> tasks = query.getResultList();
        log.debug("findAll. (result: {})", tasks.size());
        return tasks;
    }

}
