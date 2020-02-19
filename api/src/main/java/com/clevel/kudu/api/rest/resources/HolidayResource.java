package com.clevel.kudu.api.rest.resources;

import com.clevel.kudu.api.HolidayService;
import com.clevel.kudu.api.business.HolidayManager;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.HolidayDTO;
import com.clevel.kudu.model.APIResponse;
import org.slf4j.Logger;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

public class HolidayResource implements HolidayService {
    @Inject
    private Logger log;
    @Inject
    private Application app;
    @Inject
    private HolidayManager holidayManager;

    @Override
    public Response newHoliday(ServiceRequest<HolidayDTO> request) {
        log.debug("newHoliday. (request: {})", request);

        ServiceResponse<HolidayDTO> response = new ServiceResponse<>();
        HolidayDTO dto = request.getRequest();

        try {
            HolidayDTO holidayDTO = holidayManager.createNewHoliday(request.getUserId(), dto);
            response.setResult(holidayDTO);
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
    public Response getHolidayInfo(ServiceRequest<HolidayDTO> request) {
        log.debug("getHolidayInfo. (request: {})", request);

        ServiceResponse<HolidayDTO> response = new ServiceResponse<>();

        try {
            HolidayDTO holidayDTO = holidayManager.getHolidayInfo(request.getUserId(), request.getRequest().getId());
            response.setResult(holidayDTO);
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
    public Response updateHolidayInfo(ServiceRequest<HolidayDTO> request) {
        log.debug("updateHolidayInfo. (request: {})", request);

        ServiceResponse<HolidayDTO> response = new ServiceResponse<>();
        HolidayDTO before = null;
        HolidayDTO after = null;

        try {
            before = holidayManager.getHolidayInfo(request.getUserId(), request.getRequest().getId());
            holidayManager.updateHolidayInfo(request.getUserId(), request.getRequest());
            after = holidayManager.getHolidayInfo(request.getUserId(), request.getRequest().getId());
            response.setResult(after);
            response.setApiResponse(APIResponse.SUCCESS);
            // for comparison purpose
            request.setRequest(before);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (EJBException ejb) {
            if (ejb.getCausedByException() instanceof OptimisticLockException) {
                log.error("{}", ejb.getCausedByException().getMessage());
                response.setResult(before);
                response.setApiResponse(APIResponse.RECORD_UPDATED_BY_OTHER_SESSION);
                response.setMessage(ejb.getCausedByException().getMessage());
            } else {
                response = new ServiceResponse<>(APIResponse.FAILED, ejb.getCausedByException().getMessage());
            }
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getHolidayList(ServiceRequest<SimpleDTO> request) {
        log.debug("getHolidayList. (request: {})", request);

        List<HolidayDTO> holidayDTOList = Collections.emptyList();
        ServiceResponse<List<HolidayDTO>> response = new ServiceResponse<>();

        try {
            holidayDTOList = holidayManager.getHolidayList(request.getUserId());
            response.setResult(holidayDTOList);
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
    public Response deleteHoliday(ServiceRequest<HolidayDTO> request) {
        log.debug("deleteHoliday. (request: {})", request);

        ServiceResponse response = new ServiceResponse<>();

        try {
            holidayManager.deleteHoliday(request.getUserId(), request.getRequest());
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
