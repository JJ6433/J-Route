package com.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静的リソースマッピング
 * 画像アップロード経路制御
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/upload/**")
				.addResourceLocations("file:upload/");

		// 静的リソース(static)
		registry.addResourceHandler("/images/**")
				.addResourceLocations("classpath:/static/images/")
				.setCachePeriod(0);
	}
}
