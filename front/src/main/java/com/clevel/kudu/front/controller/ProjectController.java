package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.CustomerDTO;
import com.clevel.kudu.dto.working.ProjectDTO;
import com.clevel.kudu.dto.working.SearchRequest;
import com.clevel.kudu.dto.working.UserDTO;
import com.clevel.kudu.front.validation.Validator;
import com.clevel.kudu.model.RecordStatus;
import com.clevel.kudu.util.FacesUtil;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.clevel.kudu.util.LookupUtil.getObjById;

@ViewScoped
@Named("projectCtl")
public class ProjectController extends AbstractController {
    private List<ProjectDTO> projectList;
    private long selectedProjectId;

    private ProjectDTO newProject;
    private boolean editMode;

    private Validator validator;

    private List<CustomerDTO> customerList;
    private long selectedCustomerId;

    private SearchRequest searchRequest;
    private long userId;
    private List<UserDTO> approverList;

    public ProjectController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");
        searchRequest = new SearchRequest();
        approverList = new ArrayList<>();

        loadCustomerList();
        loadProjectList();
        loadApproverList();

        //Step 1 : find all rel_role_function (F0005)
        // List 8 9
        //Step 2 : use roleid to find user in wrk_user
        //userOwnerList = Step 2
    }

    private void loadCustomerList() {
        log.debug("loadCustomerList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getCustomerResource().getCustomerList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<CustomerDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<CustomerDTO>>>() {
            });
            customerList = serviceResponse.getResult();
            log.debug("customerList: {}", customerList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }
    private void loadApproverList() {
        log.debug("loadApproverList");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getCustomerResource().getApproverList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<UserDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<UserDTO>>>() {
            });
            approverList = serviceResponse.getResult();
            log.debug("approverList: {}", approverList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onChangeCustomer() {
        log.debug("onChangeCustomer. (selectedCustomerId: {})",selectedCustomerId);
        if (!customerList.isEmpty()) {
            newProject.setCustomer(getObjById(customerList,selectedCustomerId));
        }
    }

    private void loadProjectList() {
        log.debug("loadProjectList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().getProjectList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ProjectDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ProjectDTO>>>() {
            });
            projectList = serviceResponse.getResult();
            log.debug("projectList: {}", projectList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onSearchProject() {
        log.debug("onSearchProject. (searchRequest: {})",searchRequest);

        if (RecordStatus.INACTIVE.equals(searchRequest.getStatus())) {
            searchRequest.setStatus(null);
        }

        ServiceRequest<SearchRequest> request = new ServiceRequest<>(searchRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().searchProject(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ProjectDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ProjectDTO>>>() {
            });
            projectList = serviceResponse.getResult();
            log.debug("projectList: {}", projectList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreNewProject() {
        log.debug("onPreNewProject.");
        newProject = new ProjectDTO();
        newProject.setCustomer(customerList.get(0));
        editMode = false;
        validator = new Validator();
    }


    public void onNewProject() {
        log.debug("onNewProject.");

        validator = new Validator();

        // empty validation (first priority)
        validator.mustNotBlank("code",newProject.getCode(),"Code can not be empty");
        validator.mustNotBlank("name",newProject.getName(),"Name can not be empty");

        if (validator.isFailed()) {
            FacesUtil.actionFailed(validator.getMessage());
            return;
        }

        if (newProject.getBillableMDDuration().compareTo(Duration.ZERO)==0) {
            String message = "Billable-MD must greater than 0.";
            log.debug(message);
            FacesUtil.actionFailed(message);
            return;
        }

        //validation pass

        newProject.setUserID(userId);
        log.debug("onNewProject.newProject={}", newProject);
        ServiceRequest<ProjectDTO> request = new ServiceRequest<>(newProject);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().newProject(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectDTO>>() {
            });
            log.debug("response: {}", serviceResponse);
            switch (serviceResponse.getApiResponse()) {
                case SUCCESS:
                    FacesUtil.actionSuccess(serviceResponse.getApiResponse().description());
                    break;

                case FAILED:
                case EXCEPTION:
                    FacesUtil.actionFailed(serviceResponse.getMessage());
                    return;
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.actionFailed("wrong response from server!");
            return;
        }

        loadProjectList();

        FacesUtil.runClientScript("PF('projectDlg').hide();");
    }

    private void loadProjectInfo() {
        log.debug("loadProjectInfo. (selectedProjectId: {})",selectedProjectId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(selectedProjectId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().getProjectInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectDTO>>() {
            });
            newProject = serviceResponse.getResult();
            FacesUtil.actionSuccess();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.actionFailed("wrong response from server!");
        }
    }

    public void onPreUpdateProject() {
        log.debug("onPreUpdateProject.");

        loadProjectInfo();
        selectedCustomerId = newProject.getCustomer().getId();
        editMode = true;
        validator = new Validator();
    }

    public void onUpdateProject() {
        log.debug("onUpdateProject. (selectedProjectId: {})",selectedProjectId);

        //validation
        validator = new Validator();

        // empty validation (first priority)
        validator.mustNotBlank("code",newProject.getCode(),"Code can not be empty");
        validator.mustNotBlank("name",newProject.getName(),"Name can not be empty");

        if (validator.isFailed()) {
            FacesUtil.actionFailed(validator.getMessage());
            loadProjectList();
            return;
        }

        if (newProject.getBillableMDDuration().compareTo(Duration.ZERO)==0) {
            String message = "Billable-MD must greater than 0.";
            log.debug(message);
            FacesUtil.actionFailed(message);
            return;
        }

        log.debug("onUpdateProject.newProject={}", newProject);
        ServiceRequest<ProjectDTO> request = new ServiceRequest<>(newProject);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().updateProjectInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadProjectList();

        FacesUtil.runClientScript("PF('projectDlg').hide();");
    }

    public void onPreDeleteProject() {
        log.debug("onPreDeleteProject. (selectedProjectId: {})",selectedProjectId);

        loadProjectInfo();
    }

    public void onPreCloseProject() {
        log.debug("onPreCloseProject. (selectedProjectId: {})",selectedProjectId);

        loadProjectInfo();
    }

    public void onCloseProject() {
        log.debug("onCloseProject. (selectedProjectId: {})",selectedProjectId);

        ServiceRequest<ProjectDTO> request = new ServiceRequest<>(newProject);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().closeProject(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadProjectList();
    }

    public void onDeleteProject() {
        log.debug("onDeleteProject. (selectedProjectId: {})",selectedProjectId);

        ServiceRequest<ProjectDTO> request = new ServiceRequest<>(newProject);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().deleteProject(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadProjectList();
    }

    public void onManageProject() {
        log.debug("onManageProject. (selectedProjectId: {})",selectedProjectId);
        FacesUtil.getSession().setAttribute("projectId",selectedProjectId);
        FacesUtil.redirect("/site/manageProject.jsf");
    }

    public void onHundredPlusClicked() {
        newProject.setBillableMDDuration(Duration.ofHours(100));
    }

    public List<ProjectDTO> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectDTO> projectList) {
        this.projectList = projectList;
    }

    public long getSelectedProjectId() {
        return selectedProjectId;
    }

    public void setSelectedProjectId(long selectedProjectId) {
        this.selectedProjectId = selectedProjectId;
    }

    public ProjectDTO getNewProject() {
        return newProject;
    }

    public void setNewProject(ProjectDTO newProject) {
        this.newProject = newProject;
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

    public List<CustomerDTO> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<CustomerDTO> customerList) {
        this.customerList = customerList;
    }

    public long getSelectedCustomerId() {
        return selectedCustomerId;
    }

    public void setSelectedCustomerId(long selectedCustomerId) {
        this.selectedCustomerId = selectedCustomerId;
    }

    public SearchRequest getSearchRequest() {
        return searchRequest;
    }

    public void setSearchRequest(SearchRequest searchRequest) {
        this.searchRequest = searchRequest;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<UserDTO> getApproverList() {
        return approverList;
    }

    public void setApproverList(List<UserDTO> approverList) {
        this.approverList = approverList;
    }
}
