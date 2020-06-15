package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.ConfigDTO;
import com.clevel.kudu.util.FacesUtil;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

@ViewScoped
@Named("adminSettingsCtl")
public class AdminSettingsController extends AbstractController {

    private List<ConfigDTO> configList;

    private boolean changed;

    @PostConstruct
    public void constructor() {
        changed = false;

        loadConfigList();
    }

    private void loadConfigList() {
        log.debug("loadConfigList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSystemResource().getConfigList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ConfigDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ConfigDTO>>>() {
            });
            configList = serviceResponse.getResult();
            log.debug("configList: {}", configList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

    }

    public void onChanged() {
        changed = true;
    }

    public void onSave() {
        log.debug("onSave");

        ServiceRequest<List<ConfigDTO>> request = new ServiceRequest<>(configList);
        request.setUserId(userDetail.getUserId());

        Response response = apiService.getSystemResource().saveConfigList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ConfigDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ConfigDTO>>>() {
            });
            changed = false;
            configList = serviceResponse.getResult();
            log.debug("configList: {}", configList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public List<ConfigDTO> getConfigList() {
        return configList;
    }

    public void setConfigList(List<ConfigDTO> configList) {
        this.configList = configList;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
