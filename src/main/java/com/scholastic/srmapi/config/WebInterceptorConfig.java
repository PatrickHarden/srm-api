package com.scholastic.srmapi.config;

import com.scholastic.srmapi.interceptor.AssessmentControllerInterceptor;
import com.scholastic.srmapi.util.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebInterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new AssessmentControllerInterceptor()).addPathPatterns(Constants.ASSESSMENT_CONTROLLER_PATH_PATTERN);
    }
}
