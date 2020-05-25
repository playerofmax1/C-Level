package com.clevel.kudu.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.time.Duration;

/**
 * Convert between
 * Variable:Duration and Input-String:ManDays (1md=8hrs)
 */
@FacesConverter("durationManDayConverter")
public class DurationManDayConverter implements Converter {
    private Logger log= LoggerFactory.getLogger(DurationManDayConverter.class);

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s == null) {
            return Duration.ZERO;
        }
        long hours = Long.parseLong(s) * 8;
        Duration duration = Duration.ofHours(hours);
        if (log.isDebugEnabled()) {
            log.debug("DurationManDayConverter.getAsObject({})={}({}mandays)",s,duration.toString(),duration.toHours()/8);
        }
        return duration;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if (o == null) {
            return "0";
        }
        Duration duration = (Duration) o;
        long days = duration.toHours() / 8;
        if (log.isDebugEnabled()) {
            log.debug("DurationManDayConverter.getAsString({})={}days",duration.toString(),days);
        }
        return String.valueOf(days);
    }
}
