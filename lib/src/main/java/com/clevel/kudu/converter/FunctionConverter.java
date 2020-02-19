package com.clevel.kudu.converter;

import com.clevel.kudu.model.Function;
import org.apache.commons.text.StringEscapeUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("functionConverter")
public class FunctionConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return Function.fromNameEn(StringEscapeUtils.unescapeHtml4(s));
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        Function function = (Function) o;
        return function.getNameEn();
    }
}
