package com.clevel.kudu.converter;

import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.math.BigDecimal;
import java.time.Duration;

/**
 * Convert between
 * Variable:Duration and Input-String:ManDays (1.0md=8hrs)
 */
@FacesConverter("durationManDayConverter")
public class DurationManDayConverter implements Converter {
    private Logger log = LoggerFactory.getLogger(DurationManDayConverter.class);

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String mandaysString) {
        if (mandaysString == null) {
            return Duration.ZERO;
        }
        long minutes = BigDecimal.valueOf(Double.parseDouble(mandaysString) * 8.0 * 60.0).longValue();
        Duration duration = Duration.ofMinutes(minutes);
        if (log.isDebugEnabled()) {
            log.debug("DurationManDayConverter.getAsObject({}) = {}minutes", mandaysString, minutes);
            log.debug("DurationManDayConverter.getAsObject({}) = {}mandays", mandaysString, DateTimeUtil.getManDays(minutes));
        }
        return duration;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object durationObject) {
        if (durationObject == null) {
            return "0";
        }
        Duration duration = (Duration) durationObject;
        long minutes = duration.toMinutes();
        BigDecimal mandays = DateTimeUtil.getManDays(minutes);
        if (log.isDebugEnabled()) {
            log.debug("DurationManDayConverter.getAsString({})={}minutes", duration.toString(), minutes);
            log.debug("DurationManDayConverter.getAsString({})={}mandays", duration.toString(), mandays);
        }
        return mandays.toString();
    }
}
