package com.clevel.kudu.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.PropertyResourceBundle;

@SessionScoped
public class CDIBeanFactory implements Serializable {
    @Produces
    public PropertyResourceBundle getBundle() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().evaluateExpressionGet(context, "#{msg}", PropertyResourceBundle.class);
    }

    @Produces
    @SpringBean("sas")
    public CompositeSessionAuthenticationStrategy getCompositeSessionAuthenticationStrategy(InjectionPoint injectionPoint) {
        return (CompositeSessionAuthenticationStrategy) findBean(injectionPoint);
    }

    @Produces
    @SpringBean("sessionRegistry")
    public SessionRegistry getSessionRegistry(InjectionPoint injectionPoint) {
        return (SessionRegistry) findBean(injectionPoint);
    }

    private Object findBean(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        SpringBean springBeanAnnotation = annotated.getAnnotation(SpringBean.class);
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

        String name = springBeanAnnotation.value();

        if(StringUtils.isNotBlank(name))
            return WebApplicationContextUtils.getRequiredWebApplicationContext(ctx).getBean(name);
        else
            throw new NoSuchBeanDefinitionException(name, "not found in Context");

    }

}
