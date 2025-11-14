package ru.job4j.cars.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.path:/uploads/images}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        String uploadPathStr = uploadDir.toString();

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPathStr + "/")
//                .setCachePeriod(3600)
                .resourceChain(true);
    }
}