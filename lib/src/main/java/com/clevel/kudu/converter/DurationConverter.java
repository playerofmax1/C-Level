package com.clevel.kudu.converter;

import com.clevel.kudu.util.DateTimeUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.time.Duration;

@FacesConverter("durationConverter")
public class DurationConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s == null) {
            return Duration.ZERO;
        }
        return DateTimeUtil.stringToDuration(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return DateTimeUtil.durationToString((Duration) o);
    }
}
