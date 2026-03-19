package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * J-Route アプリケーションメインクラス
 * 日本旅行推薦及びAIプランナーサービス起点
 * MyBatisのみ使用のため Spring Data JDBC 自動構成除外 (Boot 4.x)
 */
@SpringBootApplication(excludeName = {
		"org.springframework.boot.data.jdbc.autoconfigure.DataJdbcRepositoriesAutoConfiguration"
})
public class JRouteApplication {

	public static void main(String[] args) {
		SpringApplication.run(JRouteApplication.class, args);
	}
}
