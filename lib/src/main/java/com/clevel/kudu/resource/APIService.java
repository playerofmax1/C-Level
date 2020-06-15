package com.clevel.kudu.resource;

import com.clevel.kudu.api.*;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.Serializable;

@Dependent
public class APIService implements Serializable {
    @Inject
    private ServletContext context;
    private static final String restAPIUrl = "rest.api.url";

//    public LookupService getLookupResource() {
//        ResteasyClient client = new ResteasyClientBuilder().build();
//        return client.target(context.getInitParameter(restAPIUrl)).proxy(LookupService.class);
//    }
//
    public SecurityService getSecurityResource() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        return client.target(context.getInitParameter(restAPIUrl)).proxy(SecurityService.class);
    }

    public CustomerService getCustomerResource() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        return client.target(context.getInitParameter(restAPIUrl)).proxy(CustomerService.class);
    }

    public TaskService getTaskResource() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        return client.target(context.getInitParameter(restAPIUrl)).proxy(TaskService.class);
    }

    public ProjectService getProjectResource() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        return client.target(context.getInitParameter(restAPIUrl)).proxy(ProjectService.class);
    }

    public TimeSheetService getTimeSheetResource() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        return client.target(context.getInitParameter(restAPIUrl)).proxy(TimeSheetService.class);
    }

    public RateService getRateResource() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        return client.target(context.getInitParameter(restAPIUrl)).proxy(RateService.class);
    }

    public HolidayService getHolidayResource() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        return client.target(context.getInitParameter(restAPIUrl)).proxy(HolidayService.class);
    }

    public SystemService getSystemResource() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        return client.target(context.getInitParameter(restAPIUrl)).proxy(SystemService.class);
    }
}
