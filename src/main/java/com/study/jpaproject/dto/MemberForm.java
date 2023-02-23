package com.study.jpaproject.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * 엔티티를 직접 폼 화면에서 사용해도 되지만 엔티티가 화면에 종속되면 엔티티가 화면을 처리하기 위한 기능이 증가한다.
 * 결국 엔티티는 지저분해지고 유지보수하기 어렵다.
 * 따라서 엔티티는 핵심 비즈니스 로직만 가지고 있어야 한다.
 */
@Getter @Setter
public class MemberForm {
	// 스프링에서 유효성 검사를 해준다.
	@NotEmpty(message = "회원 이름은 필수 입니다.")
	private String name;
	
	private String city;
	private String street;
	private String zipcode;
}
