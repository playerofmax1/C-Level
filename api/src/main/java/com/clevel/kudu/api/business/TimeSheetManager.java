package com.clevel.kudu.api.business;

import com.clevel.kudu.api.dao.working.*;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.model.db.view.UserMandays;
import com.clevel.kudu.api.model.db.working.*;
import com.clevel.kudu.api.rest.mapper.*;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.api.util.MDUtil;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.model.RecordStatus;
import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;

@Stateless
public class TimeSheetManager {
    @Inject
    private Logger log;
    @Inject
    private Application app;
    @Inject
    private UserDAO userDAO;
    @Inject
    private UserMandaysDAO userMandaysDAO;
    @Inject
    private UserPerformanceDAO userPerformanceDAO;
    @Inject
    private TimeSheetDAO timeSheetDAO;
    @Inject
    private TimeSheetLockDAO timeSheetLockDAO;
    @Inject
    private ProjectDAO projectDAO;
    @Inject
    private ProjectTaskDAO projectTaskDAO;
    @Inject
    private PerformanceYearDAO performanceYearDAO;
    @Inject
    private TaskDAO taskDAO;
    @Inject
    private HolidayDAO holidayDAO;
    @Inject
    private TimeSheetMapper timeSheetMapper;
    @Inject
    private TimeSheetLockMapper timeSheetLockMapper;
    @Inject
    private ProjectTaskMapper projectTaskMapper;
    @Inject
    private ProjectMapper projectMapper;
    @Inject
    private TaskMapper taskMapper;
    @Inject
    private HolidayManager holidayManager;
    @Inject
    private UserMandaysMapper userMandaysMapper;
    @Inject
    private UserPerformanceMapper userPerformanceMapper;

    private static final BigDecimal BD_100 = new BigDecimal("100");

    @Inject
    public TimeSheetManager() {
    }

    public List<TimeSheetDTO> getTimeSheet(long userId, long timeSheetUserId, Date month) throws RecordNotFoundException {
        log.debug("getTimeSheet. (userId: {}, timeSheetUserId: {}, month: {})", userId, timeSheetUserId, month);

        User user = userDAO.findById(userId);
        User tsUser = userDAO.findById(timeSheetUserId);
        Date now = DateTimeUtil.now();

        Date firstDate = DateTimeUtil.getFirstDateOfMonth(month);
        Date lastDate = DateTimeUtil.getLastDateOfMonth(month);

        List<TimeSheet> timeSheets = timeSheetDAO.findByMonth(tsUser, firstDate, lastDate);
        if (timeSheets.isEmpty()) {
            log.debug("no timeSheet available for this month, initial new records...");
            Date currentDate = firstDate;
            int lastDay = Integer.parseInt(DateTimeUtil.getDateStr(lastDate, "d"));

            List<TimeSheet> list = new ArrayList<>();
            int i = 1;
            while (i <= lastDay) {
                TimeSheet timeSheet = new TimeSheet();
                timeSheet.setWorkDate(currentDate);
                timeSheet.setTimeIn(DateTimeUtil.setTime(currentDate, 9, 0, 0));
                timeSheet.setTimeOut(DateTimeUtil.setTime(currentDate, 9, 0, 0));
                timeSheet.setWorkHour(Duration.ZERO);
                timeSheet.setWorkHourMinute(0);
                timeSheet.setUser(tsUser);
                timeSheet.setChargeDuration(Duration.ZERO);
                timeSheet.setChargeMinute(0);

                timeSheet.setSortOrder(1);

                timeSheet.setCreateBy(tsUser);
                timeSheet.setCreateDate(now);
                timeSheet.setModifyBy(tsUser);
                timeSheet.setModifyDate(now);
                timeSheet.setStatus(RecordStatus.ACTIVE);

                list.add(timeSheet);

                currentDate = DateTimeUtil.getDatePlusDays(currentDate, 1);
                i++;
            }
            timeSheetDAO.persist(list);

            timeSheets = timeSheetDAO.findByMonth(tsUser, firstDate, lastDate);
        }

        return timeSheetMapper.toDTO(timeSheets.stream());
    }

