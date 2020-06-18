package com.clevel.kudu.api.rest.resources;

import com.clevel.kudu.api.SystemService;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.api.system.SystemManager;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.ConfigDTO;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.model.SystemConfig;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

public class SystemResource implements SystemService {

    @Inject
    private Logger log;

    @Inject
    private Application app;

    @Inject
    private SystemManager systemManager;

    @Override
    public Response getConfigList(ServiceRequest<SimpleDTO> request) {
        log.debug("getConfigList. (request: {})", request);

        List<ConfigDTO> configDTOList = Collections.emptyList();
        ServiceResponse<List<ConfigDTO>> response = new ServiceResponse<>();

        try {
            configDTOList = systemManager.getConfigList();
            response.setResult(configDTOList);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getSpecifiedConfigList(ServiceRequest<List<SystemConfig>> request) {
        log.debug("getSpecifiedConfigList(request: {})", request);

        List<ConfigDTO> configDTOList = Collections.emptyList();
        ServiceResponse<List<ConfigDTO>> response = new ServiceResponse<>();

        try {
            List<SystemConfig> systemConfigList = request.getRequest();
            configDTOList = systemManager.getConfigList(systemConfigList);

            response.setResult(configDTOList);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response saveConfigList(ServiceRequest<List<ConfigDTO>> request) {
        log.debug("saveConfigList(request:{})", request);

        List<ConfigDTO> configDTOList = request.getRequest();
        ServiceResponse<List<ConfigDTO>> response = new ServiceResponse<>();

        try {
            configDTOList = systemManager.saveConfigList(configDTOList);
            app.loadConfiguration();

            response.setResult(configDTOList);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();

    }
}
