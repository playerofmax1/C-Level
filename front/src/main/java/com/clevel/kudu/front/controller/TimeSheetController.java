package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.front.attributes.MandaysRequestOpenAttributes;
import com.clevel.kudu.front.model.SessionAttribute;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.model.Function;
import com.clevel.kudu.model.TaskType;
import com.clevel.kudu.util.DateTimeUtil;
import com.clevel.kudu.util.FacesUtil;
import com.clevel.kudu.util.LookupUtil;
import org.primefaces.PrimeFaces;
import org.primefaces.component.export.ExcelOptions;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.*;

import static com.clevel.kudu.util.LookupUtil.getObjById;

@ViewScoped
@Named("timeSheetCtl")
public class TimeSheetController extends AbstractController {
    private List<TimeSheetDTO> timeSheetSummaryList;
    private List<TimeSheetDTO> timeSheetList;

    private List<UserDTO> userList;
    private long timeSheetUserId;
    private Boolean viewOnly;

    private TimeSheetDTO detail;

    private List<ProjectDTO> projectList;
    private long selectedProjectId;
    private List<ProjectTaskDTO> projectTaskList;
    private long selectedProjectTaskId;
    private List<TaskDTO> taskList;
    private long selectedTaskId;

    private ExcelOptions exportExcelOptions;

    private Date currentMonth;
    private boolean previousEnable;
    private boolean nextEnable;
    private boolean hasNextMonth;
    private boolean hasPreviousMonth;
    private Date tsStartDate;

    private UtilizationDTO utilization;

    private String chargeDurationButton;

    @Inject
    PageAccessControl accessControl;

    @Inject
    HttpSession httpSession;

    public TimeSheetController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        currentMonth = DateTimeUtil.now();
        nextEnable = true;
        previousEnable = true;
        utilization = new UtilizationDTO();

        if (accessControl.functionEnable(Function.F0002)) {
            loadUserList();
        }

