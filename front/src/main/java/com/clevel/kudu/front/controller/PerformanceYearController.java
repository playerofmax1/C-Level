package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.PerformanceYearDTO;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.util.DateTimeUtil;
import com.clevel.kudu.util.FacesUtil;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@ViewScoped
@Named("pfYearCtl")
public class PerformanceYearController extends AbstractController {

    private List<PerformanceYearDTO> performanceYearList;
    private PerformanceYearDTO selectedYear;
    private PerformanceYearDTO lastYear;
    private int currentYear;

    private PerformanceYearDTO newPerformanceYear;

    private boolean editMode;

    public PerformanceYearController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        currentYear = application.getCurrentYear();

        loadPerformanceYearList();
    }

    public void onPreNewYear() {
        log.debug("onPreNewYear.");
        editMode = false;

        int nextYear = (int) lastYear.getYear() + 1;
        Date firstDateOfYear = DateTimeUtil.getDatePlusYears(lastYear.getStartDate(), 1);
        Date lastDateOfYear = DateTimeUtil.getDatePlusYears(lastYear.getEndDate(), 1);

        log.debug("lastYear = {}", lastYear);
        log.debug("nextYear = {}", nextYear);
        log.debug("firstDateOfYear = {}", firstDateOfYear);
        log.debug("lastDateOfYear = {}", lastDateOfYear);

        newPerformanceYear = new PerformanceYearDTO();
        newPerformanceYear.setYear(nextYear);
        newPerformanceYear.setStartDate(firstDateOfYear);
        newPerformanceYear.setEndDate(lastDateOfYear);

        FacesUtil.actionSuccess();
    }

    public void onSaveNewYear() {
        log.debug("onSaveNewYear.");

        int focusYear = (int) newPerformanceYear.getYear();
        int startYear = DateTimeUtil.getYear(newPerformanceYear.getStartDate());
        int endYear = DateTimeUtil.getYear(newPerformanceYear.getEndDate());
        if (startYear != focusYear && endYear != focusYear) {
            FacesUtil.actionFailed("Need start or end at this year(" + focusYear + ")");
            return;
        }

        //validation pass
        ServiceRequest<PerformanceYearDTO> request = new ServiceRequest<>(newPerformanceYear);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getPerformanceYearResource().addLastYear(request);
        if (response.getStatus() == 200) {
            ServiceResponse<PerformanceYearDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<PerformanceYearDTO>>() {
            });
            FacesUtil.actionSuccess(serviceResponse.getApiResponse().description());
        } else {
            String msg = "wrong response status(" + response.getStatus() + ")! ";
            log.debug(msg);
            FacesUtil.actionFailed(msg);
        }

        loadPerformanceYearList();
    }

    public void onPreUpdateYear() {
        log.debug("onPreUpdateYear(selectedYear:{})", selectedYear);
        editMode = true;

        newPerformanceYear = new PerformanceYearDTO();
        newPerformanceYear.setYear(selectedYear.getYear());
        newPerformanceYear.setStartDate(selectedYear.getStartDate());
        newPerformanceYear.setEndDate(selectedYear.getEndDate());

        FacesUtil.actionSuccess();
    }

    public void onUpdatePerformanceYear() {
        log.debug("onUpdatePerformanceYear(selectedYear:{})", selectedYear);

        int focusYear = (int) newPerformanceYear.getYear();
        int startYear = DateTimeUtil.getYear(newPerformanceYear.getStartDate());
        int endYear = DateTimeUtil.getYear(newPerformanceYear.getEndDate());
        if (startYear != focusYear && endYear != focusYear) {
            FacesUtil.actionFailed("Need start or end at this year(" + focusYear + ")");
            return;
        }

        //validation pass
        ServiceRequest<PerformanceYearDTO> request = new ServiceRequest<>(newPerformanceYear);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getPerformanceYearResource().updateYear(request);
        if (response.getStatus() == 200) {
            ServiceResponse<PerformanceYearDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<PerformanceYearDTO>>() {
            });
            FacesUtil.actionSuccess(serviceResponse.getApiResponse().description());
        } else {
            String msg = "wrong response status(" + response.getStatus() + ")! ";
            log.debug(msg);
            FacesUtil.actionFailed(msg);
        }

        loadPerformanceYearList();
    }

    public void onPreDeleteYear() {
        log.debug("onPreUpdateYear(selectedYear:{})", selectedYear);

        if (selectedYear.getYear() != lastYear.getYear()) {
            FacesUtil.actionFailed("Year " + selectedYear.getYear() + " is not allowed to delete!");
            return;
        }

        FacesUtil.actionSuccess();
    }

    public void onDeleteYear() {
        log.debug("onDeleteYear(selectedYear:{})", selectedYear);

        ServiceRequest<PerformanceYearDTO> request = new ServiceRequest<>(selectedYear);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getPerformanceYearResource().deleteLastYear(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });
            APIResponse apiResponse = serviceResponse.getApiResponse();
            if (apiResponse == APIResponse.SUCCESS) {
                FacesUtil.actionSuccess("Year " + selectedYear.getYear() + " is deleted successful.");
            } else {
                FacesUtil.actionFailed(serviceResponse.getMessage());
            }
        } else {
            String msg = "wrong response status(" + response.getStatus() + ")! ";
            log.debug(msg);
            FacesUtil.actionFailed(msg);
        }

        loadPerformanceYearList();
    }

    private void loadPerformanceYearList() {
        log.debug("loadPerformanceYearList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getPerformanceYearResource().getYearList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<PerformanceYearDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<PerformanceYearDTO>>>() {
            });
            performanceYearList = serviceResponse.getResult();
            lastYear = performanceYearList.get(performanceYearList.size() - 1);
            log.debug("performanceYearList = {}", performanceYearList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public List<PerformanceYearDTO> getPerformanceYearList() {
        return performanceYearList;
    }

    public void setPerformanceYearList(List<PerformanceYearDTO> performanceYearList) {
        this.performanceYearList = performanceYearList;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public PerformanceYearDTO getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(PerformanceYearDTO selectedYear) {
        this.selectedYear = selectedYear;
    }

    public PerformanceYearDTO getNewPerformanceYear() {
        return newPerformanceYear;
    }

    public void setNewPerformanceYear(PerformanceYearDTO newPerformanceYear) {
        this.newPerformanceYear = newPerformanceYear;
    }

    public PerformanceYearDTO getLastYear() {
        return lastYear;
    }

    public void setLastYear(PerformanceYearDTO lastYear) {
        this.lastYear = lastYear;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
