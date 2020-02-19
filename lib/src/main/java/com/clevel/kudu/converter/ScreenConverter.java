package com.clevel.kudu.converter;

import com.clevel.kudu.model.Screen;
import org.apache.commons.text.StringEscapeUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("screenConverter")
public class ScreenConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return Screen.fromNameEn(StringEscapeUtils.unescapeHtml4(s));
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        Screen screen = (Screen) o;
        return screen.getNameEn();
    }
}
