package com.clevel.kudu.api.model.db.working;

import com.clevel.kudu.api.model.db.AbstractAuditEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "wrk_user_performance")
public class UserPerformance extends AbstractAuditEntity {

    @Column(name = "userId")
    private long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performanceYearId")
    private PerformanceYear performanceYear;

    @Column(name = "targetUtilization")
    private BigDecimal targetUtilization;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public PerformanceYear getPerformanceYear() {
        return performanceYear;
    }

    public void setPerformanceYear(PerformanceYear performanceYear) {
        this.performanceYear = performanceYear;
    }

    public BigDecimal getTargetUtilization() {
        return targetUtilization;
    }

    public void setTargetUtilization(BigDecimal targetUtilization) {
        this.targetUtilization = targetUtilization;
    }
}
