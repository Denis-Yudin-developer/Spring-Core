package com.edu.ulab.app.config;

import com.edu.ulab.app.web.constant.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Value("$1.0")
    private String swaggerApiVersion;

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title(SwaggerInfo.TITLE)
                .description(SwaggerInfo.DESCRIPTION)
                .version(swaggerApiVersion)
        );
    }
}
