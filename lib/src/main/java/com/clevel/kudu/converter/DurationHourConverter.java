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
 * Variable:Duration and Input-String:Hours
 */
@FacesConverter("durationHourConverter")
public class DurationHourConverter implements Converter {
    private Logger log = LoggerFactory.getLogger(DurationHourConverter.class);

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String hoursString) {
        if (hoursString == null) {
            return Duration.ZERO;
        }
        long minutes = BigDecimal.valueOf(Double.parseDouble(hoursString) * 60.0).longValue();
        Duration duration = Duration.ofMinutes(minutes);
        if (log.isDebugEnabled()) {
            log.debug("DurationHourConverter.getAsObject({}) = {}minutes", hoursString, minutes);
            log.debug("DurationHourConverter.getAsObject({}) = {}mandays", hoursString, DateTimeUtil.getManDays(minutes));
        }
        return duration;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object durationObject) {
        if (durationObject == null) {
            return "0";
        }
        Duration duration = (Duration) durationObject;
        /*long minutes = duration.toMinutes();*/
        long hours = duration.toHours();
        if (log.isDebugEnabled()) {
            log.debug("DurationManDayConverter.getAsString({})={}hours", duration.toString(), hours);
        }
        if (hours < 10) {
            return "0" + String.valueOf(hours);
        }
        return String.valueOf(hours);
    }
}
