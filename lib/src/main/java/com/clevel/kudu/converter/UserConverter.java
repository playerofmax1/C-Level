package com.clevel.kudu.converter;

import com.clevel.kudu.dto.working.UserDTO;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.util.List;

@FacesConverter("userConverter")
public class UserConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return getObjectFromUIPickListComponent(uiComponent,s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        String string;
        if (o == null) {
            string = "";
        } else {
            try {
                string = String.valueOf(((UserDTO) o).getId());
            } catch (ClassCastException cce) {
                throw new ConverterException();
            }
        }
        return string;
    }

    @SuppressWarnings("unchecked")
    private UserDTO getObjectFromUIPickListComponent(UIComponent component, String value) {
        final DualListModel<UserDTO> dualList;
        try {
            dualList = (DualListModel<UserDTO>) ((PickList) component).getValue();
            UserDTO resource = getObjectFromList(dualList.getSource(), Long.valueOf(value));
            if (resource == null) {
                resource = getObjectFromList(dualList.getTarget(), Long.valueOf(value));
            }

            return resource;
        } catch (ClassCastException | NumberFormatException cce) {
            throw new ConverterException();
        }
    }

    private UserDTO getObjectFromList(final List<?> list, final Long identifier) {
        for (final Object object : list) {
            final UserDTO resource = (UserDTO) object;
            if (resource.getId() == identifier) {
                return resource;
            }
        }
        return null;
    }
}
