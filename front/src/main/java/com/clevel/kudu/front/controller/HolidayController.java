package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.HolidayDTO;
import com.clevel.kudu.front.validation.Validator;
import com.clevel.kudu.util.DateTimeUtil;
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
@Named("holidayCtl")
public class HolidayController extends AbstractController {
    private List<HolidayDTO> holidayList;
    private long selectedHolidayId;

    private HolidayDTO newHoliday;
    private boolean editMode;

    private Validator validator;

    public HolidayController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        loadHolidayList();
    }

    private void loadHolidayList() {
        log.debug("loadHolidayList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getHolidayResource().getHolidayList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<HolidayDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<HolidayDTO>>>() {
            });
            holidayList = serviceResponse.getResult();
            log.debug("holidayList: {}", holidayList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreNewHoliday() {
        log.debug("onPreNewHoliday.");
        newHoliday = new HolidayDTO();
        editMode = false;
//        validator = new Validator();
    }

    public void onNewHoliday() {
        log.debug("onNewHoliday.");

//        validator = new Validator();

        //validation pass
        ServiceRequest<HolidayDTO> request = new ServiceRequest<>(newHoliday);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getHolidayResource().newHoliday(request);
        if (response.getStatus() == 200) {
            ServiceResponse<HolidayDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<HolidayDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadHolidayList();

        PrimeFaces.current().executeScript("PF('holidayDlg').hide();");
    }

    private void loadHolidayInfo() {
        log.debug("loadHolidayInfo. (selectedHolidayId: {})",selectedHolidayId);

        newHoliday = getObjById(holidayList,selectedHolidayId);
        ServiceRequest<HolidayDTO> request = new ServiceRequest<>(newHoliday);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getHolidayResource().getHolidayInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<HolidayDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<HolidayDTO>>() {
            });
            newHoliday = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }


    }
    public void onPreUpdateHoliday() {
        log.debug("onPreUpdateHoliday. (selectedHolidayId: {})",selectedHolidayId);

        loadHolidayInfo();
        editMode = true;
//        validator = new Validator();
    }

    public void onUpdateHoliday() {
        log.debug("onUpdateHoliday. (selectedHolidayId: {})",selectedHolidayId);

        //validation
//        validator = new Validator();

        ServiceRequest<HolidayDTO> request = new ServiceRequest<>(newHoliday);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getHolidayResource().updateHolidayInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<HolidayDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<HolidayDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadHolidayList();

        PrimeFaces.current().executeScript("PF('holidayDlg').hide();");
    }

    public void onPreDeleteHoliday() {
        log.debug("onPreDeleteHoliday. (selectedHolidayId: {})",selectedHolidayId);

        loadHolidayInfo();
    }

    public void onDeleteHoliday() {
        log.debug("onDeleteHoliday. (selectedHolidayId: {})",selectedHolidayId);

        ServiceRequest<HolidayDTO> request = new ServiceRequest<>(newHoliday);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getHolidayResource().deleteHoliday(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadHolidayList();
    }

    public String getActionDate() {
        return (newHoliday!=null)?DateTimeUtil.getDateStr(newHoliday.getHolidayDate()):"";
    }

    public List<HolidayDTO> getHolidayList() {
        return holidayList;
    }

    public void setHolidayList(List<HolidayDTO> holidayList) {
        this.holidayList = holidayList;
    }

    public long getSelectedHolidayId() {
        return selectedHolidayId;
    }

    public void setSelectedHolidayId(long selectedHolidayId) {
        this.selectedHolidayId = selectedHolidayId;
    }

    public HolidayDTO getNewHoliday() {
        return newHoliday;
    }

    public void setNewHoliday(HolidayDTO newHoliday) {
        this.newHoliday = newHoliday;
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
