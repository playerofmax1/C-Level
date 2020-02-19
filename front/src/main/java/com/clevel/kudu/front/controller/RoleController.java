package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.security.RoleDTO;
import com.clevel.kudu.dto.security.RoleFunctionRequest;
import com.clevel.kudu.dto.security.RoleScreenRequest;
import com.clevel.kudu.front.validation.Validator;
import com.clevel.kudu.model.Function;
import com.clevel.kudu.model.Screen;
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
@Named("roleCtl")
public class RoleController extends AbstractController {
    private DualListModel<Screen> screenList;
    private DualListModel<Function> functionList;

    private List<RoleDTO> roleList;
    private long selectedRoleId;
    private RoleDTO newRole;

    private boolean editMode;
    private Validator validator;

    public RoleController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        editMode = false;
        newRole = new RoleDTO();
        initialDualList();
        loadRoleList();
        if (!roleList.isEmpty()) {
            selectedRoleId = roleList.get(0).getId();
        }

        onChangeRole();
    }

    private List<Screen> screenFilter(List<Screen> screenList) {
        log.debug("screenFilter. (screenList: {})",screenList);
        List<Screen> resultList = new ArrayList<>();
        for (Screen s: screenList) {
            int code = Integer.parseInt(s.name().substring(1));
//            log.debug("code: {}",code);
            if (code > 0 && code < 1000) {
                resultList.add(s);
            }
        }
        return resultList;
    }

    private List<Function> functionFilter(List<Function> functionList) {
        log.debug("functionFilter. (functionList: {})",functionList);
        List<Function> resultList = new ArrayList<>();
        for (Function f: functionList) {
            int code = Integer.parseInt(f.name().substring(1));
//            log.debug("code: {}",code);
            if (code > 0 && code < 1000) {
                resultList.add(f);
            }
        }
        return resultList;
    }

    private void initialDualList() {
        log.debug("initialDualList.");
        List<Screen> screenSource = new ArrayList<>(Arrays.asList(Screen.values()));
        List<Function> functionSource = new ArrayList<>(Arrays.asList(Function.values()));

        List<Screen> screenTarget = new ArrayList<>();
        List<Function> functionTarget = new ArrayList<>();

        screenList = new DualListModel<>(screenFilter(screenSource),screenTarget);
        functionList = new DualListModel<>(functionFilter(functionSource),functionTarget);
    }

    public void onTransfer(TransferEvent event) {
        log.debug("onTransfer. (selectedRoleId: {}, screen: {})",selectedRoleId,event.getItems());

        if (event.getItems().get(0).toString().startsWith("S")) {
            RoleScreenRequest roleScreenRequest = new RoleScreenRequest();
            roleScreenRequest.setRole(getObjById(roleList,selectedRoleId));
            roleScreenRequest.setScreenList(screenList.getTarget());
            ServiceRequest<RoleScreenRequest> request = new ServiceRequest<>(roleScreenRequest);
            request.setUserId(userDetail.getUserId());
            Response response = apiService.getSecurityResource().updateRoleScreen(request);
            if (response.getStatus() == 200) {
                ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
                });
                FacesUtil.addInfo(serviceResponse.getApiResponse().description());
            } else {
                log.debug("wrong response status! (status: {})", response.getStatus());
                FacesUtil.addError("wrong response from server!");
            }

        } else {
            RoleFunctionRequest roleFunctionRequest = new RoleFunctionRequest();
            roleFunctionRequest.setRole(getObjById(roleList,selectedRoleId));
//            log.debug("SRC: {}",functionList.getSource());
//            log.debug("TAR: {}",functionList.getTarget());
            roleFunctionRequest.setFunctionList(functionList.getTarget());
            ServiceRequest<RoleFunctionRequest> request = new ServiceRequest<>(roleFunctionRequest);
            request.setUserId(userDetail.getUserId());
            Response response = apiService.getSecurityResource().updateRoleFunction(request);
            if (response.getStatus() == 200) {
                ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
                });
                FacesUtil.addInfo(serviceResponse.getApiResponse().description());
            } else {
                log.debug("wrong response status! (status: {})", response.getStatus());
                FacesUtil.addError("wrong response from server!");
            }
        }

        loadRoleList();
    }

    public void onPreNewRole() {
        log.debug("onPreNewRole.");
        newRole = new RoleDTO();
        editMode = false;
        validator = new Validator();
    }

    private void loadRoleInfo() {
        log.debug("loadRoleInfo. (selectedRoleId: {})",selectedRoleId);

        newRole = getObjById(roleList,selectedRoleId);
        ServiceRequest<RoleDTO> request = new ServiceRequest<>(newRole);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getRoleInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<RoleDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<RoleDTO>>() {
            });
            newRole = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreUpdateRole() {
        log.debug("onPreUpdateRole. (selectedRoleId: {})",selectedRoleId);

        loadRoleInfo();
        editMode = true;
        validator = new Validator();
    }

    public void onNewRole() {
        log.debug("onNewRole. (newRole: {})",newRole);

        //validation here
        validator = new Validator();
        validator.mustNotBlank("name",newRole.getName(),"Name can not be empty");

        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            return;
        }

        ServiceRequest<RoleDTO> request = new ServiceRequest<>(newRole);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().newRole(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        newRole = new RoleDTO();
        loadRoleList();
        if (selectedRoleId==0) {
            selectedRoleId = roleList.get(0).getId();
        }

        PrimeFaces.current().executeScript("PF('roleDlg').hide();");
    }

    public void onUpdateRole() {
        log.debug("onUpdateRole. (selectRoleId: {})",selectedRoleId);

        //validation here
        validator = new Validator();
        validator.mustNotBlank("name",newRole.getName(),"Name can not be empty");

        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            PrimeFaces.current().ajax().update("mainForm:msg");
            loadRoleList();
            return;
        }

        ServiceRequest<RoleDTO> request = new ServiceRequest<>(getObjById(roleList,selectedRoleId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().updateRole(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        newRole = new RoleDTO();
        loadRoleList();

        PrimeFaces.current().executeScript("PF('roleDlg').hide();");
        PrimeFaces.current().ajax().update("mainForm");

    }

    public void onPreDeleteRole() {
        log.debug("onPreDeleteRole.");
        loadRoleInfo();
    }

    public void onDeleteRole() {
        log.debug("onDeleteRole. (selectRoleId: {})",selectedRoleId);

        ServiceRequest<RoleDTO> request = new ServiceRequest<>(newRole);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().deleteRole(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        newRole = new RoleDTO();
        loadRoleList();
        if (roleList.isEmpty()) {
            selectedRoleId = 0;
        }
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

    public void onChangeRole() {
        log.debug("onChangeRole. (selectedRoleId: {})",selectedRoleId);

        if (selectedRoleId==0) {
            log.debug("no role available.");
            return;
        }
        // screen tab
        ServiceRequest<RoleDTO> request = new ServiceRequest<>(getObjById(roleList,selectedRoleId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getRoleScreenList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<Screen>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<Screen>>>() {
            });

            List<Screen> screenSource = new ArrayList<>(Arrays.asList(Screen.values()));
            List<Screen> screenTarget = serviceResponse.getResult();
            screenSource.removeAll(screenTarget);

            screenList = new DualListModel<>(screenFilter(screenSource),screenTarget);

            log.debug("screenList: {}", screenTarget);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        // function tab
        response = apiService.getSecurityResource().getRoleFunctionList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<Function>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<Function>>>() {
            });

            List<Function> functionSource = new ArrayList<>(Arrays.asList(Function.values()));
            List<Function> functionTarget = serviceResponse.getResult();
            functionSource.removeAll(functionTarget);

            functionList = new DualListModel<>(functionFilter(functionSource),functionTarget);

            log.debug("functionList: {}", functionTarget);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

    }

    public DualListModel<Screen> getScreenList() {
        return screenList;
    }

    public void setScreenList(DualListModel<Screen> screenList) {
        this.screenList = screenList;
    }

    public DualListModel<Function> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(DualListModel<Function> functionList) {
        this.functionList = functionList;
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

    public RoleDTO getNewRole() {
        return newRole;
    }

    public void setNewRole(RoleDTO newRole) {
        this.newRole = newRole;
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
