package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.model.Function;
import com.clevel.kudu.util.DateTimeUtil;
import com.clevel.kudu.util.FacesUtil;
import org.primefaces.component.export.ExcelOptions;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ViewScoped
@Named("rptMandaysCtl")
public class ReportMandaysController extends AbstractController {

    @Inject
    PageAccessControl accessControl;

    private List<UserDTO> userList;
    private long selectedUserId;

    private int currentYear;
    private boolean previousEnable;
    private boolean nextEnable;

    private List<UserMandaysDTO> userMandaysDTOList;
    private UserMandaysDTO totalUserMandays;
    private UtilizationDTO utilization;
    private BigDecimal targetUtilization;

    private String br = "<br/>";

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        loadMandays();
        if (accessControl.functionEnable(Function.F0002)) {
            loadUserList();
        }
    }

    public void onChangeUser() {
        log.debug("onChangeUser. (timeSheetUserId: {})", selectedUserId);
        loadMandays();
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
            userList.add(currentUser);
            selectedUserId = userDetail.getUserId();
            for (UserTimeSheetDTO u : userTSList) {
                userList.add(u.getTimeSheetUser());
            }
            log.debug("userList: {}", userList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

    }

    public void onPrevious() {
        log.debug("onPrevious.");
        loadMandays();
    }

    public void onNext() {
        log.debug("onNext.");
        loadMandays();
    }

    public void checkNavigationButtonEnable() {
        Date lastDate = DateTimeUtil.getLastDateOfMonth(DateTimeUtil.now());

        /*TODO: when time sheet has more than one year @2021++ */

        // check next
        //Date next = DateTimeUtil.getDatePlusMonths(currentMonth, 1);
        //nextEnable = !next.after(lastDate);
        nextEnable = false;

        // check previous
        //Date pre = DateTimeUtil.getDatePlusMonths(currentMonth, -1);
        //previousEnable = !pre.before(tsStartDate);
        previousEnable = false;
    }

    private void loadMandays() {
        if (selectedUserId == 0) {
            selectedUserId = userDetail.getUserId();
        }
        log.debug("loadMandays. (timeSheetUserId: {})", selectedUserId);

        MandaysRequest mandaysRequest = new MandaysRequest();
        mandaysRequest.setUserId(selectedUserId);
        mandaysRequest.setYear(currentYear);

        ServiceRequest<MandaysRequest> request = new ServiceRequest<>(mandaysRequest);
        request.setUserId(userDetail.getUserId());

        Response response = apiService.getTimeSheetResource().getMandays(request);
        if (response.getStatus() != 200) {
            String message = "Call API is failed by connection problem(" + response.getStatus() + ")!";
            log.debug(message);
            FacesUtil.addError(message);
            return;
        }

        ServiceResponse<MandaysResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<MandaysResult>>() {
        });
        log.debug("serviceResponse = {}", serviceResponse);
        if (serviceResponse.getApiResponse() != APIResponse.SUCCESS) {
            String message = "loadMandays is failed! " + serviceResponse.getMessage();
            log.debug(message);
            FacesUtil.addError(message);
            return;
        }

        MandaysResult mandaysResult = serviceResponse.getResult();
        userMandaysDTOList = mandaysResult.getUserMandaysDTOList();
        totalUserMandays = mandaysResult.getTotalMandaysDTO();
        utilization = mandaysResult.getUtilization();
        currentYear = (int) utilization.getYear();
        targetUtilization = (userMandaysDTOList.size() == 0) ? BigDecimal.ZERO : userMandaysDTOList.get(0).getTargetPercentCU().multiply(BigDecimal.valueOf(100.00));

        normalize(totalUserMandays, userMandaysDTOList);

        checkNavigationButtonEnable();

    }

    private void normalize(UserMandaysDTO totalUserMandays, List<UserMandaysDTO> userMandaysDTOList) {
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        int scale = 2;

        totalUserMandays.setChargeHours(totalUserMandays.getChargeHours().setScale(scale, roundingMode));
        totalUserMandays.setChargeDays(totalUserMandays.getChargeDays().setScale(scale, roundingMode));
        totalUserMandays.setAMD(totalUserMandays.getAMD().setScale(scale, roundingMode));

        for (UserMandaysDTO userMandays : userMandaysDTOList) {
            userMandays.setChargeDays(userMandays.getChargeDays().setScale(scale, roundingMode));
            userMandays.setChargeHours(userMandays.getChargeHours().setScale(scale, roundingMode));
            userMandays.setAMD(userMandays.getAMD().setScale(scale, roundingMode));
        }
    }

    public List<UserDTO> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDTO> userList) {
        this.userList = userList;
    }

    public long getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(long selectedUserId) {
        this.selectedUserId = selectedUserId;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
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

    public List<UserMandaysDTO> getUserMandaysDTOList() {
        return userMandaysDTOList;
    }

    public void setUserMandaysDTOList(List<UserMandaysDTO> userMandaysDTOList) {
        this.userMandaysDTOList = userMandaysDTOList;
    }

    public UserMandaysDTO getTotalUserMandays() {
        return totalUserMandays;
    }

    public void setTotalUserMandays(UserMandaysDTO totalUserMandays) {
        this.totalUserMandays = totalUserMandays;
    }

    public UtilizationDTO getUtilization() {
        return utilization;
    }

    public void setUtilization(UtilizationDTO utilization) {
        this.utilization = utilization;
    }

    public BigDecimal getTargetUtilization() {
        return targetUtilization;
    }

    public void setTargetUtilization(BigDecimal targetUtilization) {
        this.targetUtilization = targetUtilization;
    }

    public String getBr() {
        return br;
    }

    public ExcelOptions getExportExcelOptions() {
        return new ExcelOptions("BOLD", "#FFFFFF", "#6666FF", "10", "", "#000000", "11");
    }

    public String dataExportFileName() {
        String userName = null;
        if (userList != null) {
            for (UserDTO userDTO : userList) {
                if (userDTO.getId() == selectedUserId) {
                    userName = userDTO.getLoginName();
                    log.debug("dataExportFileName() = UserName({})", userName);
                }
            }
        }

        if (userName == null) {
            userName = userDetail.getUserName();
            log.debug("dataExportFileName() = CurrentLoggedInUser({})", userName);
        }

        return userName + "-utilization-" + currentYear;
    }

    public void onSaveTargetUtilization() {
        TargetUtilizationRequest targetUtilizationRequest = new TargetUtilizationRequest();
        targetUtilizationRequest.setUserId(selectedUserId);
        targetUtilizationRequest.setYear(currentYear);
        targetUtilizationRequest.setTargetUtilization(targetUtilization.divide(BigDecimal.valueOf(100.00), DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP));

        ServiceRequest<TargetUtilizationRequest> request = new ServiceRequest<>(targetUtilizationRequest);
        request.setUserId(userDetail.getUserId());

        Response response = apiService.getTimeSheetResource().saveTargetUtilization(request);
        if (response.getStatus() != 200) {
            String message = "Save target utilization is failed by connection!";
            log.debug(message);
            FacesUtil.actionFailed(message);
            return;
        }

        ServiceResponse<TargetUtilizationResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TargetUtilizationResult>>() {
        });
        if (serviceResponse.getApiResponse() != APIResponse.SUCCESS) {
            String message = "Save target utilization is failed! " + serviceResponse.getMessage();
            log.debug(message);
            FacesUtil.actionFailed(message);
            return;
        }

        String message = "Save Target Utilization(" + targetUtilization + "%)";
        log.debug(message);
        FacesUtil.actionSuccess(message);
    }
}
