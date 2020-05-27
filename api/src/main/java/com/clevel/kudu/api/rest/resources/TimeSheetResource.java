package com.clevel.kudu.api.rest.resources;

import com.clevel.kudu.api.TimeSheetService;
import com.clevel.kudu.api.business.TimeSheetManager;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.model.SystemConfig;
import com.clevel.kudu.api.model.db.working.PerformanceYear;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.api.system.SystemManager;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.util.DateTimeUtil;
import com.clevel.kudu.util.Util;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TimeSheetResource implements TimeSheetService {
    @Inject
    private Logger log;
    @Inject
    private Application app;

    @Inject
    private TimeSheetManager timeSheetManager;
    @Inject
    private SystemManager systemManager;

    @Inject
    private HttpServletRequest httpServletRequest;
    @CookieParam("JSESSIONID")
    Cookie cookie;

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Response getTimeSheet(ServiceRequest<TimeSheetRequest> request) {
        log.debug("getTimeSheet. (request: {})", request);

        systemManager.audit(httpServletRequest.getRequestURL().toString(), httpServletRequest.getRemoteAddr() + ":" + httpServletRequest.getRemotePort(),
                httpServletRequest.getHeader("User-Agent"), httpServletRequest.getHeader("Referer"), (cookie == null) ? "null" : cookie.toString(),
                request.toString());

        TimeSheetResult timeSheetResult = new TimeSheetResult();
        ServiceResponse<TimeSheetResult> response = new ServiceResponse<>();

        try {
            Date monthDate = request.getRequest().getMonth();
            List<TimeSheetDTO> timeSheetDTOList = timeSheetManager.getTimeSheet(request.getUserId(),
                    request.getRequest().getTimeSheetUserId(), monthDate);

            // mark as holiday
            timeSheetManager.markAsHolidays(timeSheetDTOList, monthDate);
            timeSheetResult.setTimeSheetList(timeSheetDTOList);

            long totalChargedMinutes = timeSheetManager.getTotalChargedMinutes(timeSheetDTOList);
            Date monthStartDate = DateTimeUtil.getFirstDateOfMonth(monthDate);
            Date monthEndDate = DateTimeUtil.getLastDateOfMonth(monthDate);
            UtilizationDTO utilization = timeSheetManager.getUtilization(monthStartDate, monthEndDate, totalChargedMinutes);
            timeSheetResult.setUtilization(utilization);

            timeSheetResult.setCutoffEnable(Util.isTrue(app.getConfig(SystemConfig.TS_CUTOFF_DATE_ENABLE)));
            timeSheetResult.setCutoffDate(Integer.parseInt(app.getConfig(SystemConfig.TS_CUTOFF_DATE)));
            log.debug("cutoff enable: {}, cutoff Date: {}", timeSheetResult.isCutoffEnable(), timeSheetResult.getCutoffDate());

            List<TimeSheetLockDTO> timeSheetLockDTOList = timeSheetManager.getTimeSheetLock(request.getRequest().getTimeSheetUserId(), monthDate);
            timeSheetResult.setTimeSheetLockList(timeSheetLockDTOList);

            response.setResult(timeSheetResult);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getTimeSheetInfo(ServiceRequest<TimeSheetDTO> request) {
        log.debug("getTimeSheetInfo. (request: {})", request);

        TimeSheetDTO timeSheetDTO;
        ServiceResponse<TimeSheetDTO> response = new ServiceResponse<>();

        try {
            timeSheetDTO = timeSheetManager.getTimeSheetInfo(request.getUserId(), request.getRequest().getId());
            timeSheetManager.markAsHoliday(timeSheetDTO);
            response.setResult(timeSheetDTO);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getMandays(ServiceRequest<MandaysRequest> request) {
        log.debug("getMandays. (request: {})", request);

        systemManager.audit(httpServletRequest.getRequestURL().toString(), httpServletRequest.getRemoteAddr() + ":" + httpServletRequest.getRemotePort(),
                httpServletRequest.getHeader("User-Agent"), httpServletRequest.getHeader("Referer"), (cookie == null) ? "null" : cookie.toString(),
                request.toString());

        MandaysResult mandaysResult = new MandaysResult();
        ServiceResponse<MandaysResult> response = new ServiceResponse<>();

        try {
            MandaysRequest mandaysRequest = request.getRequest();
            int year = mandaysRequest.getYear();
            if (year == 0) {
                year = Integer.parseInt(app.getConfig(SystemConfig.PF_YEAR));
                log.debug("{} = {}", SystemConfig.PF_YEAR.name(), year);
            }

            List<UserMandaysDTO> userMandaysDTOList = timeSheetManager.getUserMandays(mandaysRequest.getUserId(), year);
            mandaysResult.setUserMandaysDTOList(userMandaysDTOList);

            UserMandaysDTO totalMandaysDTO = timeSheetManager.getTotalMandays(userMandaysDTOList);
            mandaysResult.setTotalMandaysDTO(totalMandaysDTO);

            PerformanceYear performanceYear = timeSheetManager.getPerformanceYear(year);
            long AMDMinutes = DateTimeUtil.mandaysToMinutes(totalMandaysDTO.getAMD());
            UtilizationDTO utilization = timeSheetManager.getUtilization(performanceYear.getStartDate(), performanceYear.getEndDate(), AMDMinutes);
            utilization.setYear(year);
            utilization.setPercentAMD(timeSheetManager.generatePercentAMD(userMandaysDTOList, totalMandaysDTO, utilization.getNetWorkingDays()));
            mandaysResult.setUtilization(utilization);

            response.setResult(mandaysResult);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response newRecord(ServiceRequest<TimeSheetDTO> request) {
        log.debug("newTask. (request: {})", request);

        ServiceResponse<TimeSheetDTO> response = new ServiceResponse<>();
        TimeSheetDTO dto = request.getRequest();

        try {
            TimeSheetDTO timeSheetDTO = timeSheetManager.newRecord(request.getUserId(), dto);
            response.setResult(timeSheetDTO);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response deleteRecord(ServiceRequest<TimeSheetDTO> request) {
        log.debug("deleteRecord. (request: {})", request);

        ServiceResponse<TaskDTO> response = new ServiceResponse<>();

        try {
            timeSheetManager.deleteRecord(request.getUserId(), request.getRequest());
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response saveRecord(ServiceRequest<TimeSheetDTO> request) {
        log.debug("saveRecord. (request: {})", request);

        ServiceResponse<TimeSheetDTO> response = new ServiceResponse<>();
        TimeSheetDTO before = null;
        TimeSheetDTO after = null;

        try {
            before = timeSheetManager.getTimeSheetInfo(request.getUserId(), request.getRequest().getId());
            after = timeSheetManager.saveTimeSheet(before, request.getUserId(), request.getRequest());
            response.setResult(after);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException | ValidationException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getProjectList(ServiceRequest<SimpleDTO> request) {
        log.debug("getProjectList. (request: {})", request);

        List<ProjectDTO> projectDTOList = Collections.emptyList();
        ServiceResponse<List<ProjectDTO>> response = new ServiceResponse<>();

        try {
            projectDTOList = timeSheetManager.getProjectByUser(request.getUserId(), request.getRequest().getId());
            response.setResult(projectDTOList);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getTaskList(ServiceRequest<ProjectTaskRequest> request) {
        log.debug("getTaskList. (request: {})", request);

        List<TaskDTO> taskDTOList = Collections.emptyList();
        ServiceResponse<List<TaskDTO>> response = new ServiceResponse<>();

        try {
            taskDTOList = timeSheetManager.getTaskByProject(request.getUserId(),
                    request.getRequest().getUserId(), request.getRequest().getProjectId());
            response.setResult(taskDTOList);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getUtilization(ServiceRequest<UtilizationRequest> request) {
        log.debug("getUtilization. (request: {})", request);

        ServiceResponse<UtilizationResult> response = new ServiceResponse<>();
        try {
            UtilizationRequest utilizationRequest = request.getRequest();
            long totalChargedMinutes = utilizationRequest.getTotalChargedMinutes();
            Date monthDate = utilizationRequest.getMonth();

            Date monthStartDate = DateTimeUtil.getFirstDateOfMonth(monthDate);
            Date monthEndDate = DateTimeUtil.getLastDateOfMonth(monthDate);
            UtilizationDTO utilization = timeSheetManager.getUtilization(monthStartDate, monthEndDate, totalChargedMinutes);

            UtilizationResult utilizationResult = new UtilizationResult(utilization);
            response.setResult(utilizationResult);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response saveTargetUtilization(ServiceRequest<TargetUtilizationRequest> request) {
        log.debug("saveTargetUtilization. (request: {})", request);

        systemManager.audit(httpServletRequest.getRequestURL().toString(), httpServletRequest.getRemoteAddr() + ":" + httpServletRequest.getRemotePort(),
                httpServletRequest.getHeader("User-Agent"), httpServletRequest.getHeader("Referer"), (cookie == null) ? "null" : cookie.toString(),
                request.toString());

        TargetUtilizationResult targetUtilizationResult = new TargetUtilizationResult();
        ServiceResponse<TargetUtilizationResult> response = new ServiceResponse<>();

        try {
            TargetUtilizationRequest targetUtilizationRequest = request.getRequest();
            UserPerformanceDTO userPerformance = timeSheetManager.saveTargetUtilization(request.getUserId(), targetUtilizationRequest.getUserId(), targetUtilizationRequest.getYear(), targetUtilizationRequest.getTargetUtilization());
            targetUtilizationResult.setUserPerformance(userPerformance);

            response.setResult(targetUtilizationResult);
            response.setApiResponse(APIResponse.SUCCESS);

        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response resetRecord(ServiceRequest<TimeSheetDTO> request) {
        log.debug("resetRecord. (request: {})", request);

        ServiceResponse<TimeSheetDTO> response = new ServiceResponse<>();
        TimeSheetDTO dto = request.getRequest();

        try {
            TimeSheetDTO timeSheetDTO = timeSheetManager.resetRecord(request.getUserId(), dto);
            response.setResult(timeSheetDTO);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response migrateWorkHour(ServiceRequest<SimpleDTO> request) {
        log.debug("migrateWorkHour. (request: {})", request);

        ServiceResponse response = new ServiceResponse();

        try {
            timeSheetManager.migrateWorkHour();
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Response lockTimeSheet(ServiceRequest<TimeSheetRequest> request) {
        log.debug("lockTimeSheet. (request: {})", request);

        systemManager.audit(httpServletRequest.getRequestURL().toString(), httpServletRequest.getRemoteAddr() + ":" + httpServletRequest.getRemotePort(),
                httpServletRequest.getHeader("User-Agent"), httpServletRequest.getHeader("Referer"), (cookie == null) ? "null" : cookie.toString(),
                request.toString());

        TimeSheetResult timeSheetResult = new TimeSheetResult();
        ServiceResponse<TimeSheetResult> response = new ServiceResponse<>();

        try {
            TimeSheetLockDTO timeSheetLockDTO = timeSheetManager.lockTimeSheet(request.getUserId(), request.getRequest().getTimeSheetUserId(), request.getRequest().getMonth());
            List<TimeSheetLockDTO> timeSheetLockDTOList = new ArrayList<>();
            timeSheetLockDTOList.add(timeSheetLockDTO);
            timeSheetResult.setTimeSheetLockList(timeSheetLockDTOList);

            response.setResult(timeSheetResult);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Response unlockTimeSheet(ServiceRequest<TimeSheetRequest> request) {
        log.debug("unlockTimeSheet. (request: {})", request);

        systemManager.audit(httpServletRequest.getRequestURL().toString(), httpServletRequest.getRemoteAddr() + ":" + httpServletRequest.getRemotePort(),
                httpServletRequest.getHeader("User-Agent"), httpServletRequest.getHeader("Referer"), (cookie == null) ? "null" : cookie.toString(),
                request.toString());

        TimeSheetResult timeSheetResult = new TimeSheetResult();
        ServiceResponse<TimeSheetResult> response = new ServiceResponse<>();

        try {
            timeSheetManager.unlockTimeSheet(request.getRequest().getTimeSheetUserId(), request.getRequest().getMonth());

            response.setResult(timeSheetResult);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }
}
