package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.CustomerDTO;
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
@Named("customerCtl")
public class CustomerController extends AbstractController {
    private List<CustomerDTO> customerList;
    private long selectedCustomerId;

    private CustomerDTO newCustomer;
    private boolean editMode;

    private Validator validator;

    public CustomerController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        loadCustomerList();
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

    public void onPreNewCustomer() {
        log.debug("onPreNewCustomer.");
        newCustomer = new CustomerDTO();
        editMode = false;
        validator = new Validator();
    }

    public void onNewCustomer() {
        log.debug("onNewCustomer.");

        validator = new Validator();

        // empty validation (first priority)
        validator.mustNotBlank("code",newCustomer.getCode(),"Code can not be empty");
        validator.mustNotBlank("name",newCustomer.getName(),"Name can not be empty");

        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            return;
        }

        //validation pass
        ServiceRequest<CustomerDTO> request = new ServiceRequest<>(newCustomer);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getCustomerResource().newCustomer(request);
        if (response.getStatus() == 200) {
            ServiceResponse<CustomerDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<CustomerDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadCustomerList();

        PrimeFaces.current().executeScript("PF('customerDlg').hide();");
    }

    public void loadCustomerInfo() {
        log.debug("loadCustomerInfo. (selectedCustomerId: {})",selectedCustomerId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(selectedCustomerId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getCustomerResource().getCustomerInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<CustomerDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<CustomerDTO>>() {
            });
            newCustomer = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreUpdateCustomer() {
        log.debug("onPreUpdateCustomer.");

        loadCustomerInfo();
        editMode = true;
        validator = new Validator();
    }

    public void onUpdateCustomer() {
        log.debug("onUpdateCustomer. (selectedCustomerId: {})",selectedCustomerId);

        //validation
        validator = new Validator();

        // empty validation (first priority)
        validator.mustNotBlank("code",newCustomer.getCode(),"Code can not be empty");
        validator.mustNotBlank("name",newCustomer.getName(),"Name can not be empty");

        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            loadCustomerList();
            return;
        }

        ServiceRequest<CustomerDTO> request = new ServiceRequest<>(newCustomer);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getCustomerResource().updateCustomerInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<CustomerDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<CustomerDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadCustomerList();

        PrimeFaces.current().executeScript("PF('customerDlg').hide();");
    }

    public void onPreDeleteCustomer() {
        log.debug("onPreDeleteCustomer. (selectedCustomerId: {})",selectedCustomerId);

        loadCustomerInfo();
    }

    public void onDeleteCustomer() {
        log.debug("onDeleteCustomer. (selectedCustomerId: {})",selectedCustomerId);

        ServiceRequest<CustomerDTO> request = new ServiceRequest<>(newCustomer);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getCustomerResource().deleteCustomer(request);
        if (response.getStatus() == 200) {
            ServiceResponse<CustomerDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<CustomerDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadCustomerList();
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

    public CustomerDTO getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(CustomerDTO newCustomer) {
        this.newCustomer = newCustomer;
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