    public List<TimeSheetLockDTO> getTimeSheetLock(long timeSheetUserId, Date date) throws RecordNotFoundException {
        log.debug("getTimeSheetLock. (timeSheetUserId: {}, date: {})", timeSheetUserId, date);

        User tsUser = userDAO.findById(timeSheetUserId);
        List<TimeSheetLock> timeSheetLockList = timeSheetLockDAO.findByDate(tsUser, date);

        return timeSheetLockMapper.toDTO(timeSheetLockList.stream());
    }

    public void markAsHoliday(TimeSheetDTO timeSheetDTO) {
        log.debug("applyHoliday. (timeSheetDTO: {})", timeSheetDTO);

        List<Holiday> holidays = holidayDAO.findByMonth(timeSheetDTO.getWorkDate());
        Map<Date, Date> holidayMap = new HashMap<>();
        for (Holiday h : holidays) {
            holidayMap.put(h.getHolidayDate(), h.getHolidayDate());
        }

        timeSheetDTO.setHoliday(DateTimeUtil.isHoliday(timeSheetDTO.getWorkDate()));
        if (holidayMap.containsKey(timeSheetDTO.getWorkDate())) {
            timeSheetDTO.setHoliday(true);
        }
    }

    public void markAsHolidays(List<TimeSheetDTO> source, Date month) {
        log.debug("applyHolidays.");
        List<Holiday> holidays = holidayDAO.findByMonth(month);
        Map<Date, Date> holidayMap = new HashMap<>();
        for (Holiday h : holidays) {
            holidayMap.put(h.getHolidayDate(), h.getHolidayDate());
        }

        for (TimeSheetDTO ts : source) {
            ts.setHoliday(DateTimeUtil.isHoliday(ts.getWorkDate()));
            if (holidayMap.containsKey(ts.getWorkDate())) {
                ts.setHoliday(true);
            }
        }
    }

    public TimeSheetDTO getTimeSheetInfo(long userId, long timeSheetId) throws RecordNotFoundException {
        log.debug("getTimeSheetInfo. (userId: {}, timeSheetId: {})", userId, timeSheetId);

        // validate user
//        userDAO.findById(userId);

        TimeSheet timeSheet = timeSheetDAO.findById(timeSheetId);

        return timeSheetMapper.toDTO(timeSheet);
    }

    public TimeSheetDTO newRecord(long userId, TimeSheetDTO timeSheetDTO) throws RecordNotFoundException {
        log.debug("newRecord. (userId: {}, timeSheetDTO: {})", userId, timeSheetDTO);

        User user = userDAO.findById(userId);
        Date now = DateTimeUtil.now();

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.setWorkDate(timeSheetDTO.getWorkDate());
        timeSheet.setTimeIn(DateTimeUtil.setTime(timeSheetDTO.getWorkDate(), 9, 0, 0));
        timeSheet.setTimeOut(DateTimeUtil.setTime(timeSheetDTO.getWorkDate(), 9, 0, 0));
        timeSheet.setWorkHour(Duration.ZERO);
        timeSheet.setWorkHourMinute(0);

        timeSheet.setUser(user);
        timeSheet.setChargeDuration(Duration.ZERO);
        timeSheet.setChargeMinute(0);

        int currentOrder = (int) timeSheetDAO.maxSortOrderByDate(timeSheetDTO.getWorkDate());
        timeSheet.setSortOrder(currentOrder + 1);

        timeSheet.setCreateBy(user);
        timeSheet.setCreateDate(now);
        timeSheet.setModifyBy(user);
        timeSheet.setModifyDate(now);
        timeSheet.setStatus(RecordStatus.ACTIVE);

        return timeSheetMapper.toDTO(timeSheetDAO.persist(timeSheet));
    }

