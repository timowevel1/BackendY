package com.timowevelsiep.sensordataforwarder;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.*;
import java.io.IOException;
import java.util.Enumeration;

@Component
public class LoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        filterChain.doFilter(wrappedRequest, servletResponse);

        logRequestDetails(wrappedRequest);
    }

    private void logRequestDetails(HttpServletRequest request) throws IOException {
        String uri = request.getRequestURI();
        HttpHeaders headers = new HttpHeaders();

        // Extrahieren und Loggen der Header mit einer Enumeration
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> values = request.getHeaders(headerName);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                headers.add(headerName, value);
            }
        }

        log.info("Request URI: {}", uri);
        log.info("Request Headers: {}", headers);
        log.info("Request Body: {}", new String(request.getInputStream().readAllBytes()));
    }
}
