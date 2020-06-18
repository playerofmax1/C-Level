package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class MandaysResult {

    private List<UserMandaysDTO> userMandaysDTOList;

    private UserMandaysDTO totalMandaysDTO;
    private UtilizationDTO utilization;

    private List<HolidayDTO> holidayList;

    private boolean hasPreviousYear;
    private boolean hasNextYear;

    public List<UserMandaysDTO> getUserMandaysDTOList() {
        return userMandaysDTOList;
    }

    public void setUserMandaysDTOList(List<UserMandaysDTO> userMandaysDTOList) {
        this.userMandaysDTOList = userMandaysDTOList;
    }

    public UserMandaysDTO getTotalMandaysDTO() {
        return totalMandaysDTO;
    }

    public void setTotalMandaysDTO(UserMandaysDTO totalMandaysDTO) {
        this.totalMandaysDTO = totalMandaysDTO;
    }

    public UtilizationDTO getUtilization() {
        return utilization;
    }

    public void setUtilization(UtilizationDTO utilization) {
        this.utilization = utilization;
    }

    public List<HolidayDTO> getHolidayList() {
        return holidayList;
    }

    public void setHolidayList(List<HolidayDTO> holidayList) {
        this.holidayList = holidayList;
    }

    public boolean isHasPreviousYear() {
        return hasPreviousYear;
    }

    public void setHasPreviousYear(boolean hasPreviousYear) {
        this.hasPreviousYear = hasPreviousYear;
    }

    public boolean isHasNextYear() {
        return hasNextYear;
    }

    public void setHasNextYear(boolean hasNextYear) {
        this.hasNextYear = hasNextYear;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("userMandaysDTOList", userMandaysDTOList)
                .append("totalMandaysDTO", totalMandaysDTO)
                .append("utilization", utilization)
                .append("holidayList", holidayList)
                .append("hasPreviousYear", hasPreviousYear)
                .append("hasNextYear", hasNextYear)
                .toString()
                .replace('=', ':');
    }
}