    public TimeSheetDTO resetRecord(long userId, TimeSheetDTO timeSheetDTO) throws RecordNotFoundException {
        log.debug("resetRecord. (userId: {}, timeSheetDTO: {})", userId, timeSheetDTO);

        User user = userDAO.findById(userId);
        Date now = DateTimeUtil.now();

        TimeSheet timeSheet = timeSheetDAO.findById(timeSheetDTO.getId());

        // update actual manDays
        if (timeSheet.getProjectTask() != null) {
            ProjectTask projectTask = projectTaskDAO.findProjectTask(timeSheet.getUser(), timeSheet.getProjectTask().getId());
            log.debug("projectTask: {}", projectTask);
            projectTask.setActualMDDuration(projectTask.getActualMDDuration().minus(timeSheet.getChargeDuration()));
            projectTask.setActualMDMinute(projectTask.getActualMDDuration().toMinutes());
            projectTask.setActualMD(DateTimeUtil.getManDays(projectTask.getActualMDMinute()));
            projectTaskDAO.persist(projectTask);
        }

        timeSheet.setWorkDate(timeSheetDTO.getWorkDate());
        timeSheet.setTimeIn(DateTimeUtil.setTime(timeSheetDTO.getWorkDate(), 9, 0, 0));
        timeSheet.setTimeOut(DateTimeUtil.setTime(timeSheetDTO.getWorkDate(), 9, 0, 0));
        timeSheet.setWorkHour(Duration.ZERO);
        timeSheet.setWorkHourMinute(0);
        timeSheet.setDescription("");

        timeSheet.setUser(user);
        timeSheet.setChargeDuration(Duration.ZERO);
        timeSheet.setChargeMinute(0);

        timeSheet.setProject(null);
        timeSheet.setProjectTask(null);
        timeSheet.setTask(null);

        timeSheet.setModifyBy(user);
        timeSheet.setModifyDate(now);

        TimeSheetDTO dto = timeSheetMapper.toDTO(timeSheetDAO.persist(timeSheet));
        dto.setHoliday(timeSheetDTO.isHoliday());

        return dto;
    }

    public void deleteRecord(long userId, TimeSheetDTO timeSheetDTO) throws RecordNotFoundException {
        log.debug("deleteRecord. (userId: {}, timeSheetDTO: {})", userId, timeSheetDTO);

        User user = userDAO.findById(userId);
        TimeSheet timeSheet = timeSheetDAO.findById(timeSheetDTO.getId());

        // update actual manDays
        if (timeSheet.getProjectTask() != null) {
            ProjectTask projectTask = projectTaskDAO.findProjectTask(timeSheet.getUser(), timeSheet.getProjectTask().getId());
            log.debug("projectTask: {}", projectTask);
            projectTask.setActualMDDuration(projectTask.getActualMDDuration().minus(timeSheetDTO.getChargeDuration()));
            projectTask.setActualMDMinute(projectTask.getActualMDDuration().toMinutes());
            projectTask.setActualMD(DateTimeUtil.getManDays(projectTask.getActualMDMinute()));
            projectTaskDAO.persist(projectTask);
        }

        // delete
        timeSheetDAO.delete(timeSheet);
    }

    private void validateManDays(TimeSheet timeSheet, User timeSheetUser) throws RecordNotFoundException, ValidationException {
        log.debug("validateManDays(timeSheet: {})", timeSheet);

        ProjectTask projectTask = projectTaskDAO.findProjectTask(timeSheetUser, timeSheet.getProjectTask().getId());
        long planManDays = projectTask.getPlanMDMinute();
        long extendManDays = projectTask.getExtendMDMinute();
        long sumManDays = timeSheetDAO.sumManDays(timeSheet.getId(), timeSheet.getProjectTask());
        log.debug("plan MD: {}, extend MD: {}, plan+extend MD: {}, sum MD(actual in db): {}, this timeSheet: {}, target: {}",
                planManDays, extendManDays, planManDays + extendManDays, sumManDays, timeSheet.getChargeMinute(),
                sumManDays + timeSheet.getChargeMinute());

        if (sumManDays + timeSheet.getChargeMinute() > planManDays + extendManDays) {
            log.debug("manDays is over limit!");
            throw new ValidationException(APIResponse.MD_IS_OVER_LIMIT);
        }
    }

