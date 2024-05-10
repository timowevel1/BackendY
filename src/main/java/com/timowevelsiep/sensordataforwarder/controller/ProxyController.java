package com.timowevelsiep.sensordataforwarder.controller;

import com.timowevelsiep.sensordataforwarder.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@RestController
public class ProxyController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AsyncService asyncService;

    @Value("${main.backend.url}")
    private String mainBackendUrl;

    @RequestMapping("/**")
    public ResponseEntity<String> proxyRequest(HttpServletRequest request, @RequestBody(required = false) String body) throws IOException {
        String path = request.getRequestURI();
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.add(headerName, request.getHeader(headerName));
        }

        // Weiterleitung an das Haupt-Backend
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(mainBackendUrl + path, entity, String.class);

        // Asynchrone Weiterleitung an das sekundäre Backend
        asyncService.forwardToSecondaryBackend(path, entity);

        // Loggen der Antwort
        System.out.println("Response: " + response.getBody());

        return response;
    }
}
