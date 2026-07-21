package com.hostel.accommodation.config;

import com.hostel.accommodation.util.SecurityContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            String token = SecurityContextUtil.getToken();
            System.out.println("TOKEN IN INTERCEPTOR: " + token);
            if (token != null) {
                request.getHeaders().set("Authorization", "Bearer " + token);
            }
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