    public TimeSheetDTO saveTimeSheet(TimeSheetDTO before, long userId, TimeSheetDTO timeSheetDTO) throws RecordNotFoundException, ValidationException {
        log.debug("saveTimeSheet. (before: {},userId: {}, timeSheetDTO: {})", before, userId, timeSheetDTO);

        User user = userDAO.findById(userId);
        TimeSheet tmp = timeSheetDAO.findById(timeSheetDTO.getId());
        TimeSheet timeSheet = timeSheetMapper.updateFromDTO(timeSheetDTO, tmp);

        if (timeSheetDTO.getProject() != null && timeSheetDTO.getProject().getId() != 0) {
            timeSheet.setProject(projectDAO.findById(timeSheetDTO.getProject().getId()));
        } else {
            timeSheet.setProject(null);
        }
        if (timeSheetDTO.getProjectTask() != null) {
            timeSheet.setProjectTask(projectTaskDAO.findById(timeSheetDTO.getProjectTask().getId()));
        } else {
            timeSheet.setProjectTask(null);
        }
        if (timeSheetDTO.getTask() != null) {
            timeSheet.setTask(taskDAO.findById(timeSheetDTO.getTask().getId()));
        } else {
            timeSheet.setTask(null);
        }

        //validate task to ensure only single one is allowed
        if (timeSheet.getTask() != null && timeSheet.getProjectTask() != null) {
            log.error("Task and projectTask have value at the same time!");
            throw new ValidationException(APIResponse.FAILED);
        }

        // update date on time in/time out
        timeSheet.setTimeIn(DateTimeUtil.setTime(timeSheet.getWorkDate(),
                Integer.parseInt(DateTimeUtil.getDateStr(timeSheet.getTimeIn(), "HH")),
                Integer.parseInt(DateTimeUtil.getDateStr(timeSheet.getTimeIn(), "mm")), 0));
        timeSheet.setTimeOut(DateTimeUtil.setTime(timeSheet.getWorkDate(),
                Integer.parseInt(DateTimeUtil.getDateStr(timeSheet.getTimeOut(), "HH")),
                Integer.parseInt(DateTimeUtil.getDateStr(timeSheet.getTimeOut(), "mm")), 0));

        Duration workHour = DateTimeUtil.getWorkHour(timeSheet.getTimeIn(), timeSheet.getTimeOut());
        timeSheet.setWorkHour(workHour);
        timeSheet.setWorkHourMinute(workHour.toMinutes());
        timeSheet.setChargeMinute(timeSheet.getChargeDuration().toMinutes());
        log.debug("timeSheet: {}", timeSheet);

        // validate/update man-days available if task is chargeable
        if (timeSheet.getProject() != null) {
            validateManDays(timeSheet, user);

            // update actual manDays
            ProjectTask projectTask = projectTaskDAO.findProjectTask(timeSheet.getUser(), timeSheet.getProjectTask().getId());
            log.debug("projectTask (before): {}", projectTask);

            long actualMDMinute = getActualManDayMinute(projectTask);
            log.debug("actualMDMinute: {}", actualMDMinute);
            projectTask.setActualMDMinute(actualMDMinute);
            projectTask.setActualMD(DateTimeUtil.getManDays(projectTask.getActualMDMinute()));
            projectTask.setActualMDDuration(DateTimeUtil.getTotalDuration(projectTask.getActualMDMinute()));

            // update %AMD
            projectTask.setPercentAMD(MDUtil.getPercentAMD(projectTask.getPlanMDMinute(), projectTask.getActualMDMinute()));

            projectTask = projectTaskDAO.persist(projectTask);
            log.debug("projectTask (after): {}", projectTask);
        }

        timeSheet.setModifyBy(user);
        timeSheet.setModifyDate(DateTimeUtil.now());

        return timeSheetMapper.toDTO(timeSheetDAO.persist(timeSheet));
    }

    public long getActualManDayMinute(ProjectTask projectTask) {
        log.debug("getActualManDayMinute. (projectTask: {})", projectTask.getId());

        return timeSheetDAO.sumActualManDays(projectTask);
    }

    public List<ProjectDTO> getProjectByUser(long userId, long requestUserId) throws RecordNotFoundException {
        log.debug("getProjectByUser. (userId: {}, requestUserId: {})", userId, requestUserId);

        User user = userDAO.findById(userId);
        User requestUser = userDAO.findById(requestUserId);

        List<ProjectTask> projectTasks = projectTaskDAO.findByUser(requestUser);
        List<Project> projects = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        for (ProjectTask pt : projectTasks) {
            if (!map.containsKey(pt.getProject().getCode())) {
                map.put(pt.getProject().getCode(), pt.getProject().getName());
                projects.add(pt.getProject());
            }
        }

        return projectMapper.toDTO(projects.stream());
    }

