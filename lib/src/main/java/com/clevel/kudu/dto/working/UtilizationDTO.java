package com.clevel.kudu.dto.working;

import com.clevel.kudu.util.DateTimeUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class UtilizationDTO {

    private long year;
    private Date startDate;
    private Date endDate;

    private long daysInYearExcludeWeekends;
    private long holidaysInYear;
    private long netWorkingDays;
    private long netWorkingDaysInMinutes;
    private long chargedMinutes;
    private BigDecimal percentCU;
    private BigDecimal percentAMD;

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public long getDays() {
        if (startDate == null || endDate == null) {
            return 0L;
        }
        return DateTimeUtil.countDay(startDate, endDate);
    }

    public long getDaysInYearExcludeWeekends() {
        return daysInYearExcludeWeekends;
    }

    public void setDaysInYearExcludeWeekends(long daysInYearExcludeWeekends) {
        this.daysInYearExcludeWeekends = daysInYearExcludeWeekends;
    }

    public long getHolidaysInYear() {
        return holidaysInYear;
    }

    public void setHolidaysInYear(long holidaysInYear) {
        this.holidaysInYear = holidaysInYear;
    }

    public long getNetWorkingDays() {
        return netWorkingDays;
    }

    public void setNetWorkingDays(long netWorkingDays) {
        this.netWorkingDays = netWorkingDays;
    }

    public long getChargedMinutes() {
        return chargedMinutes;
    }

    public void setChargedMinutes(long chargedMinutes) {
        this.chargedMinutes = chargedMinutes;
    }

    public BigDecimal getChargedDays() {
        BigDecimal chargedMinutes = BigDecimal.valueOf(this.chargedMinutes);
        return chargedMinutes.divide(BigDecimal.valueOf(60L), 2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(8L), 2, RoundingMode.HALF_UP);
    }

    public long getNetWorkingDaysInMinutes() {
        return netWorkingDaysInMinutes;
    }

    public void setNetWorkingDaysInMinutes(long netWorkingDaysInMinutes) {
        this.netWorkingDaysInMinutes = netWorkingDaysInMinutes;
    }

    public BigDecimal getPercentCU() {
        return percentCU;
    }

    public void setPercentCU(BigDecimal percentCU) {
        this.percentCU = percentCU;
    }

    public BigDecimal getPercentCUByDays() {
        BigDecimal percentCURecheck = getChargedDays().multiply(BigDecimal.valueOf(100));
        percentCURecheck = percentCURecheck.divide(BigDecimal.valueOf(netWorkingDays), 2, RoundingMode.HALF_UP);
        return percentCURecheck;
    }

    public BigDecimal getPercentCUByMinutes() {
        BigDecimal percentCURecheck = BigDecimal.valueOf(chargedMinutes * 100L);
        percentCURecheck = percentCURecheck.divide(BigDecimal.valueOf(netWorkingDaysInMinutes), 2, RoundingMode.HALF_UP);
        return percentCURecheck;
    }

    public BigDecimal getPercentAMD() {
        return percentAMD;
    }

    public void setPercentAMD(BigDecimal percentAMD) {
        this.percentAMD = percentAMD;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("year", year)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .append("daysInYearExcludeWeekends", daysInYearExcludeWeekends)
                .append("holidaysInYear", holidaysInYear)
                .append("netWorkingDays", netWorkingDays)
                .append("netWorkingDaysInMinutes", netWorkingDaysInMinutes)
                .append("chargedMinutes", chargedMinutes)
                .append("percentCU", percentCU)
                .append("percentAMD", percentAMD)
                .toString()
                .replace('=', ':');
    }
}
