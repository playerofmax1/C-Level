package com.clevel.kudu.api.rest.resources;

import com.clevel.kudu.api.CustomerService;
import com.clevel.kudu.api.business.CustomerManager;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.CustomerDTO;
import com.clevel.kudu.model.APIResponse;
import org.slf4j.Logger;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

public class CustomerResource implements CustomerService {
    @Inject
    private Logger log;
    @Inject
    private Application app;

    @Inject
    private CustomerManager customerManager;

    @Override
    public Response newCustomer(ServiceRequest<CustomerDTO> request) {
        log.debug("newCustomer. (request: {})", request);

        ServiceResponse<CustomerDTO> response = new ServiceResponse<>();
        CustomerDTO dto = request.getRequest();

        try {
            CustomerDTO customerDTO = customerManager.createNewCustomer(request.getUserId(), dto);
            response.setResult(customerDTO);
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
    public Response getCustomerInfo(ServiceRequest<SimpleDTO> request) {
        log.debug("getCustomerInfo. (request: {})", request);

        ServiceResponse<CustomerDTO> response = new ServiceResponse<>();

        try {
            CustomerDTO customerDTO = customerManager.getCustomerInfo(request.getUserId(), request.getRequest().getId());
            response.setResult(customerDTO);
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
    public Response updateCustomerInfo(ServiceRequest<CustomerDTO> request) {
        log.debug("updateCustomerInfo. (request: {})", request);

        ServiceResponse<CustomerDTO> response = new ServiceResponse<>();
        CustomerDTO before = null;
        CustomerDTO after = null;

        try {
            before = customerManager.getCustomerInfo(request.getUserId(), request.getRequest().getId());
            customerManager.updateCustomerInfo(request.getUserId(), request.getRequest());
            after = customerManager.getCustomerInfo(request.getUserId(), request.getRequest().getId());
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
    public Response getCustomerList(ServiceRequest<SimpleDTO> request) {
        log.debug("getCustomerList. (request: {})", request);

        List<CustomerDTO> customerDTOList = Collections.emptyList();
        ServiceResponse<List<CustomerDTO>> response = new ServiceResponse<>();

        try {
            customerDTOList = customerManager.getCustomerList(request.getUserId());
            response.setResult(customerDTOList);
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
    public Response deleteCustomer(ServiceRequest<CustomerDTO> request) {
        log.debug("deleteCustomer. (request: {})", request);

        ServiceResponse<CustomerDTO> response = new ServiceResponse<>();

        try {
            customerManager.deleteCustomer(request.getUserId(), request.getRequest());
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
