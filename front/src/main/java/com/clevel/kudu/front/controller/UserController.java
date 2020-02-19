package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.security.PwdRequest;
import com.clevel.kudu.dto.security.RoleDTO;
import com.clevel.kudu.dto.working.RateDTO;
import com.clevel.kudu.dto.working.UserDTO;
import com.clevel.kudu.dto.working.UserTimeSheetDTO;
import com.clevel.kudu.dto.working.UserTimeSheetRequest;
import com.clevel.kudu.front.validation.Validator;
import com.clevel.kudu.model.Function;
import com.clevel.kudu.model.Screen;
import com.clevel.kudu.util.DateTimeUtil;
import com.clevel.kudu.util.FacesUtil;
import org.primefaces.PrimeFaces;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.clevel.kudu.util.LookupUtil.getObjById;

@ViewScoped
@Named("userCtl")
public class UserController extends AbstractController {
    private List<RoleDTO> roleList;
    private long selectedRoleId;
    private List<RateDTO> rateList;
    private long selectedRateId;
    private List<UserDTO> userList;
    private long selectedUserId;
    private String confirmPwd;

    private DualListModel<UserDTO> userTimeSheet;

    private UserDTO newUser;
    private boolean editMode;

    private PwdRequest pwdRequest;

    private Validator validator;

    public UserController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        editMode = false;
        userTimeSheet = new DualListModel<>();

