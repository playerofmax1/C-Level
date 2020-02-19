package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.CustomerDTO;
import com.clevel.kudu.dto.working.ProjectDTO;
import com.clevel.kudu.dto.working.SearchRequest;
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

    public ProjectController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");
        searchRequest = new SearchRequest();

        loadCustomerList();
        loadProjectList();
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
            FacesUtil.addError(validator.getMessage());
            return;
        }

        //validation pass
        ServiceRequest<ProjectDTO> request = new ServiceRequest<>(newProject);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().newProject(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectDTO>>() {
            });
            log.debug("response: {}",serviceResponse);
            switch (serviceResponse.getApiResponse()) {
                case SUCCESS:
                    FacesUtil.addInfo(serviceResponse.getApiResponse().description());
                    break;
                case FAILED:
                case EXCEPTION:
                    FacesUtil.addError(serviceResponse.getMessage());
                    break;
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadProjectList();

        PrimeFaces.current().executeScript("PF('projectDlg').hide();");
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
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
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
            FacesUtil.addError(validator.getMessage());
            loadProjectList();
            return;
        }

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

        PrimeFaces.current().executeScript("PF('projectDlg').hide();");
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
}