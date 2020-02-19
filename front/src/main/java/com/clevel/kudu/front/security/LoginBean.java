package com.clevel.kudu.front.security;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.security.AuthenticationRequest;
import com.clevel.kudu.dto.security.AuthenticationResult;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.model.CurrentUser;
import com.clevel.kudu.model.SpringBean;
import com.clevel.kudu.model.UserDetail;
import com.clevel.kudu.resource.APIService;
import com.clevel.kudu.util.FacesUtil;
import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.Serializable;

@Named("loginBean")
@RequestScoped
public final class LoginBean implements Serializable {
    @Inject
    private Logger log;
    @Inject
    private APIService apiService;

    private String userName;
    private String password;

    @Inject
    private SimpleAuthenticationManager authenticationManager;
    @Inject
    @SpringBean("sessionRegistry")
    private SessionRegistry sessionRegistry;
    @Inject
    @SpringBean("sas")
    private CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy;

    public void login() {
        log.debug("SessionRegistry principle size: {}", sessionRegistry.getAllPrincipals().size());

        // pre-validate username format as userLoginName@companyLoginName
        if (userName.length() > 50 || password.length() > 50) {
            log.debug("user field length: {}, password field length: {}", userName.length(), password.length());
            return;
        }

        ServiceRequest<AuthenticationRequest> authenticationRequest = new ServiceRequest<>(new AuthenticationRequest(userName, password));
        Response response = apiService.getSecurityResource().authenticate(authenticationRequest);

        if (response.getStatus() != 200) {
            log.debug("service request failed!");
            return;
        }

        ServiceResponse<AuthenticationResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<AuthenticationResult>>() {
        });
        log.debug("response: {}", serviceResponse);
        if (serviceResponse.getApiResponse() == APIResponse.FAILED || serviceResponse.getApiResponse() == APIResponse.EXCEPTION) {
            log.debug("authentication failed!");
            FacesUtil.addError("authentication failed!");
            return;
        }

        if (serviceResponse.getApiResponse() == APIResponse.ACCOUNT_DISABLED) {
            log.debug("account is disabled!");
            FacesUtil.addError("account is disabled!");
            return;
        }

        AuthenticationResult result = serviceResponse.getResult();
        log.debug("{}", result);

        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(result.getUserId());
        userDetail.setUserName(result.getUserName());
        userDetail.setName(result.getName());
        userDetail.setLastName(result.getLastName());
        userDetail.setScreenList(result.getScreenList());
        userDetail.setFunctionList(result.getFunctionList());

        userDetail.setRole("ROLE_USER");

        //spacial for maintenance
        if (userDetail.getUserName().equalsIgnoreCase("thammasak")) {
            userDetail.setRole("ROLE_SYSTEM");
        }

        try {
            UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(userDetail, this.getPassword());
            request.setDetails(new WebAuthenticationDetails(FacesUtil.getRequest()));

            Authentication authentication = authenticationManager.authenticate(request);
            log.debug("authentication result: {}", authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            compositeSessionAuthenticationStrategy.onAuthentication(request, FacesUtil.getRequest(), FacesUtil.getResponse());

            log.debug("logging in user: {}",userDetail);
            FacesUtil.redirect("/site/welcome.jsf");
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void logout() {
        log.debug("logging out.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(FacesUtil.getRequest(), FacesUtil.getResponse(), auth);
        }
        FacesUtil.redirect("/signin.jsf");
    }

    public void checkAlreadyLogin() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            UserDetail userDetail = (UserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.debug("security context holder is not null, continue redirection for user role: {}", userDetail.getRole());
            FacesUtil.redirect("/site/welcome.jsf");
        }
    }

    public UserDetail getUserDetail() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        return (UserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Produces
    @CurrentUser
    UserDetail produceUserDetail() {
        return getUserDetail();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
