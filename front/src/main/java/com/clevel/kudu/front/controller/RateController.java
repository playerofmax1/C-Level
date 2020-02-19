package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.RateDTO;
import com.clevel.kudu.front.validation.Validator;
import com.clevel.kudu.util.FacesUtil;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.clevel.kudu.util.LookupUtil.getObjById;

@ViewScoped
@Named("rateCtl")
public class RateController extends AbstractController {
    private List<RateDTO> rateList;
    private long selectedRateId;

    private RateDTO newRate;
    private boolean editMode;

    private Validator validator;

    public RateController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        loadRateList();
    }

    private void loadRateList() {
        log.debug("loadRateList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getRateResource().getRateList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<RateDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<RateDTO>>>() {
            });
            rateList = serviceResponse.getResult();
            log.debug("rateList: {}", rateList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreNewRate() {
        log.debug("onPreNewCustomer.");
        newRate = new RateDTO();
        editMode = false;
        validator = new Validator();
    }

    public void onNewRate() {
        log.debug("onNewRate.");

        validator = new Validator();

        // empty validation (first priority)
        validator.mustNotBlank("code",newRate.getCode(),"Code can not be empty");
        validator.mustNotBlank("name",newRate.getName(),"Name can not be empty");

        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            return;
        }

        //validation pass
        ServiceRequest<RateDTO> request = new ServiceRequest<>(newRate);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getRateResource().newRate(request);
        if (response.getStatus() == 200) {
            ServiceResponse<RateDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<RateDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadRateList();

        PrimeFaces.current().executeScript("PF('rateDlg').hide();");
    }

    private void loadRateInfo() {
        log.debug("loadRateInfo. (selectedRateId: {})",selectedRateId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(selectedRateId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getRateResource().getRateInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<RateDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<RateDTO>>() {
            });
            newRate = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreUpdateRate() {
        log.debug("onPreUpdateRate. (selectedRateId: {})",selectedRateId);

        loadRateInfo();
        editMode = true;
        validator = new Validator();
    }

    public void onUpdateRate() {
        log.debug("onUpdateRate. (selectedRateId: {})",selectedRateId);

        //validation
        validator = new Validator();

        // empty validation (first priority)
        validator.mustNotBlank("code",newRate.getCode(),"Code can not be empty");
        validator.mustNotBlank("name",newRate.getName(),"Name can not be empty");

        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            loadRateList();
            return;
        }

        ServiceRequest<RateDTO> request = new ServiceRequest<>(newRate);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getRateResource().updateRateInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<RateDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<RateDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadRateList();

        PrimeFaces.current().executeScript("PF('rateDlg').hide();");
    }

    public void onPreDeleteRate() {
        log.debug("onPreDeleteRate. (selectedRateId: {})",selectedRateId);

        loadRateInfo();
    }

    public void onDeleteRate() {
        log.debug("onDeleteRate. (selectedRateId: {})",selectedRateId);

        ServiceRequest<RateDTO> request = new ServiceRequest<>(newRate);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getRateResource().deleteRate(request);
        if (response.getStatus() == 200) {
            ServiceResponse<RateDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<RateDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadRateList();
    }

    public List<RateDTO> getRateList() {
        return rateList;
    }

    public void setRateList(List<RateDTO> rateList) {
        this.rateList = rateList;
    }

    public long getSelectedRateId() {
        return selectedRateId;
    }

    public void setSelectedRateId(long selectedRateId) {
        this.selectedRateId = selectedRateId;
    }

    public RateDTO getNewRate() {
        return newRate;
    }

    public void setNewRate(RateDTO newRate) {
        this.newRate = newRate;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }
}