        newUser = new UserDTO();
        loadRoleList();
        loadRateList();
        loadUserList();
    }

    private void loadRoleList() {
        log.debug("loadRoleList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getRoleList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<RoleDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<RoleDTO>>>() {
            });
            roleList = serviceResponse.getResult();
            log.debug("roleList: {}", roleList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
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

    private void loadUserList() {
        log.debug("loadUserList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getUserList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<UserDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<UserDTO>>>() {
            });
            userList = serviceResponse.getResult();
            log.debug("userList: {}", userList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreNewUser() {
        log.debug("onPreNewUser.");
        newUser = new UserDTO();
        newUser.setTsStartDate(DateTimeUtil.now());

        selectedRoleId = 0;

        if (!rateList.isEmpty()) {
            selectedRateId = rateList.get(0).getId();
            newUser.setRate(getObjById(rateList, selectedRateId));
        }

        editMode = false;
        validator = new Validator();
    }

    public void onNewUser() {
        log.debug("onNewUser.");

        validator = new Validator();

        // validate last priority first
        // special validation (last priority)
        validator.mustNumericAndValidLength("phone", newUser.getPhoneNumber(), 10, "Phone number is not correct (10 digit only)");
        validator.mustValidEmail("email", newUser.getEmail(), "Email address is not correct");
        validator.mustPwdMatch("pwd", newUser.getPassword(), confirmPwd, "The password and confirmation do not match");

        // empty validation (first priority)
        validator.mustNotBlank("name", newUser.getName(), "Name can not be empty");
        validator.mustNotBlank("lastName", newUser.getLastName(), "Last Name can not be empty");
        validator.mustNotBlank("login", newUser.getLoginName(), "Login name can not be empty");
        validator.mustNotBlank("email", newUser.getEmail(), "Email can not be empty");
        validator.mustNotBlank("phone", newUser.getPhoneNumber(), "Phone number can not be empty");
        validator.mustNotBlank("pwd", newUser.getPassword(), "Password can not be empty");

        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            return;
        }

        //validation pass
        newUser.setRole(getObjById(roleList, selectedRoleId));
        newUser.setRate(getObjById(rateList, selectedRateId));
        ServiceRequest<UserDTO> request = new ServiceRequest<>(newUser);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().newUser(request);
        if (response.getStatus() == 200) {
            ServiceResponse<UserDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<UserDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
            log.debug("userList: {}", userList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadUserList();

        PrimeFaces.current().executeScript("PF('userDlg').hide();");
    }

    private void loadUserInfo() {
        log.debug("loadUserInfo. (selectedUserId: {})",selectedUserId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(selectedUserId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getUserInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<UserDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<UserDTO>>() {
            });
            newUser = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreUpdateUser() {
        log.debug("onPreUpdateUser. (selectedUserId: {})",selectedUserId);

        loadUserInfo();
        if (newUser.getRole() != null) {
            selectedRoleId = newUser.getRole().getId();
        } else {
            selectedRoleId = 0;
        }

        if (newUser.getRate() != null) {
            selectedRateId = newUser.getRate().getId();
        } else {
            selectedRateId = 0;
        }

        editMode = true;
        validator = new Validator();
    }

    public void onUpdateUser() {
        log.debug("onUpdateUser. (selectedUserId: {})", selectedUserId);

        //validation
        validator = new Validator();

        // validate last priority first
        // special validation (last priority)
        validator.mustNumericAndValidLength("phone", newUser.getPhoneNumber(), 10, "Phone number is not correct (10 digit only)");
        validator.mustValidEmail("email", newUser.getEmail(), "Email address is not correct");

        // pattern validation

        // empty validation (first priority)
        validator.mustNotBlank("name", newUser.getName(), "Name can not be empty");
        validator.mustNotBlank("lastName", newUser.getLastName(), "Last Name can not be empty");
        validator.mustNotBlank("email", newUser.getEmail(), "Email can not be empty");
        validator.mustNotBlank("phone", newUser.getPhoneNumber(), "Phone number can not be empty");

        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            loadUserList();
            return;
        }

        newUser.setRole(getObjById(roleList, selectedRoleId));
        newUser.setRate(getObjById(rateList, selectedRateId));
        ServiceRequest<UserDTO> request = new ServiceRequest<>(newUser);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().updateUserInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<UserDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<UserDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadUserList();

        PrimeFaces.current().executeScript("PF('userDlg').hide();");
    }

    public void onPreResetPassword() {
        log.debug("onPreResetPassword. (selectedUserId: {})", selectedUserId);

        newUser = getObjById(userList, selectedUserId);
        pwdRequest = new PwdRequest();
        pwdRequest.setUserId(selectedUserId);
    }

    public void onResetPassword() {
        log.debug("onResetPassword. (pwdRequest: {})", pwdRequest);

        ServiceRequest<PwdRequest> request = new ServiceRequest<>(pwdRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().resetPassword(request);
        if (response.getStatus() == 200) {
            ServiceResponse<UserDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<UserDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
            log.debug("userList: {}", userList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreDeleteUser() {
        log.debug("onPreDeleteUser. (selectedUserId: {})", selectedUserId);

        loadUserInfo();
    }

    public void onDeleteUser() {
        log.debug("onDeleteUser. (selectedUserId: {})", selectedUserId);

        ServiceRequest<UserDTO> request = new ServiceRequest<>(newUser);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().deleteUser(request);
        if (response.getStatus() == 200) {
            ServiceResponse<UserDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<UserDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadUserList();
    }

    public void initDualList() {
        log.debug("initDualList. (selectedUserId: {})", selectedUserId);

        List<UserDTO> userSource = userList;
        List<UserDTO> userTarget = new ArrayList<>();

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(selectedUserId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getUserViewTS(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<UserTimeSheetDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<UserTimeSheetDTO>>>() {
            });
            List<UserTimeSheetDTO> userTSList = serviceResponse.getResult();
            for (UserTimeSheetDTO u : userTSList) {
                userTarget.add(u.getTimeSheetUser());
                userSource.remove(u.getTimeSheetUser());
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        userTimeSheet = new DualListModel<>(userFilter(userSource), userTarget);
        log.debug("userTimeSheet: {}", userTimeSheet);
    }

    private List<UserDTO> userFilter(List<UserDTO> userList) {
        log.debug("userFilter. (userList: {})", userList);
        List<UserDTO> userResult = new ArrayList<>();
        for (UserDTO u : userList) {
            if (u.getId() != userDetail.getUserId()) {
                userResult.add(u);
            }
        }
        log.debug("list in size: {}, list out size: {}", userList.size(), userResult.size());
        return userResult;
    }

    public void onPreViewTSUser() {
        log.debug("onPreViewTSUser.");

        initDualList();
    }

    public void onTransfer(TransferEvent event) {
        log.debug("onTransfer. (selectedUserId: {} user: {})", selectedUserId, event.getItems());
        log.debug("SRC: {}", userTimeSheet.getSource());
        log.debug("TAR: {}", userTimeSheet.getTarget());

        List<UserTimeSheetDTO> tsList = new ArrayList<>();
        UserTimeSheetDTO userTS;

        for (UserDTO u : userTimeSheet.getTarget()) {
            userTS = new UserTimeSheetDTO();
            userTS.setUser(getObjById(userList, selectedUserId));
            userTS.setTimeSheetUser(u);
            log.debug("userTS: {}", userTS);
            tsList.add(userTS);
        }

        UserTimeSheetRequest userTimeSheetRequest = new UserTimeSheetRequest(selectedUserId,tsList);
        ServiceRequest<UserTimeSheetRequest> request = new ServiceRequest<>(userTimeSheetRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().updateUserViewTS(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public List<RoleDTO> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RoleDTO> roleList) {
        this.roleList = roleList;
    }

    public long getSelectedRoleId() {
        return selectedRoleId;
    }

    public void setSelectedRoleId(long selectedRoleId) {
        this.selectedRoleId = selectedRoleId;
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

    public String getConfirmPwd() {
        return confirmPwd;
    }

    public void setConfirmPwd(String confirmPwd) {
        this.confirmPwd = confirmPwd;
    }

    public UserDTO getNewUser() {
        return newUser;
    }

    public void setNewUser(UserDTO newUser) {
        this.newUser = newUser;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public PwdRequest getPwdRequest() {
        return pwdRequest;
    }

    public void setPwdRequest(PwdRequest pwdRequest) {
        this.pwdRequest = pwdRequest;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public DualListModel<UserDTO> getUserTimeSheet() {
        return userTimeSheet;
    }

    public void setUserTimeSheet(DualListModel<UserDTO> userTimeSheet) {
        this.userTimeSheet = userTimeSheet;
    }
}
