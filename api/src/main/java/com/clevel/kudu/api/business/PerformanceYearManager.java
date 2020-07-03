package com.clevel.kudu.api.business;

import com.clevel.kudu.api.dao.working.PerformanceYearDAO;
import com.clevel.kudu.api.dao.working.UserDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.model.db.working.PerformanceYear;
import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.api.rest.mapper.PerformanceYearMapper;
import com.clevel.kudu.dto.working.PerformanceYearDTO;
import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Stateless
public class PerformanceYearManager {

    @Inject
    private Logger log;

    @Inject
    private UserDAO userDAO;

    @Inject
    private PerformanceYearDAO performanceYearDAO;

    @Inject
    private PerformanceYearMapper performanceYearMapper;

    public List<PerformanceYearDTO> getPerformanceYearList(long userId) throws RecordNotFoundException {
        log.debug("getPerformanceYearList. (userId: {})", userId);

        // validate user
        User user = userDAO.findById(userId);

        return performanceYearMapper.toDTO(performanceYearDAO.findAll().stream());
    }

    public PerformanceYearDTO addPerformanceYear(long userId, PerformanceYearDTO requestYear) throws RecordNotFoundException, IllegalArgumentException {
        log.debug("addPerformanceYear(userId: {}, requestYear: {})", userId, requestYear);

        // validate user
        User user = userDAO.findById(userId);

        /*TODO: need to confirm the performanceYearList is ordered by Year*/
        List<PerformanceYear> performanceYearList = performanceYearDAO.findAll();
        PerformanceYear lastYear = performanceYearList.get(performanceYearList.size() - 1);
        int nextYear = (int) lastYear.getYear() + 1;

        // validation: allow next year only.
        if (nextYear != requestYear.getYear()) {
            throw new IllegalArgumentException("Need continued year only, expected:" + nextYear + " requested:" + requestYear.getYear() + ".");
        }

        PerformanceYear newPerformanceYear = new PerformanceYear();
        newPerformanceYear.setYear(nextYear);
        newPerformanceYear.setStartDate(requestYear.getStartDate());
        newPerformanceYear.setEndDate(requestYear.getEndDate());

        Date now = DateTimeUtil.now();
        newPerformanceYear.setCreateDate(now);
        newPerformanceYear.setCreateBy(user);
        newPerformanceYear.setModifyDate(now);
        newPerformanceYear.setModifyBy(user);

        newPerformanceYear = performanceYearDAO.persist(newPerformanceYear);
        return performanceYearMapper.toDTO(newPerformanceYear);
    }

    public PerformanceYearDTO updatePerformanceYear(long userId, PerformanceYearDTO requestYear) throws RecordNotFoundException {
        log.debug("updatePerformanceYear(userId: {}, requestYear: {})", userId, requestYear);

        // validate user and year to update
        User user = userDAO.findById(userId);
        PerformanceYear lastPerformanceYear = performanceYearDAO.findByYear((int) requestYear.getYear());

        lastPerformanceYear.setYear(requestYear.getYear());
        lastPerformanceYear.setStartDate(requestYear.getStartDate());
        lastPerformanceYear.setEndDate(requestYear.getEndDate());

        Date now = DateTimeUtil.now();
        lastPerformanceYear.setModifyDate(now);
        lastPerformanceYear.setModifyBy(user);

        lastPerformanceYear = performanceYearDAO.persist(lastPerformanceYear);
        return performanceYearMapper.toDTO(lastPerformanceYear);
    }

    public void deletePerformanceYear(long userId, PerformanceYearDTO requestYear) throws RecordNotFoundException, IllegalArgumentException {
        log.debug("deletePerformanceYear(userId: {}, requestYear: {})", userId, requestYear);

        // validate user and year to delete
        User user = userDAO.findById(userId);
        PerformanceYear lastPerformanceYear = performanceYearDAO.findLastYear();

        if (requestYear.getYear() != lastPerformanceYear.getYear()) {
            throw new IllegalArgumentException("The year(" + requestYear.getYear() + ") is not allowed to delete! last year only.");
        }

        performanceYearDAO.delete(lastPerformanceYear);
    }
}
