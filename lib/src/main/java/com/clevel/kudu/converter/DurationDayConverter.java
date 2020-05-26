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
 * Variable:Duration and Input-String:Days (1day=24hrs)
 */
@FacesConverter("durationDayConverter")
public class DurationDayConverter implements Converter {
    private Logger log= LoggerFactory.getLogger(DurationDayConverter.class);

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s == null) {
            return Duration.ZERO;
        }
        Duration duration = Duration.ofDays(Long.parseLong(s));
        return duration;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if (o == null) {
            return "0";
        }
        Duration duration = (Duration) o;
        long days = duration.toDays();
        return String.valueOf(days);
    }
}
