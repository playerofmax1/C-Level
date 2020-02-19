package com.clevel.kudu.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SimpleAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger log = LoggerFactory.getLogger(SimpleAccessDeniedHandler.class);
    private String redirectUrl;
    private static final String FACES_REQUEST_HEADER = "faces-request";

    public SimpleAccessDeniedHandler(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        String contextPath = httpServletRequest.getContextPath();
        String url = httpServletResponse.encodeRedirectURL(contextPath + redirectUrl);
        boolean ajaxRedirect = "partial/ajax".equals(httpServletRequest.getHeader(FACES_REQUEST_HEADER));
        log.debug("ajaxRedirect: {}",ajaxRedirect);
        if (e instanceof MissingCsrfTokenException) {
            log.debug("missing Csrf token redirect to: {}",url);
            if (ajaxRedirect) {
                log.debug("missing Csrf token due to ajax request, redirecting to: {}", url);

                String ajaxRedirectXml = createAjaxRedirectXml(url);
                log.debug("Ajax partial response redirect to: {}", ajaxRedirectXml);

                httpServletResponse.setContentType("text/xml");
                httpServletResponse.getWriter().write(ajaxRedirectXml);
            } else {
                httpServletResponse.sendRedirect(httpServletResponse.encodeRedirectURL(url));
            }
        } else {
            httpServletResponse.sendRedirect(httpServletResponse.encodeRedirectURL(url));
        }
    }

    private String createAjaxRedirectXml(String redirectUrl) {
        return new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<partial-response><redirect url=\"")
                .append(redirectUrl)
                .append("\"></redirect></partial-response>")
                .toString();
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
