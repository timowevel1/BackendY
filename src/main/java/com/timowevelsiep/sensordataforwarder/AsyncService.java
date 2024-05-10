package com.timowevelsiep.sensordataforwarder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AsyncService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${secondary.backend.url}")
    private String secondaryBackendUrl;

    @Async("taskExecutor")
    public void forwardToSecondaryBackend(String path, HttpEntity<String> entity) {
        restTemplate.postForEntity(secondaryBackendUrl + path, entity, String.class);
    }
}