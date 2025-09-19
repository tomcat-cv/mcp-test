package com.example.mcp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebClientConfig {
	@Bean
	public RestTemplate downstreamRestTemplate(DownstreamProperties props,
	                                         @Value("${spring.application.name:mcp-forwarder}") String appName) {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(5000); // 5秒连接超时
		factory.setReadTimeout(30000);   // 30秒读取超时
		
		RestTemplate restTemplate = new RestTemplate(factory);
		
		// 设置默认的User-Agent头
		restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().set("User-Agent", appName + "/1.0");
			return execution.execute(request, body);
		});
		
		return restTemplate;
	}
}
