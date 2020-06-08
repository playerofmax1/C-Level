package com.clevel.kudu.api.model.converter;

import com.clevel.kudu.model.RecordStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SystemConfigConverter implements AttributeConverter<RecordStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(RecordStatus recordStatus) {
        switch (recordStatus) {
            case INACTIVE: return RecordStatus.INACTIVE.code();
            case ACTIVE: return RecordStatus.ACTIVE.code();
            case LOCK: return RecordStatus.LOCK.code();
            case CLOSE: return RecordStatus.CLOSE.code();
            case FORCE_CHANGE_PWD: return RecordStatus.FORCE_CHANGE_PWD.code();
            default: throw new IllegalArgumentException("Unknown status: " + recordStatus);
        }
    }

    @Override
    public RecordStatus convertToEntityAttribute(Integer integer) {
        switch (integer) {
            case 0: return RecordStatus.INACTIVE;
            case 1: return RecordStatus.ACTIVE;
            case 2: return RecordStatus.LOCK;
            case 3: return RecordStatus.CLOSE;
            case 10: return RecordStatus.FORCE_CHANGE_PWD;
            default: throw new IllegalArgumentException("Unknown code: " + integer);
        }
    }
}
