package com.clevel.kudu.api.model.converter;

import com.clevel.kudu.model.TaskType;

import javax.persistence.AttributeConverter;

public class TaskTypeConverter implements AttributeConverter<TaskType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TaskType taskType) {
        switch (taskType) {
            case LEAVE: return TaskType.LEAVE.code();
            case GENERAL: return TaskType.GENERAL.code();
            default: throw new IllegalArgumentException("Unknown status: " + taskType);
        }
    }

    @Override
    public TaskType convertToEntityAttribute(Integer integer) {
        switch (integer) {
            case 1:
                return TaskType.GENERAL;
            case 2:
                return TaskType.LEAVE;
            default:
                throw new IllegalArgumentException("Unknown code: " + integer);
        }
    }
}