        timeSheetUserId = userDetail.getUserId();
        onChangeUser();
        loadTask();
    }

    public void onChangeUser() {
        log.debug("onChangeUser.timeSheetUserId: {}", timeSheetUserId);
        currentMonth = DateTimeUtil.now();

        if (userList == null) {
            tsStartDate = userDetail.getTsStartDate();
        } else {
            UserDTO selectedUser = LookupUtil.getObjById(userList, timeSheetUserId);
            log.debug("onChangeUser.selectedUser: {}", selectedUser);
            tsStartDate = selectedUser.getTsStartDate();
        }
        log.debug("onChangeUser.tsStartDate: {}", tsStartDate);

        loadTimeSheet();
    }

    public void onPrevious() {
        log.debug("onPrevious.");
        currentMonth = DateTimeUtil.getDatePlusMonths(currentMonth, -1);
        loadTimeSheet();
    }

    public void onToday() {
        log.debug("onToday.");
        currentMonth = DateTimeUtil.now();
        loadTimeSheet();
    }

    public void onNext() {
        log.debug("onNext.");
        currentMonth = DateTimeUtil.getDatePlusMonths(currentMonth, 1);
        loadTimeSheet();
    }

    public void onChangeViewOnlyFlag() {
        TimeSheetRequest timeSheetRequest = new TimeSheetRequest();
        timeSheetRequest.setTimeSheetUserId(timeSheetUserId);
        timeSheetRequest.setMonth(currentMonth);

        ServiceRequest<TimeSheetRequest> request = new ServiceRequest<>(timeSheetRequest);
        request.setUserId(timeSheetUserId);

        Response response;
        if (viewOnly) {
            response = apiService.getTimeSheetResource().lockTimeSheet(request);
        } else {
            response = apiService.getTimeSheetResource().unlockTimeSheet(request);
        }

        if (response.getStatus() != 200) {
            String message = "Update ViewOnly flag is failed by connection!";
            log.debug(message);
            FacesUtil.addError(message);
            viewOnly = !viewOnly;
            return;
        }

        ServiceResponse<TimeSheetResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetResult>>() {
        });

        if (serviceResponse.getApiResponse() != APIResponse.SUCCESS) {
            String message = "Update ViewOnly flag is failed! " + serviceResponse.getMessage();
            log.debug(message);
            FacesUtil.addError(message);
            viewOnly = !viewOnly;
            return;
        }

        String message = "Update ViewOnly flag success.";
        log.debug(message);
        FacesUtil.addInfo(message);
    }

    public void onChangeProjectInDetail() {
        log.debug("onChangeProjectInDetail. (selectedProjectId: {})", selectedProjectId);
        selectedProjectTaskId = 0;
        selectedTaskId = 0;

        detail.setProject(getObjById(projectList, selectedProjectId));
        detail.setProjectTask(null);
        detail.setTask(null);

        loadProjectTask();
    }

    public void onChangeProjectTaskInDetail() {
        log.debug("onChangeProjectTaskInDetail. (selectedProjectTaskId: {})", selectedProjectTaskId);
        detail.setProjectTask(getObjById(projectTaskList, selectedProjectTaskId));
    }

    public void onChangeTaskInDetail() {
        log.debug("onChangeTaskInDetail. (selectTaskId: {})", selectedTaskId);
        detail.setTask(getObjById(taskList, selectedTaskId));
    }

    public void onChangeDuration() {
        log.trace("onChangeDuration.");
        chargeDurationButton = DateTimeUtil.durationToString(detail.getChargeDuration());
        log.debug("chargeDurationButton = {}", chargeDurationButton);
    }

    public void onChargeButtonClicked() {
        log.trace("onChargeButtonClicked.");
        detail.setChargeDuration(DateTimeUtil.stringToDuration(chargeDurationButton));
        log.debug("detail.chargeDuration = {}", detail.getChargeDuration());
    }

    private boolean isLeaveTask(TimeSheetDTO detail) {
        if (detail.getTask() == null) {
            return false;
        } else {
            return detail.getTask().getType() == TaskType.LEAVE;
        }
    }

    public void onSaveDetail() {
        log.debug("onSaveDetail. (detail: {})", detail);

        // validate task
        if (detail.getProjectTask() == null && detail.getTask() == null) {
            log.debug("not select project task.");
            FacesUtil.addError("Please select task!");
            return;
        }

        // if task are A002, A003 (leave)
        if (isLeaveTask(detail)) {
            log.debug("on leave task.");
        } else {
            // validate time-in, time-out
            if (detail.getTimeOut().before(detail.getTimeIn()) && detail.getSortOrder() == 1) {
                log.debug("time-out is before time-in. (time-in: {}, time-out: {})", detail.getTimeIn(), detail.getTimeOut());
                FacesUtil.addError("Time-out is before Time-in!");
                return;
            }

            if (detail.getTimeOut().equals(detail.getTimeIn()) && detail.getSortOrder() == 1) {
                log.debug("time-out is equal to time-in. (time-in: {}, time-out: {})", detail.getTimeIn(), detail.getTimeOut());
                FacesUtil.addError("Time-out is equal to Time-in!");
                return;
            }

            // validate charge hour
            if (detail.getChargeDuration().compareTo(Duration.ZERO) <= 0) {
                log.debug("not fill in charge duration.");
                FacesUtil.addError("Charge hours must greater than 0!");
                return;
            }
        }

        // validate description if no project
        if (detail.getProject() == null && detail.getDescription().isEmpty()) {
            log.debug("need to provide description if no project.");
            FacesUtil.addError("Please fill in description.");
            return;
        }

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(detail);
        request.setUserId(timeSheetUserId);
        Response response = apiService.getTimeSheetResource().saveRecord(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            if (serviceResponse.getApiResponse() == APIResponse.SUCCESS) {
                TimeSheetDTO result = serviceResponse.getResult();

                updateViewRecord(result);
                loadUtilization();
                PrimeFaces.current().executeScript("PF('timeSheetDlg').hide();");
            } else {
                FacesUtil.addError(serviceResponse.getMessage());
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onReset() {
        log.debug("onReset. (detail: {})", detail);

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(detail);
        request.setUserId(timeSheetUserId);
        Response response = apiService.getTimeSheetResource().resetRecord(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            if (serviceResponse.getApiResponse() == APIResponse.SUCCESS) {
                TimeSheetDTO result = serviceResponse.getResult();

                updateViewRecord(result);
                loadUtilization();
                PrimeFaces.current().executeScript("PF('timeSheetDlg').hide();");
            } else {
                FacesUtil.addError(serviceResponse.getMessage());
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onAddRecord(TimeSheetDTO timeSheet) {
        log.debug("onAddRecord. (timeSheet: {})", timeSheet);

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(timeSheet);
        request.setUserId(timeSheetUserId);
        Response response = apiService.getTimeSheetResource().newRecord(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            TimeSheetDTO newTimeSheet = serviceResponse.getResult();
            log.debug("newTimeSheet: {}", newTimeSheet);
            timeSheetList.add(newTimeSheet);
            sortList(timeSheetList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onDeleteRecord(TimeSheetDTO timeSheet) {
        log.debug("onDeleteRecord. (timeSheet: {})", timeSheet);

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(timeSheet);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().deleteRecord(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            timeSheetList.removeIf(t -> t.getId() == timeSheet.getId());
            loadUtilization();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void sortList(List<TimeSheetDTO> list) {
        log.debug("sortList.");
//        list.sort((TimeSheetDTO o1, TimeSheetDTO o2) -> {
//            return Integer.compare(0, o2.getWorkDate().compareTo(o1.getWorkDate()));
//        });

        list.sort(Comparator.comparing(TimeSheetDTO::getWorkDate).thenComparing(TimeSheetDTO::getSortOrder));
    }

    public void onPreEdit(TimeSheetDTO timeSheet) {
        log.debug("onPreEdit. (timeSheet: {})", timeSheet);
        detail = timeSheet;

        if (projectList == null) {
            loadProject();
        }

        selectedProjectId = (detail.getProject() == null) ? 0 : detail.getProject().getId();
        selectedProjectTaskId = (detail.getProjectTask() == null) ? 0 : detail.getProjectTask().getId();
        selectedTaskId = (detail.getTask() == null) ? 0 : detail.getTask().getId();

        loadProjectTask();

        loadTimeSheetInfo(timeSheet);
    }

    public void onExtendMandays(ProjectTaskDTO selectedProjectTask) {
        log.debug("onExtendMandays(selectedProjectTask: {})", selectedProjectTask);
        /*TODO: may be need the confirm dialog for lost data on the Timesheet Detail dialog*/

        MandaysRequestOpenAttributes openAttributes = new MandaysRequestOpenAttributes();
        openAttributes.setProjectTask(selectedProjectTask);
        httpSession.setAttribute(SessionAttribute.MANDAYS_REQUEST_OPEN.name(), openAttributes);

        FacesUtil.redirect("/site/mandaysRequest.jsf");
    }

    private void checkNavigationButtonEnable() {
        nextEnable = hasNextMonth;

        Date previousMonth = DateTimeUtil.getDatePlusMonths(currentMonth, -1);
        previousEnable = hasPreviousMonth && !previousMonth.before(tsStartDate);
    }

    private void loadUserList() {
        log.debug("loadUserList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(userDetail.getUserId()));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getUserViewTS(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<UserTimeSheetDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<UserTimeSheetDTO>>>() {
            });
            List<UserTimeSheetDTO> userTSList = serviceResponse.getResult();
            userList = new ArrayList<>();
            UserDTO currentUser = new UserDTO();
            currentUser.setId(userDetail.getUserId());
            currentUser.setName(userDetail.getName());
            currentUser.setLastName(userDetail.getLastName());
            currentUser.setTsStartDate(userDetail.getTsStartDate());
            userList.add(currentUser);
            timeSheetUserId = userDetail.getUserId();
            for (UserTimeSheetDTO u : userTSList) {
                userList.add(u.getTimeSheetUser());
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

    }

    private void checkViewOnly(TimeSheetResult result) {

        // time sheet locked
        viewOnly = isCurrentMonthLocked(result);
        if (viewOnly) {
            log.debug("viewOnly: true");
            return;
        }

        // view only if expired
        log.debug("cutoff enable: {}", result.isCutoffEnable());
        boolean cutoffEnable = result.isCutoffEnable();
        boolean timeSheetOwner = userDetail.getUserId() == timeSheetUserId;
        boolean forceEdit = userDetail.getFunctionList().contains(Function.F0003);

        if (result.isCutoffEnable()) {
            Date lastDate = DateTimeUtil.getLastDateOfMonth(currentMonth);
            Date cutOffDate = DateTimeUtil.getDatePlusDays(lastDate, result.getCutoffDate());
            Date currentDate = DateTimeUtil.currentDate();
            log.debug("cutOffDate: {}, currentDate: {}", cutOffDate, currentDate);

            if (cutoffEnable && timeSheetOwner && !forceEdit) {
                if (currentDate.after(cutOffDate)) {
                    log.debug("edit expired.");
                    viewOnly = true;
                    log.debug("[edit expired] viewOnly: {}", viewOnly);
                } else {
                    // view only if not owner
                    viewOnly = false;
                    log.debug("[not time sheet owner] viewOnly: {}", viewOnly);
                }
            } else {
                viewOnly = getViewResult(cutoffEnable, timeSheetOwner, forceEdit);
            }
        } else {
            viewOnly = getViewResult(cutoffEnable, timeSheetOwner, forceEdit);
        }

        log.debug("viewOnly: {}", viewOnly);
    }

    private boolean isCurrentMonthLocked(TimeSheetResult result) {
        List<TimeSheetLockDTO> timeSheetLockList = result.getTimeSheetLockList();
        log.debug("isCurrentMonthLocked.timeSheetLockList: {}", timeSheetLockList);
        return timeSheetLockList.size() > 0;
    }

    private boolean getViewResult(boolean cutoffEnable, boolean timeSheetOwner, boolean forceEdit) {
        log.debug("getViewResult. (cutoffEnable: {}, timeSheetOwner: {}, forceEdit: {})", cutoffEnable, timeSheetOwner, forceEdit);

        return (!cutoffEnable && !timeSheetOwner && !forceEdit) || (cutoffEnable && !timeSheetOwner && !forceEdit);
    }

    private void loadTimeSheet() {
        log.debug("loadTimeSheet. (timeSheetUserId: {})", timeSheetUserId);

        TimeSheetRequest timeSheetRequest = new TimeSheetRequest();
        timeSheetRequest.setTimeSheetUserId(timeSheetUserId);
        timeSheetRequest.setMonth(currentMonth);
        ServiceRequest<TimeSheetRequest> request = new ServiceRequest<>(timeSheetRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().getTimeSheet(request);
        if (response.getStatus() != 200) {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
            return;
        }

        ServiceResponse<TimeSheetResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetResult>>() {
        });
        TimeSheetResult result = serviceResponse.getResult();
        if (result == null) {
            log.debug("Result not found!");
            FacesUtil.addError("Result not found!");
            return;
        }

        timeSheetList = result.getTimeSheetList();
        sortList(timeSheetList);

        timeSheetSummaryList = sumHoursOfProject(timeSheetList);
        utilization = result.getUtilization();

        hasNextMonth = result.isHasNextMonth();
        hasPreviousMonth = result.isHasPreviousMonth();

        checkNavigationButtonEnable();
        checkViewOnly(result);
    }

    private void loadTimeSheetInfo(TimeSheetDTO timeSheet) {
        log.debug("loadTimeSheetInfo. (timeSheet: {})", timeSheet);

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(timeSheet);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().getTimeSheetInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            detail = serviceResponse.getResult();
            chargeDurationButton = DateTimeUtil.durationToString(detail.getChargeDuration());
            log.debug("detail: {}", detail);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadUtilization() {
        log.debug("loadUtilization. (timeSheetUserId: {}, currentMonth: {})", timeSheetUserId, currentMonth);
        timeSheetSummaryList = sumHoursOfProject(timeSheetList);

        UtilizationRequest utilizationRequest = new UtilizationRequest(timeSheetUserId, currentMonth);
        utilizationRequest.setTotalChargedMinutes(getTotalChargedMinutes(timeSheetSummaryList));

        ServiceRequest<UtilizationRequest> request = new ServiceRequest<>(utilizationRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().getUtilization(request);
        if (response.getStatus() == 200) {
            ServiceResponse<UtilizationResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<UtilizationResult>>() {
            });

            utilization = serviceResponse.getResult().getUtilization();
            log.debug("utilization: {}", utilization);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    /**
     * This function is copied from TimesheetController.getTotalChargedMinutes.
     */
    private long getTotalChargedMinutes(List<TimeSheetDTO> timeSheetDTOList) {
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

    private void loadProject() {
        log.debug("loadProject.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(timeSheetUserId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().getProjectList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ProjectDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ProjectDTO>>>() {
            });
            projectList = serviceResponse.getResult();
            log.debug("projectList: {}", projectList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadProjectTask() {
        log.debug("loadProjectTask. (selectedProjectId: {})", selectedProjectId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(selectedProjectId));
        request.setUserId(timeSheetUserId);
        Response response = apiService.getProjectResource().getProjectTaskListForUser(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ProjectTaskDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ProjectTaskDTO>>>() {
            });
            projectTaskList = serviceResponse.getResult();
            log.debug("projectTaskList: {}", projectTaskList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

    }

    private void loadTask() {
        log.debug("loadTask. (selectedProjectId: {})", selectedProjectId);

        TaskRequest taskRequest = new TaskRequest(false, true, false);
        ServiceRequest<TaskRequest> request = new ServiceRequest<>(taskRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTaskResource().getTaskList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<TaskDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<TaskDTO>>>() {
            });
            taskList = serviceResponse.getResult();
            log.debug("taskList: {}", taskList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private List<TimeSheetDTO> sumHoursOfProject(List<TimeSheetDTO> timeSheetList) {
        /*HashMap<"ProjectCode.TaskCode", TimeSheetDTO>*/
        HashMap<String, TimeSheetDTO> summaryMap = new HashMap<>();
        TimeSheetDTO currentSummaryTimeSheetDTO;
        ProjectDTO project;
        Duration chargeDuration;
        String mapKey;

        for (TimeSheetDTO timeSheetDTO : timeSheetList) {
            project = timeSheetDTO.getProject();
            if (project == null) {
                continue;
            }

            mapKey = project.getCode();
            currentSummaryTimeSheetDTO = summaryMap.get(mapKey);
            if (currentSummaryTimeSheetDTO == null) {
                currentSummaryTimeSheetDTO = new TimeSheetDTO();

                /*may be need to clone Project and Task to another object*/
                currentSummaryTimeSheetDTO.setProjectTask(timeSheetDTO.getProjectTask());
                currentSummaryTimeSheetDTO.setProject(project);
                currentSummaryTimeSheetDTO.setChargeDuration(timeSheetDTO.getChargeDuration());

                summaryMap.put(mapKey, currentSummaryTimeSheetDTO);

            } else {
                chargeDuration = currentSummaryTimeSheetDTO.getChargeDuration();
                chargeDuration = chargeDuration.plusMinutes(timeSheetDTO.getChargeDuration().toMinutes());
                currentSummaryTimeSheetDTO.setChargeDuration(chargeDuration);
            }
        }

        ArrayList<TimeSheetDTO> summaryList = new ArrayList<>(summaryMap.values());
        summaryList.sort((o1, o2) -> {
            return o1.getProject().getCode().compareTo(o2.getProject().getCode());
        });

        return summaryList;
    }

    private void updateViewRecord(TimeSheetDTO timeSheet) {
        log.debug("updateViewRecord. (timeSheet: {})", timeSheet);
        TimeSheetDTO t = null;
        for (TimeSheetDTO tmp : timeSheetList) {
            if (tmp.getId() == timeSheet.getId()) {
                t = tmp;
            }
        }
        timeSheetList.remove(t);
        timeSheetList.add(timeSheet);
        sortList(timeSheetList);
    }

    public List<TimeSheetDTO> getTimeSheetList() {
        return timeSheetList;
    }

    public void setTimeSheetList(List<TimeSheetDTO> timeSheetList) {
        this.timeSheetList = timeSheetList;
    }

    public List<TimeSheetDTO> getTimeSheetSummaryList() {
        return timeSheetSummaryList;
    }

    public void setTimeSheetSummaryList(List<TimeSheetDTO> timeSheetSummaryList) {
        this.timeSheetSummaryList = timeSheetSummaryList;
    }

    public List<ProjectDTO> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectDTO> projectList) {
        this.projectList = projectList;
    }

    public long getSelectedProjectId() {
        return selectedProjectId;
    }

    public void setSelectedProjectId(long selectedProjectId) {
        this.selectedProjectId = selectedProjectId;
    }

    public List<ProjectTaskDTO> getProjectTaskList() {
        return projectTaskList;
    }

    public void setProjectTaskList(List<ProjectTaskDTO> projectTaskList) {
        this.projectTaskList = projectTaskList;
    }

    public long getSelectedProjectTaskId() {
        return selectedProjectTaskId;
    }

    public void setSelectedProjectTaskId(long selectedProjectTaskId) {
        this.selectedProjectTaskId = selectedProjectTaskId;
    }

    public List<TaskDTO> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskDTO> taskList) {
        this.taskList = taskList;
    }

    public long getSelectedTaskId() {
        return selectedTaskId;
    }

    public void setSelectedTaskId(long selectedTaskId) {
        this.selectedTaskId = selectedTaskId;
    }

    public Date getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(Date currentMonth) {
        this.currentMonth = currentMonth;
    }

    public boolean isPreviousEnable() {
        return previousEnable;
    }

    public void setPreviousEnable(boolean previousEnable) {
        this.previousEnable = previousEnable;
    }

    public boolean isNextEnable() {
        return nextEnable;
    }

    public void setNextEnable(boolean nextEnable) {
        this.nextEnable = nextEnable;
    }

    public TimeSheetDTO getDetail() {
        return detail;
    }

    public void setDetail(TimeSheetDTO detail) {
        this.detail = detail;
    }

    public List<UserDTO> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDTO> userList) {
        this.userList = userList;
    }

    public long getTimeSheetUserId() {
        return timeSheetUserId;
    }

    public void setTimeSheetUserId(long timeSheetUserId) {
        this.timeSheetUserId = timeSheetUserId;
    }

    public Boolean getViewOnly() {
        return viewOnly;
    }

    public void setViewOnly(Boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    public UtilizationDTO getUtilization() {
        return utilization;
    }

    public void setUtilization(UtilizationDTO utilization) {
        this.utilization = utilization;
    }

    public String getChargeDurationButton() {
        return chargeDurationButton;
    }

    public void setChargeDurationButton(String chargeDurationButton) {
        this.chargeDurationButton = chargeDurationButton;
    }

    public ExcelOptions getExportExcelOptions() {
        if (exportExcelOptions == null) {
            exportExcelOptions = new ExcelOptions("BOLD", "#FFFFFF", "#6666FF", "10", "", "#000000", "11");
        }
        return exportExcelOptions;
    }

    public void setExportExcelOptions(ExcelOptions exportExcelOptions) {
        this.exportExcelOptions = exportExcelOptions;
    }

    public String dataExportFileName() {
        String userName = null;
        if (userList != null) {
            for (UserDTO userDTO : userList) {
                if (userDTO.getId() == timeSheetUserId) {
                    userName = userDTO.getLoginName();
                    log.debug("dataExportFileName() = UserName({})", userName);
                }
            }
        }

        if (userName == null) {
            userName = userDetail.getUserName();
            log.debug("dataExportFileName() = CurrentLoggedInUser({})", userName);
        }

        String currentMonthString = DateTimeUtil.getDateStr(currentMonth, "MMM");
        return userName + "-timesheet-" + currentMonthString;
    }

    public String exportProject(TimeSheetDTO timeSheetDTO) {
        ProjectDTO project = timeSheetDTO.getProject();
        if (project == null) {
            return "-- No Project --";
        }

        return project.getCode() + " - " + project.getName();
    }

    public String exportTask(TimeSheetDTO timeSheetDTO) {
        TaskDTO task;

        ProjectTaskDTO projectTaskDTO = timeSheetDTO.getProjectTask();
        if (projectTaskDTO != null) {
            task = projectTaskDTO.getTask();
        } else {
            task = timeSheetDTO.getTask();
            if (task == null) {
                return "-- No Task --";
            }
        }

        return task.getCode() + " - " + task.getName();
    }

}
