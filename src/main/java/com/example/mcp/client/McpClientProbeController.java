package com.example.mcp.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/client/mcp", produces = MediaType.APPLICATION_JSON_VALUE)
public class McpClientProbeController {

    private final ListableBeanFactory beanFactory;

    public McpClientProbeController(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @GetMapping("/health")
    public Mono<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        int clientBeans = beanFactory.getBeanDefinitionCount();
        boolean hasMcpClient = false;
        for (String name : beanFactory.getBeanDefinitionNames()) {
            if (name.toLowerCase().contains("mcp") && name.toLowerCase().contains("client")) {
                hasMcpClient = true;
                break;
            }
        }
        result.put("beanCount", clientBeans);
        result.put("hasMcpClientBeanName", hasMcpClient);
        result.put("status", hasMcpClient ? "UP" : "UNKNOWN");
        return Mono.just(result);
    }
}