    public List<TaskDTO> getTaskByProject(long userId, long requestUserId, long projectId) throws RecordNotFoundException {
        log.debug("getTaskByProject. (userId: {}, requestUserId: {})", userId, projectId);

        User user = userDAO.findById(userId);
        User requestUser = userDAO.findById(requestUserId);
        Project project = projectDAO.findById(projectId);

        List<ProjectTask> projectTasks = projectTaskDAO.findByProjectAndUser(project, requestUser);
        List<Task> tasks = new ArrayList<>();
        for (ProjectTask pt : projectTasks) {
            tasks.add(pt.getTask());
        }

        return taskMapper.toDTO(tasks.stream());
    }

    // special method for migrate date
    public void migrateWorkHour() {
        log.debug("migrateWorkHour. (start)");
        List<TimeSheet> timeSheetList = timeSheetDAO.findForMigrateWorkHour();

        for (TimeSheet ts : timeSheetList) {
            // fix work hour
            Duration workHour = DateTimeUtil.getWorkHour(ts.getTimeIn(), ts.getTimeOut());
            ts.setWorkHour(workHour);
            ts.setWorkHourMinute(workHour.toMinutes());

            // fix time in/time out
            ts.setTimeIn(DateTimeUtil.setTime(ts.getWorkDate(),
                    Integer.parseInt(DateTimeUtil.getDateStr(ts.getTimeIn(), "HH")),
                    Integer.parseInt(DateTimeUtil.getDateStr(ts.getTimeIn(), "mm")), 0));
            ts.setTimeOut(DateTimeUtil.setTime(ts.getWorkDate(),
                    Integer.parseInt(DateTimeUtil.getDateStr(ts.getTimeOut(), "HH")),
                    Integer.parseInt(DateTimeUtil.getDateStr(ts.getTimeOut(), "mm")), 0));
        }

        timeSheetDAO.persist(timeSheetList);
        log.debug("migrateWorkHour. (finish)");
    }

    public TimeSheetLockDTO lockTimeSheet(long userId, long timeSheetUserId, Date date) throws RecordNotFoundException {
        User user = userDAO.findById(userId);
        User timesheetUser = userDAO.findById(timeSheetUserId);
        Date now = DateTimeUtil.now();

        TimeSheetLock timeSheetLock = new TimeSheetLock();
        timeSheetLock.setUser(timesheetUser);
        timeSheetLock.setStartDate(DateTimeUtil.getFirstDateOfMonth(date));
        timeSheetLock.setEndDate(DateTimeUtil.getLastDateOfMonth(date));

        timeSheetLock.setCreateBy(user);
        timeSheetLock.setCreateDate(now);
        timeSheetLock.setModifyBy(user);
        timeSheetLock.setModifyDate(now);

        return timeSheetLockMapper.toDTO(timeSheetLockDAO.persist(timeSheetLock));
    }

    public void unlockTimeSheet(long timeSheetUserId, Date date) throws RecordNotFoundException {
        log.debug("getTimeSheetLock. (timeSheetUserId: {}, date: {})", timeSheetUserId, date);

        User tsUser = userDAO.findById(timeSheetUserId);
        List<TimeSheetLock> timeSheetLockList = timeSheetLockDAO.findByDate(tsUser, date);

        timeSheetLockDAO.delete(timeSheetLockList);
    }

    public List<UserMandaysDTO> getUserMandays(long userId, int year) throws RecordNotFoundException {
        log.debug("getUserMandays(user:{},year:{})", userId, year);
        List<UserMandays> userMandaysList = userMandaysDAO.findByYear(userId, year);

        return userMandaysMapper.toDTO(userMandaysList.stream());
    }

