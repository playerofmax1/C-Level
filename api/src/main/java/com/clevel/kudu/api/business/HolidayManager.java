package com.clevel.kudu.api.business;

import com.clevel.kudu.api.dao.working.HolidayDAO;
import com.clevel.kudu.api.dao.working.UserDAO;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.model.db.working.Holiday;
import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.api.rest.mapper.HolidayMapper;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.working.HolidayDTO;
import com.clevel.kudu.model.RecordStatus;
import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Stateless
public class HolidayManager {
    @Inject
    private Logger log;
    @Inject
    private Application app;
    @Inject
    private UserDAO userDAO;
    @Inject
    private HolidayDAO holidayDAO;
    @Inject
    private HolidayMapper holidayMapper;

    @Inject
    public HolidayManager() {
    }

    public HolidayDTO createNewHoliday(long userId, HolidayDTO holidayDTO) throws RecordNotFoundException, ValidationException {
        log.debug("createNewHoliday. (userId: {}, holidayDTO: {})", userId, holidayDTO);

        User user = userDAO.findById(userId);

        Holiday newHoliday = holidayMapper.toEntity(holidayDTO);
        log.debug("newHoliday: {}", newHoliday);

        Date now = DateTimeUtil.now();
        newHoliday.setCreateDate(now);
        newHoliday.setCreateBy(user);
        newHoliday.setModifyDate(now);
        newHoliday.setModifyBy(user);
        newHoliday.setStatus(RecordStatus.ACTIVE);

        newHoliday = holidayDAO.persist(newHoliday);

        return holidayMapper.toDTO(newHoliday);
    }

    public HolidayDTO getHolidayInfo(long userId, long holidayId) throws RecordNotFoundException {
        log.debug("getHolidayInfo. (userId: {}, holidayId: {})", userId, holidayId);

        // validate user
        userDAO.findById(userId);

        Holiday holiday = holidayDAO.findById(holidayId);

        return holidayMapper.toDTO(holiday);
    }

    public void updateHolidayInfo(long userId, HolidayDTO holidayDTO) throws RecordNotFoundException {
        log.debug("updateHolidayInfo. (userId: {}, holidayDTO: {})", userId, holidayDTO);

        // validate user
        User user = userDAO.findById(userId);

        Holiday holiday = holidayDAO.findById(holidayDTO.getId());
        Holiday after = holidayMapper.updateFromDTO(holidayDTO, holiday);

        after.setModifyDate(DateTimeUtil.now());
        after.setModifyBy(user);

//        log.debug("AFTER: {}",after);
        holidayDAO.merge(after);
    }

    public List<HolidayDTO> getHolidayList(long userId) throws RecordNotFoundException {
        log.debug("getHolidayList. (userId: {})", userId);

        // validate user
        User user = userDAO.findById(userId);

        return holidayMapper.toDTO(holidayDAO.findAll().stream());
    }

    public void deleteHoliday(long userId, HolidayDTO holidayDTO) throws RecordNotFoundException {
        log.debug("deleteHoliday. (userId: {}, holidayDTO: {})",userId,holidayDTO);

        User user = userDAO.findById(userId);
        Holiday holiday = holidayDAO.findById(holidayDTO.getId());

        holidayDAO.remove(holiday);
    }

    public long getTotalWorkingDays(long userId, Date month) {
        log.debug("getTotalWorkingDays. (userId: {}, month: {})",userId,month);

        long workingDays = DateTimeUtil.countWorkingDay(month);
        log.debug("normal working days: {}",workingDays);
        long holidays = holidayDAO.countHoliday(month);
        log.debug("holidays: {}",holidays);
        long totalWorkingDays = workingDays-holidays;
        log.debug("total working days: {}",totalWorkingDays);

        return totalWorkingDays;
    }
}
