package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TaskRequest {
    private boolean chargeable;
    private boolean nonChargeAble;
    private boolean all;

    public TaskRequest() {
    }

    public TaskRequest(boolean chargeable, boolean nonChargeAble, boolean all) {
        this.chargeable = chargeable;
        this.nonChargeAble = nonChargeAble;
        this.all = all;
    }

    public boolean isChargeable() {
        return chargeable;
    }

    public void setChargeable(boolean chargeable) {
        this.chargeable = chargeable;
    }

    public boolean isNonChargeAble() {
        return nonChargeAble;
    }

    public void setNonChargeAble(boolean nonChargeAble) {
        this.nonChargeAble = nonChargeAble;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("chargeable", chargeable)
                .append("nonChargeAble", nonChargeAble)
                .append("all", all)
                .toString();
    }
}
