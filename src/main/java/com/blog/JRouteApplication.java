package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * J-Route 애플리케이션 메인 클래스
 * 일본 여행 추천 및 AI 플래너 서비스 진입점
 * MyBatis만 사용하므로 Spring Data JDBC 자동구성 제외 (Boot 4.x)
 */
@SpringBootApplication(excludeName = {
		"org.springframework.boot.data.jdbc.autoconfigure.DataJdbcRepositoriesAutoConfiguration"
})
public class JRouteApplication {

	public static void main(String[] args) {
		SpringApplication.run(JRouteApplication.class, args);
	}
}
