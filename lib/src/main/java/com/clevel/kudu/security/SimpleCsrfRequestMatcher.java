package com.clevel.kudu.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class SimpleCsrfRequestMatcher implements RequestMatcher {
    private static final Logger log = LoggerFactory.getLogger(SimpleCsrfRequestMatcher.class);
    private static final Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
    private List<RequestMatcher> unprotectedUrlList;

    public SimpleCsrfRequestMatcher() {
        unprotectedUrlList = Collections.emptyList();
    }

    public SimpleCsrfRequestMatcher(String urls) {
        String[] str = urls.split(",");
        unprotectedUrlList = new ArrayList<>();
        RequestMatcher requestMatcher;
        for (String s: str) {
            requestMatcher = new RegexRequestMatcher(s,null);
            unprotectedUrlList.add(requestMatcher);
            log.debug("un-protected CSRF url: {}",s);
        }
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if(allowedMethods.matcher(request.getMethod()).matches()){
            return false;
        }

        for (RequestMatcher r: unprotectedUrlList) {
            if (r.matches(request)) {
                return false;
            }
        }
        return true;
    }

}
