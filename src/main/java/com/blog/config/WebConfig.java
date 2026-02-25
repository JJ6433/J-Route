package com.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 정적 리소스 매핑
 * 업로드 이미지 경로 /upload/** → upload/ 디렉터리
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/upload/**")
				.addResourceLocations("file:upload/");

		// 정적 리소스 (static)
		registry.addResourceHandler("/images/**")
				.addResourceLocations("classpath:/static/images/")
				.setCachePeriod(0);
	}
}