    /**
     * This function has duplicated in TimesheetController.getTotalChargedMinutes.
     * When you modified this function may be need to modify them too.
     */
    public long getTotalChargedMinutes(List<TimeSheetDTO> timeSheetDTOList) {
        long chargedMinutes = 0;
        for (TimeSheetDTO timeSheet : timeSheetDTOList) {
            if (timeSheet.getProject() == null) {
                /*total is for chargeable items only, chargeable item must have PID.*/
                continue;
            }
            if (!timeSheet.getProjectTask().getTask().isChargeable()) {
                log.warn("This case is timesheet with projectTask.chargeable = false, this is impossible case @2020.05.21 by the original technique from Thammasak " +
                        "(chargeable=false will switch to use task-colume and leave null for project-column and project-task-column)" +
                        "(chargeable=true will switch to use project-column and project-task-column and leave null for task-column) {}", timeSheet);
                continue;
            }
            chargedMinutes += timeSheet.getChargeDuration().toMinutes();
        }
        return chargedMinutes;
    }

    public UserMandaysDTO getTotalMandays(List<UserMandaysDTO> userMandaysDTOList) {
        long chargeMinutes = 0;
        BigDecimal chargeHours = BigDecimal.ZERO;
        BigDecimal chargeDays = BigDecimal.ZERO;
        BigDecimal PMD = BigDecimal.ZERO;
        BigDecimal AMD = BigDecimal.ZERO;
        long workDays = 0;

        for (UserMandaysDTO userMandays : userMandaysDTOList) {
            if (userMandays.getProject() == null) {
                /*total is for chargeable items only, chargeable item must have PID.*/
                continue;
            }
            chargeMinutes += userMandays.getChargeMinutes();
            chargeHours = chargeHours.add(userMandays.getChargeHours());
            chargeDays = chargeDays.add(userMandays.getChargeDays());
            AMD = AMD.add(userMandays.getAMD());
            PMD = PMD.add(userMandays.getPMD());
            workDays += userMandays.getWorkDays();
        }

        UserMandaysDTO totalUserMandays = new UserMandaysDTO();
        totalUserMandays.setChargeMinutes(chargeMinutes);
        totalUserMandays.setChargeHours(chargeHours);
        totalUserMandays.setChargeDays(chargeDays);
        totalUserMandays.setWorkDays(workDays);
        totalUserMandays.setPMD(PMD);
        totalUserMandays.setAMD(AMD);

        return totalUserMandays;
    }

    public PerformanceYear getPerformanceYear(int year) throws RecordNotFoundException {
        return performanceYearDAO.findByYear(year);
    }

    public UtilizationDTO getUtilization(Date startDate, Date endDate, long chargeMinutes) {
        long daysInYearExcludeWeekends = DateTimeUtil.countWorkingDay(startDate, endDate);
        long holidaysInYear = holidayDAO.countHoliday(startDate, endDate);
        long netWorkingDays = daysInYearExcludeWeekends - holidaysInYear;
        long netWorkingDaysInMinutes = netWorkingDays * 8 * 60;

        UtilizationDTO utilization = new UtilizationDTO();
        utilization.setStartDate(startDate);
        utilization.setEndDate(endDate);
        utilization.setDaysInYearExcludeWeekends(daysInYearExcludeWeekends);
        utilization.setHolidaysInYear(holidaysInYear);
        utilization.setNetWorkingDays(netWorkingDays);
        utilization.setNetWorkingDaysInMinutes(netWorkingDaysInMinutes);
        utilization.setChargedMinutes(chargeMinutes);
        utilization.setPercentCU(utilization.getPercentCUByDays());
        log.debug("utilization = {}", utilization);

        return utilization;
    }

