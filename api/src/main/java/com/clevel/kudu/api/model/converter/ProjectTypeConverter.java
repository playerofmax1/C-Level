package com.clevel.kudu.api.model.converter;

import com.clevel.kudu.model.ProjectType;

import javax.persistence.AttributeConverter;

public class ProjectTypeConverter implements AttributeConverter<ProjectType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProjectType projectType) {
        switch (projectType) {
            case NORMAL: return ProjectType.NORMAL.code();
            case MAINTENANCE: return ProjectType.MAINTENANCE.code();
            default: throw new IllegalArgumentException("Unknown status: " + projectType);
        }
    }

    @Override
    public ProjectType convertToEntityAttribute(Integer integer) {
        switch (integer) {
            case 1:
                return ProjectType.NORMAL;
            case 2:
                return ProjectType.MAINTENANCE;
            default:
                throw new IllegalArgumentException("Unknown code: " + integer);
        }
    }
}
