package com.boatsafari.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve EXTERNAL uploads directory - NO RESTART NEEDED
        registry.addResourceHandler("/external-uploads/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(0); // Disable caching for immediate access

        // Serve classpath uploads (for backward compatibility)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/")
                .setCachePeriod(0);

        // Serve other static resources
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

        System.out.println("✅ Configured external uploads directory: " + uploadDir);
    }
}