package com.clevel.kudu.api.rest.resources;

import com.clevel.kudu.api.RateService;
import com.clevel.kudu.api.business.RateManager;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.RateDTO;
import com.clevel.kudu.model.APIResponse;
import org.slf4j.Logger;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

public class RateResource implements RateService {
    @Inject
    private Logger log;
    @Inject
    private Application app;

    @Inject
    private RateManager rateManager;

    @Override
    public Response newRate(ServiceRequest<RateDTO> request) {
        log.debug("newRate. (request: {})", request);

        ServiceResponse<RateDTO> response = new ServiceResponse<>();
        RateDTO dto = request.getRequest();

        try {
            RateDTO rateDTO = rateManager.createNewRate(request.getUserId(), dto);
            response.setResult(rateDTO);
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
    public Response getRateInfo(ServiceRequest<SimpleDTO> request) {
        log.debug("getRateInfo. (request: {})", request);

        ServiceResponse<RateDTO> response = new ServiceResponse<>();

        try {
            RateDTO rateDTO = rateManager.getRateInfo(request.getUserId(), request.getRequest().getId());
            response.setResult(rateDTO);
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
    public Response updateRateInfo(ServiceRequest<RateDTO> request) {
        log.debug("updateRateInfo. (request: {})", request);

        ServiceResponse<RateDTO> response = new ServiceResponse<>();
        RateDTO before = null;
        RateDTO after = null;

        try {
            before = rateManager.getRateInfo(request.getUserId(), request.getRequest().getId());
            rateManager.updateRateInfo(request.getUserId(), request.getRequest());
            after = rateManager.getRateInfo(request.getUserId(), request.getRequest().getId());
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
    public Response getRateList(ServiceRequest<SimpleDTO> request) {
        log.debug("getRateList. (request: {})", request);

        List<RateDTO> rateDTOList = Collections.emptyList();
        ServiceResponse<List<RateDTO>> response = new ServiceResponse<>();

        try {
            rateDTOList = rateManager.getRateList(request.getUserId());
            response.setResult(rateDTOList);
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
    public Response deleteRate(ServiceRequest<RateDTO> request) {
        log.debug("deleteRate. (request: {})", request);

        ServiceResponse<RateDTO> response = new ServiceResponse<>();

        try {
            rateManager.deleteRate(request.getUserId(), request.getRequest());
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
