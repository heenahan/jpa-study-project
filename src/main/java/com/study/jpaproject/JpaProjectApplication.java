package com.study.jpaproject;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpaProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaProjectApplication.class, args);
	}
	
	// Hibernate5Module 모듈 : Lazy = null로 처리
	@Bean
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		// 강제 지연 로딩 따라서 양방향 연관관계의 경우 한 쪽은 JsonIgnore로 끊어줘야 한다.
		// 강제 지연 로딩의 문제점 -> 원하지 않음에도 연관된 모든 엔티티 조회, N + 1
		// hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return hibernate5Module;
	}

}
