package com.clevel.kudu.api.dao.working;

import com.clevel.kudu.api.dao.GenericDAO;
import com.clevel.kudu.api.model.db.working.Project;
import com.clevel.kudu.api.model.db.working.Project_;
import com.clevel.kudu.model.RecordStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO extends GenericDAO<Project, Long> {
    @Inject
    private Logger log;

    @Inject
    public ProjectDAO() {
    }

    @SuppressWarnings("unchecked")
    public boolean isCodeExist(String code) {
        log.debug("isCodeExist. (code: {})", code);
        CriteriaQuery<Project> criteria = createCriteriaQuery();

        criteria.where(
                cb.and(
                        cb.equal(root.get(Project_.code), code),
                        cb.equal(root.get(Project_.status), RecordStatus.ACTIVE)
                )
        );

        Query query = em.createQuery(criteria);

        List<Project> list;
        list = query.getResultList();
        log.debug("isCodeExist. (code: {}, result size: {})", code, ((list.isEmpty()) ? 0 : list.size()));
        return !list.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public List<Project> findAll() {
        log.debug("findAll.");
        CriteriaQuery<Project> criteria = createCriteriaQuery();

        criteria.where(
                cb.or(cb.equal(root.get(Project_.status), RecordStatus.ACTIVE),
                        cb.equal(root.get(Project_.status), RecordStatus.CLOSE))
        ).orderBy(cb.asc(root.get(Project_.code)));

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<Project> projects = query.getResultList();
        log.debug("findAll. (result: {})", projects.size());
        return projects;
    }

    @SuppressWarnings("unchecked")
    public List<Project> searchProject(String code, String name) {
        log.debug("searchProject. ( code: {}, name: {})", code, name);
        CriteriaQuery<Project> criteria = createCriteriaQuery();

        List<Predicate> predicates = new ArrayList<>();

        if (code != null && !code.isEmpty()) {
            predicates.add(cb.like(root.get(Project_.code), code + "%"));
        }

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(root.get(Project_.name), name + "%"));
        }


        if (predicates.size() == 0) {
            criteria.where(
                    cb.or(cb.equal(root.get(Project_.status), RecordStatus.ACTIVE),
                            cb.equal(root.get(Project_.status), RecordStatus.CLOSE))

            ).orderBy(cb.asc(root.get(Project_.code)));
        } else if (predicates.size() == 1) {
            criteria.where(
                    cb.and(
                            predicates.get(0),
                            cb.or(cb.equal(root.get(Project_.status), RecordStatus.ACTIVE),
                                    cb.equal(root.get(Project_.status), RecordStatus.CLOSE))
                    )
            ).orderBy(cb.asc(root.get(Project_.code)));
        } else {
            criteria.where(
                    cb.and(
                            cb.and(predicates.toArray(new Predicate[0])
                            ),
                            cb.or(cb.equal(root.get(Project_.status), RecordStatus.ACTIVE),
                                    cb.equal(root.get(Project_.status), RecordStatus.CLOSE))
                    )
            ).orderBy(cb.asc(root.get(Project_.code)));
        }

        Query query = em.createQuery(criteria);
        query.setHint(FETCH_GRAPH, graph);

        List<Project> result = query.getResultList();

        log.debug("searchProject. (result size: {})", result.size());
        return result;
    }

}
