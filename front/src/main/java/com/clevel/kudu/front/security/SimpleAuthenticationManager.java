package com.clevel.kudu.front.security;

import com.clevel.kudu.model.UserDetail;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Named
public class SimpleAuthenticationManager implements AuthenticationManager {
    @Inject
    Logger log;

    @Inject
    public SimpleAuthenticationManager() {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        log.debug("authenticate: {}", userDetail);

        WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails) authentication.getDetails();

        // admin system
        if ("ROLE_SYSTEM".equalsIgnoreCase(userDetail.getRole())) {
            log.debug("system role.");
            return getAuthority(userDetail, authentication, authenticationDetails);
        }
        // user admin
        if ("ROLE_ADMIN".equalsIgnoreCase(userDetail.getRole())) {
            log.debug("admin role.");
            return getAuthority(userDetail, authentication, authenticationDetails);
        }
        // user user
        if ("ROLE_USER".equalsIgnoreCase(userDetail.getRole())) {
            log.debug("user role.");
            return getAuthority(userDetail, authentication, authenticationDetails);
        }

        throw new BadCredentialsException("Bad Credentials");
    }

    private UsernamePasswordAuthenticationToken getAuthority(UserDetail userDetail, Authentication authentication, WebAuthenticationDetails authenticationDetails) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(userDetail.getRole()));
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userDetail,
                authentication.getCredentials(), grantedAuthorities);
        result.setDetails(authenticationDetails);
        return result;
    }
}

