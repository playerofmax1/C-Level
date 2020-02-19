package com.clevel.kudu.api.model.db;

import com.clevel.kudu.api.model.db.working.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class AbstractAuditEntity extends AbstractEntity {
    @Column(name = "createDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createBy", nullable = false)
    private User createBy;
    @Column(name = "modifyDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modifyBy", nullable = false)
    private User modifyBy;

    @Version
    private long version;

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public User getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(User modifyBy) {
        this.modifyBy = modifyBy;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        if (this.version != version) {
            throw new OptimisticLockException("Wrong object version! (this: "+this.version+", coming: "+version+")");
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("createDate", createDate)
                .append("createBy", createBy)
                .append("modifyDate", modifyDate)
                .append("modifyBy", modifyBy)
                .append("version", version)
                .toString();
    }
}
