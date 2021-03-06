package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.rpt.MandaysReportRequest;
import com.clevel.kudu.dto.rpt.MandaysReportResult;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.model.Function;
import com.clevel.kudu.util.DateTimeUtil;
import com.clevel.kudu.util.ExcelUtil;
import com.clevel.kudu.util.FacesUtil;
import com.clevel.kudu.util.LookupUtil;
import org.apache.commons.math3.util.Pair;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private PerformanceYearDTO currentYear;
    private boolean previousEnable;
    private boolean nextEnable;
    private boolean hasNextYear;
    private boolean hasPreviousYear;
    private Date tsStartDate;
    private List<HolidayDTO> holidayList;

    private List<UserMandaysDTO> userMandaysDTOList;
    private UserMandaysDTO totalUserMandays;
    private UtilizationDTO utilization;
    private BigDecimal targetUtilization;

    private String br = "<br/>";

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        currentYear = new PerformanceYearDTO();
        selectedUserId = userDetail.getUserId();

        if (accessControl.functionEnable(Function.F0002)) {
            loadUserList();
        }

        onChangeUser();
    }

    public void onChangeUser() {
        log.debug("onChangeUser. (selectedUserId: {})", selectedUserId);
        currentYear.setYear(0);

        if (userList == null) {
            tsStartDate = userDetail.getTsStartDate();
        } else {
            UserDTO selectedUser = LookupUtil.getObjById(userList, selectedUserId);
            log.debug("onChangeUser.selectedUser: {}", selectedUser);
            tsStartDate = selectedUser.getTsStartDate();
        }
        log.debug("onChangeUser.tsStartDate: {}", tsStartDate);

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

            UserDTO currentUser = new UserDTO();
            currentUser.setId(selectedUserId);
            currentUser.setName(userDetail.getName());
            currentUser.setLastName(userDetail.getLastName());
            currentUser.setTsStartDate(userDetail.getTsStartDate());

            userList = new ArrayList<>();
            userList.add(currentUser);

            List<UserTimeSheetDTO> userTSList = serviceResponse.getResult();
            for (UserTimeSheetDTO u : userTSList) {
                userList.add(u.getTimeSheetUser());
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

    }

    public StreamedContent onExcelTemplate() {
        String templatePath = application.getAppPath() + "/WEB-INF/xlsx/";
        String tmpPath = templatePath + "tmp/";
        String templateFileName = "report_mandays.xlsx";
        String outputFileName = templateFileName.substring(0, templateFileName.length() - 5) + "." + DateTimeUtil.getDateStr(new Date(), "yyyyMMdd.HHmmss") + ".xlsx";
        String outputSheetName = "Time Sheet Summary";
        String aliasFileName = "report_mandays.xlsx";
        log.debug("onExcelTemplate.");

        /*clean previous output files*/
        File tmpPathFile = new File(tmpPath);
        FileSystemUtils.deleteRecursively(tmpPathFile);
        log.debug("onExcelTemplate.createTempFolder passed");

        /*load data for report*/
        MandaysReportResult mandaysReportResult = loadMandaysReport();
        if (mandaysReportResult == null) {
            log.debug("onExcelTemplate.loadMandaysReport failed");
            return null;
        }
        log.debug("onExcelTemplate.loadMandaysReport passed, mandaysReportResult = {}", mandaysReportResult);

        /*create output file*/
        String outputFullFileName = tmpPath + outputFileName;
        createExcelReport(templatePath + templateFileName, outputFullFileName, outputSheetName, mandaysReportResult);
        log.debug("onExcelTemplate.createExcelReport passed");

        /*download output file*/
        try {
            String mimeType = "application/vnd.ms-excel";
            log.debug("download(alias:{}, file:{}, mimeType:{})", aliasFileName, outputFullFileName, mimeType);
            return new DefaultStreamedContent(new FileInputStream(outputFullFileName), mimeType, aliasFileName);

        } catch (FileNotFoundException ex) {
            String errorMessage = "Download failed: " + ex.getMessage();
            log.error(errorMessage);
            FacesUtil.actionFailed(errorMessage);
            return null;
        }
    }

    private MandaysReportResult loadMandaysReport() {
        log.debug("loadMandaysReport(year: {})", currentYear.getYear());

        MandaysReportRequest mandaysReportRequest = new MandaysReportRequest();
        mandaysReportRequest.setYear((int) currentYear.getYear());

        ServiceRequest<MandaysReportRequest> request = new ServiceRequest<>(mandaysReportRequest);
        request.setUserId(userDetail.getUserId());

        Response response = apiService.getTimeSheetResource().getMandaysReport(request);
        if (response.getStatus() != 200) {
            String message = "Call API is failed by connection problem(" + response.getStatus() + ")!";
            log.debug(message);
            FacesUtil.addError(message);
            return null;
        }

        ServiceResponse<MandaysReportResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<MandaysReportResult>>() {
        });
        log.debug("serviceResponse = {}", serviceResponse);
        if (serviceResponse.getApiResponse() != APIResponse.SUCCESS) {
            String message = "loadMandaysReport is failed! " + serviceResponse.getMessage();
            log.debug(message);
            FacesUtil.addError(message);
            return null;
        }

        return serviceResponse.getResult();
    }

    @SuppressWarnings("unchecked")
    private void createExcelReport(String templateFileName, String outputFileName, String outputSheetName, MandaysReportResult mandaysReportResult) {
        String errMessage;/*create output file*/
        try {
            /*Read Template as InputStream and create the output as OutputStream for JETT, JXLS v.1*/
            /*InputStream templateFileInputStream = new BufferedInputStream(new FileInputStream(templateFileName));
            FileOutputStream fileOutputStream = new FileOutputStream(outputFileName);
            log.debug("templateFile is ready.");*/

            /*Create Variable Map for JETT, JXLS v.1*/
            /*HashMap<String, Object> beans = new HashMap<>();
            beans.put("title", "This is TITLE");
            beans.put("mandaysList", userMandaysDTOList);*/

            /*Create Variable Map for ExcelUtil on JXLS v.2*/
            List<Pair<String, Object>> variableList = new ArrayList<>();
            variableList.add(new Pair<>("datetime", DateTimeUtil.getDateStr(DateTimeUtil.now(), "dd/MM/yyyy HH:mm:ss")));
            variableList.add(new Pair<>("user", userDetail.getName() + " " + userDetail.getLastName()));
            PerformanceYearDTO performanceYear = mandaysReportResult.getPerformanceYear();
            variableList.add(new Pair<>("year", performanceYear.getYear()));
            variableList.add(new Pair<>("yearStartDate", DateTimeUtil.getDateStr(performanceYear.getStartDate(), "dd/MM/yyyy")));
            variableList.add(new Pair<>("yearEndDate", DateTimeUtil.getDateStr(performanceYear.getEndDate(), "dd/MM/yyyy")));
            Pair<String, Object>[] variables = new Pair[2];
            variables = variableList.toArray(variables);

            /*Using JETT - Generate excel workbook by template and variableMap*/
            /*ExcelTransformer transformer = new ExcelTransformer();
            Workbook workbook = transformer.transform(templateFileInputStream, beans);*/

            /*[Tested-OK-don't-support-for-to-the-right]Using JXLS v.1 - Generate excel workbook by template and variableMap*/
            /*XLSTransformer xlsTransformer = new XLSTransformer();
            Workbook workbook = xlsTransformer.transformXLS(templateFileInputStream, beans);
            log.debug("workbook is ready.");*/

            /*Using JXLS v.2 - Generate excel file by template*/
            log.debug("createExcelReport.mandaysReportResult = {}", mandaysReportResult);
            ExcelUtil.createExcel(outputFileName, outputSheetName, templateFileName, mandaysReportResult.getReportItemList(), mandaysReportResult.getProjectList(), variables);

            /*Create output file*/
            /*workbook.write(fileOutputStream);
            fileOutputStream.close();*/
            log.debug("response is ready.");

            //FacesUtil.actionSuccess("Success");

        } catch (IOException e) {
            errMessage = e.getMessage();
            log.error("IOException reading excel-template: {}", errMessage);
            FacesUtil.actionFailed("Failed, " + errMessage);

        /*} catch (InvalidFormatException e) {
            errMessage = e.getMessage();
            log.error("InvalidFormatException reading excel-template: {}", errMessage);
            FacesUtil.actionFailed("Failed, " + errMessage);*/

        } catch (RuntimeException e) {
            errMessage = e.getMessage();
            log.error("Please check mapping list for excel-template: {}", errMessage);
            FacesUtil.actionFailed("Failed, " + errMessage);

        } catch (Exception e) {
            errMessage = e.getMessage();
            log.error("Reading excel-template failed: {}", errMessage);
            FacesUtil.actionFailed("Failed, " + errMessage);
        }
    }

    public void onPrevious() {
        log.debug("onPrevious.");
        currentYear.setYear(currentYear.getYear() - 1);
        loadMandays();
    }

    public void onToDay() {
        log.debug("onToDay.");
        currentYear.setYear(0);
        loadMandays();
    }

    public void onNext() {
        log.debug("onNext.");
        currentYear.setYear(currentYear.getYear() + 1);
        loadMandays();
    }

    public void checkNavigationButtonEnable() {
        nextEnable = hasNextYear;

        long previousYear = currentYear.getYear() - 1;
        Date firstDateOfMonth = DateTimeUtil.getLastDateOfYear((int) previousYear);
        previousEnable = hasPreviousYear && tsStartDate.before(firstDateOfMonth);
    }

    private void loadMandays() {
        if (selectedUserId == 0) {
            selectedUserId = userDetail.getUserId();
        }
        log.debug("loadMandays. (timeSheetUserId: {})", selectedUserId);

        MandaysRequest mandaysRequest = new MandaysRequest();
        mandaysRequest.setUserId(selectedUserId);
        mandaysRequest.setYear((int) currentYear.getYear());

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

        currentYear.setYear(utilization.getYear());
        currentYear.setStartDate(utilization.getStartDate());
        currentYear.setEndDate(utilization.getEndDate());

        if (userMandaysDTOList.size() > 0) {
            UserMandaysDTO firstUserMandaysDTO = userMandaysDTOList.get(0);
            targetUtilization = firstUserMandaysDTO.getTargetPercentCU();
            log.debug("targetUtilization = {}", targetUtilization);
            log.debug("firstUserMandaysDTO = {}", firstUserMandaysDTO);
        } else {
            targetUtilization = BigDecimal.ZERO;
            log.debug("userMandaysDTOList is empty.");
        }
        holidayList = mandaysResult.getHolidayList();

        normalize(totalUserMandays, userMandaysDTOList);

        hasNextYear = mandaysResult.isHasNextYear();
        hasPreviousYear = mandaysResult.isHasPreviousYear();
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

    public PerformanceYearDTO getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(PerformanceYearDTO currentYear) {
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

    public List<HolidayDTO> getHolidayList() {
        return holidayList;
    }

    public void setHolidayList(List<HolidayDTO> holidayList) {
        this.holidayList = holidayList;
    }

    public String getBr() {
        return br;
    }

    public String getTotalLabel() {
        return "TOTAL<br/><sup>(exclude A00X)</sup>";
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
        targetUtilizationRequest.setYear((int) currentYear.getYear());
        targetUtilizationRequest.setTargetUtilization(targetUtilization);
        log.debug("onSaveTargetUtilization.targetUtilizationRequest = {}", targetUtilizationRequest);

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
