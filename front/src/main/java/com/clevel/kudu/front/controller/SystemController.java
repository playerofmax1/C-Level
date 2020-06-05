package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.security.PwdRequest;
import com.clevel.kudu.front.validation.Validator;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.util.FacesUtil;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

@ViewScoped
@Named("systemCtl")
public class SystemController extends AbstractController {
    private PwdRequest pwdRequest;
    private String confirmPwd;
    private Validator validator;

    private boolean saved;

    public SystemController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");
        saved = false;
        pwdRequest = new PwdRequest();
    }

    public void onChangePwd() {
        log.debug("onChangePwd.");

        validator = new Validator();
        validator.mustPwdMatch("pwd", pwdRequest.getNewPassword(), confirmPwd, "The password and confirmation do not match");

        validator.mustNotBlank("pwd", pwdRequest.getNewPassword(), "New Password can not be empty");
        validator.mustNotBlank("oldPwd", pwdRequest.getOldPassword(), "Old password can not be empty");
        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            return;
        }

        pwdRequest.setUserId(userDetail.getUserId());
        ServiceRequest<PwdRequest> request = new ServiceRequest<>(pwdRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().changePassword(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });

            if (serviceResponse.getApiResponse() == APIResponse.FAILED) {
                FacesUtil.addError(serviceResponse.getMessage());
                return;
            }
            FacesUtil.actionSuccess("Password is changed successful.");
            saved = true;
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.actionFailed("wrong response from server!");
        }

        pwdRequest = new PwdRequest();
        confirmPwd = "";
    }

    public void onMigrateWorkHour() {
        log.debug("onMigrateWorkHour.");
        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().migrateWorkHour(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });

            if (serviceResponse.getApiResponse() == APIResponse.FAILED) {
                FacesUtil.addError(serviceResponse.getMessage());
                return;
            }
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onReCalculationPercentAMD() {
        log.debug("onReCalculationPercentAMD.");
        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().reCalculatePercentAMD(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });

            if (serviceResponse.getApiResponse() == APIResponse.FAILED) {
                FacesUtil.addError(serviceResponse.getMessage());
                return;
            }
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public PwdRequest getPwdRequest() {
        return pwdRequest;
    }

    public void setPwdRequest(PwdRequest pwdRequest) {
        this.pwdRequest = pwdRequest;
    }

    public String getConfirmPwd() {
        return confirmPwd;
    }

    public void setConfirmPwd(String confirmPwd) {
        this.confirmPwd = confirmPwd;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