    /**
     * @param userMandaysDTOList two ways, setNetWorkdays for all records, setPMD and setRPMDPercent for (No-Project) record, setWeight for all records.
     * @param totalMandaysDTO    two ways, setPMD
     * @param netWorkdays        = DaysInYear - Weekends - Holidays
     * @return Average PercentAMD.
     */
    public BigDecimal generatePercentAMD(List<UserMandaysDTO> userMandaysDTOList, UserMandaysDTO totalMandaysDTO, long netWorkdays) {
        log.debug("generatePercentAMD(netWorkdays:{})", netWorkdays);
        int recordCount = userMandaysDTOList.size();
        if (recordCount == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal percentAMD = BigDecimal.ZERO;
        BigDecimal PMD;
        BigDecimal RPMDPercent;
        BigDecimal totalRPMDPercent = BigDecimal.ZERO;
        BigDecimal netWorkdaysDec = new BigDecimal(netWorkdays);
        BigDecimal weight;
        BigDecimal totalWeight = BigDecimal.ZERO;

        BigDecimal weightMultiplier;

        for (UserMandaysDTO userMandays : userMandaysDTOList) {
            userMandays.setNetWorkdays(netWorkdays);
            weightMultiplier = userMandays.isPlanFlag() ? BigDecimal.ONE : BigDecimal.ZERO;

            if (userMandays.getProject() == null) {
                log.debug("in case of no-project");

                BigDecimal AMD = userMandays.getChargeDays().setScale(DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP);
                userMandays.setAMD(AMD);
                log.debug("AMD = {}", AMD);

                PMD = AMD.add(BigDecimal.ZERO);
                userMandays.setPMD(PMD);
                log.debug("PMD = {}", PMD);

                /*recalc for no-project: [RPMDPercent] = (PMD - AMD) / PMD
                 * [RPMDPercent] sometimes called %AMD */
                RPMDPercent = PMD.subtract(AMD).divide(PMD, DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP);
                userMandays.setRPMDPercent(RPMDPercent);
                log.debug("RPMDPercent = {}", RPMDPercent);

            } else {
                PMD = userMandays.getPMD();
                log.debug("PMD = {}", PMD);

                RPMDPercent = userMandays.getRPMDPercent().setScale(DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP);
                userMandays.setRPMDPercent(RPMDPercent);
                log.debug("RPMDPercent = {}", RPMDPercent);

                totalRPMDPercent = totalRPMDPercent.add(RPMDPercent);
                log.debug("totalRPMDPercent = {}", totalRPMDPercent);
            }

            /* recordWeight = (PMD / [NetWorkdays]) x [RPMDPercent]
             * [RPMDPercent] sometimes called %AMD
             **/
            weight = PMD.divide(netWorkdaysDec, DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP);
            log.debug("PMD / newWorkdays = {}", weight);

            weight = weight.multiply(RPMDPercent).setScale(DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP);
            log.debug("PMD / newWorkdays x %AMD = {}", weight);

            weight = weightMultiplier.multiply(weight);
            userMandays.setWeight(weight);

            totalWeight = totalWeight.add(userMandays.getWeight());
        }

        /*exclude no-project: totalRPMDPercent */
        totalMandaysDTO.setRPMDPercent(totalRPMDPercent);
        log.debug("setTotalRPMDPercent = {}", totalRPMDPercent);

        /*include no-project: totalWeight */
        totalMandaysDTO.setWeight(totalWeight);
        log.debug("setTotalWeight = {}", totalWeight);

        /*average percentAMD = totalWeight / recordCount*/
        log.debug("totalWeight = {}", totalWeight);
        percentAMD = totalWeight.divide(BigDecimal.valueOf(recordCount), DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP);
        log.debug("percentAMD = {}", percentAMD);

        return percentAMD;
    }

    public UserPerformanceDTO saveTargetUtilization(long userId, long timeSheetUserId, int year, BigDecimal targetUtilization) throws RecordNotFoundException {
        log.debug("saveTargetUtilization(userId:{}, timeSheetUserId:{}, year:{}, targetUtilization:{})", userId, timeSheetUserId, year, targetUtilization);
        User user = userDAO.findById(userId);
        Date now = DateTimeUtil.now();

        UserPerformance userPerformance = userPerformanceDAO.findByYear(timeSheetUserId, year);
        if (userPerformance == null) {
            userPerformance = new UserPerformance();
            userPerformance.setUserId(timeSheetUserId);

            PerformanceYear performanceYear = performanceYearDAO.findByYear(year);
            userPerformance.setPerformanceYear(performanceYear);

            userPerformance.setCreateBy(user);
            userPerformance.setCreateDate(now);
        }

        userPerformance.setTargetUtilization(targetUtilization);

        userPerformance.setModifyBy(user);
        userPerformance.setModifyDate(now);

        return userPerformanceMapper.toDTO(userPerformanceDAO.persist(userPerformance));
    }
}
