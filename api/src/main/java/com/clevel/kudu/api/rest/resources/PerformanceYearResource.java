package com.clevel.kudu.api.rest.resources;

import com.clevel.kudu.api.PerformanceYearService;
import com.clevel.kudu.api.business.PerformanceYearManager;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.PerformanceYearDTO;
import com.clevel.kudu.model.APIResponse;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;


public class PerformanceYearResource implements PerformanceYearService {

    @Inject
    private Logger log;

    @Inject
    private PerformanceYearManager performanceYearManager;

    @Override
    public Response addLastYear(ServiceRequest<PerformanceYearDTO> request) {
        log.debug("addLastYear. (request: {})", request);

        ServiceResponse<PerformanceYearDTO> response = new ServiceResponse<>();

        try {
            PerformanceYearDTO requestYear = request.getRequest();
            PerformanceYearDTO performanceYearDTO = performanceYearManager.addPerformanceYear(request.getUserId(), requestYear);

            response.setResult(performanceYearDTO);
            response.setApiResponse(APIResponse.SUCCESS);

        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());

        } catch (IllegalArgumentException e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.INVALID_INPUT_PARAMETER, e.getMessage());

        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response updateYear(ServiceRequest<PerformanceYearDTO> request) {
        log.debug("updateYear. (request: {})", request);

        ServiceResponse<PerformanceYearDTO> response = new ServiceResponse<>();

        try {
            PerformanceYearDTO requestYear = request.getRequest();
            PerformanceYearDTO performanceYearDTO = performanceYearManager.updatePerformanceYear(request.getUserId(), requestYear);

            response.setResult(performanceYearDTO);
            response.setApiResponse(APIResponse.SUCCESS);

        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());

        } catch (IllegalArgumentException e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.INVALID_INPUT_PARAMETER, e.getMessage());

        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getYearList(ServiceRequest<SimpleDTO> request) {
        log.debug("getYearList. (request: {})", request);

        List<PerformanceYearDTO> performanceYearDTOList = Collections.emptyList();
        ServiceResponse<List<PerformanceYearDTO>> response = new ServiceResponse<>();

        try {
            performanceYearDTOList = performanceYearManager.getPerformanceYearList(request.getUserId());

            response.setResult(performanceYearDTOList);
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
    public Response deleteLastYear(ServiceRequest<PerformanceYearDTO> request) {
        log.debug("deleteLastYear(request: {})", request);

        ServiceResponse response = new ServiceResponse<>();
        PerformanceYearDTO requestYear = request.getRequest();

        try {
            performanceYearManager.deletePerformanceYear(request.getUserId(), requestYear);

            response.setApiResponse(APIResponse.SUCCESS);

        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());

        } catch (IllegalArgumentException e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.INVALID_INPUT_PARAMETER, e.getMessage());

        } catch (Exception e) {
            log.warn("Don't worry, this is expected exception: {}", e.getMessage());
            response = new ServiceResponse<>(APIResponse.CODE_ALREADY_IN_USE, "Year " + requestYear.getYear() + " already inuse! can't delete.");
        }

        return Response.ok().entity(response).build();
    }
}
