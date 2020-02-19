package com.clevel.kudu.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SimpleRedirectStrategy implements RedirectStrategy {
    private static final Logger log = LoggerFactory.getLogger(SimpleRedirectStrategy.class);
    private static final String FACES_REQUEST_HEADER = "faces-request";

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String contextPath = request.getContextPath();
        String targetUrl = response.encodeRedirectURL(contextPath + url);
        boolean ajaxRedirect = "partial/ajax".equals(request.getHeader(FACES_REQUEST_HEADER));
        log.debug("ajaxRedirect: {}",ajaxRedirect);
            if (ajaxRedirect) {
                log.debug("missing Csrf token due to ajax request, redirecting to: {}", targetUrl);

                String ajaxRedirectXml = createAjaxRedirectXml(targetUrl);
                log.debug("Ajax partial response redirect to: {}", ajaxRedirectXml);

                response.setContentType("text/xml");
                response.getWriter().write(ajaxRedirectXml);
            } else {
                response.sendRedirect(response.encodeRedirectURL(targetUrl));
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

}
