package com.CAUCSD.MUTCHIGI.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAPIConfig {

    @Value("${openai.api.key}")
    private String openApiKey;

    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openApiKey);
            return execution.execute(request, body);
        }));
        return restTemplate;
    }

}
