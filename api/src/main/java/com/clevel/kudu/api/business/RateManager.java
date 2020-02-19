package com.clevel.kudu.api.business;

import com.clevel.kudu.api.dao.working.RateDAO;
import com.clevel.kudu.api.dao.working.UserDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.model.db.master.Rate;
import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.api.rest.mapper.RateMapper;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.working.RateDTO;
import com.clevel.kudu.model.RecordStatus;
import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Stateless
public class RateManager {
    @Inject
    private Logger log;
    @Inject
    private Application app;
    @Inject
    private RateDAO rateDAO;
    @Inject
    private UserDAO userDAO;
    @Inject
    private RateMapper rateMapper;

    @Inject
    public RateManager() {
    }
    public RateDTO createNewRate(long userId, RateDTO rateDTO) throws RecordNotFoundException, ValidationException {
        log.debug("createNewRate. (userId: {}, rateDTO: {})", userId, rateDTO);

        User user = userDAO.findById(userId);

        Rate newRate = rateMapper.toEntity(rateDTO);
        log.debug("newRate: {}", newRate);

        Date now = DateTimeUtil.now();
        newRate.setCreateDate(now);
        newRate.setCreateBy(user);
        newRate.setModifyDate(now);
        newRate.setModifyBy(user);
        newRate.setStatus(RecordStatus.ACTIVE);

        newRate = rateDAO.persist(newRate);

        return rateMapper.toDTO(newRate);
    }

    public RateDTO getRateInfo(long userId, long rateId) throws RecordNotFoundException {
        log.debug("getRateInfo. (userId: {}, rateId: {})", userId, rateId);

        // validate user
        userDAO.findById(userId);

        Rate rate = rateDAO.findById(rateId);

        return rateMapper.toDTO(rate);
    }

    public void updateRateInfo(long userId, RateDTO rateDTO) throws RecordNotFoundException {
        log.debug("updateRateInfo. (userId: {}, rateDTO: {})", userId, rateDTO);

        // validate user
        User user = userDAO.findById(userId);

        Rate rate = rateDAO.findById(rateDTO.getId());
        Rate after = rateMapper.updateFromDTO(rateDTO, rate);

        after.setModifyDate(DateTimeUtil.now());
        after.setModifyBy(user);

//        log.debug("AFTER: {}",after);
        rateDAO.merge(after);
    }

    public List<RateDTO> getRateList(long userId) throws RecordNotFoundException {
        log.debug("getRateList. (userId: {})", userId);

        // validate user
        User user = userDAO.findById(userId);

        return rateMapper.toDTO(rateDAO.findAll().stream());
    }

    public void deleteRate(long userId, RateDTO rateDTO) throws RecordNotFoundException {
        log.debug("deleteRate. (userId: {}, rateDTO: {})",userId,rateDTO);

        User user = userDAO.findById(userId);
        Rate rate = rateDAO.findById(rateDTO.getId());

        rate.setStatus(RecordStatus.INACTIVE);
        rate.setModifyDate(DateTimeUtil.now());
        rate.setModifyBy(user);

        rateDAO.persist(rate);
    }
}
