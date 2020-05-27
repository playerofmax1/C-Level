package com.clevel.kudu.converter;

import com.clevel.kudu.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Convert between:
 * BigDecimal with 1.00 is 100%
 * String with 100 is 100%
 */
@FacesConverter("percentConverter")
public class PercentConverter implements Converter {
    private Logger log = LoggerFactory.getLogger(PercentConverter.class);

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return getBigDecimal(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        BigDecimal percent;
        if (o instanceof BigDecimal) {
            log.debug("getAsString(BigDecimal: {})", o);
            percent = (BigDecimal) o;
        } else {
            log.debug("getAsString(Object: {})", o);
            percent = getBigDecimal(o.toString());
        }
        percent = percent.multiply(BigDecimal.valueOf(100.00)).setScale(DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP);
        return percent.toString();
    }

    private BigDecimal getBigDecimal(String decimalString) {
        log.debug("getBigDecimal(decimalString:{})", decimalString);
        BigDecimal percent = new BigDecimal(decimalString);
        percent = percent.divide(BigDecimal.valueOf(100.00), DateTimeUtil.DEFAULT_SCALE, RoundingMode.HALF_UP);
        return percent;
    }

}
