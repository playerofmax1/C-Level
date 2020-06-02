package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class MandaysResult {

    private List<UserMandaysDTO> userMandaysDTOList;

    private UserMandaysDTO totalMandaysDTO;
    private UtilizationDTO utilization;

    private List<HolidayDTO> holidayList;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("userMandaysDTOList", userMandaysDTOList)
                .append("totalMandaysDTO", totalMandaysDTO)
                .append("utilization", utilization)
                .append("holidayList", holidayList)
                .toString()
                .replace('=', ':');
    }
}
