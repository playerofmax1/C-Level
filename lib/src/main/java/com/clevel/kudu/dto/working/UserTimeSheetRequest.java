package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class UserTimeSheetRequest {
    private long ownerUserId;
    private List<UserTimeSheetDTO> userTimeSheetList;

    public UserTimeSheetRequest() {
    }

    public UserTimeSheetRequest(long ownerUserId, List<UserTimeSheetDTO> userTimeSheetList) {
        this.ownerUserId = ownerUserId;
        this.userTimeSheetList = userTimeSheetList;
    }

    public long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public List<UserTimeSheetDTO> getUserTimeSheetList() {
        return userTimeSheetList;
    }

    public void setUserTimeSheetList(List<UserTimeSheetDTO> userTimeSheetList) {
        this.userTimeSheetList = userTimeSheetList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("ownerUserId", ownerUserId)
                .append("userTimeSheetList", userTimeSheetList)
                .toString();
    }
}
