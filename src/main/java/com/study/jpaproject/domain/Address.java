package com.study.jpaproject.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

	private String city;
	private String street;
	private String zipcode;
	
	// JPA 라이브러리가 객체를 생성할 때 리플렉션 기술을 사용
	// 엔티티나 임베디드 타입은 기본 생성자를 생성
	protected Address() {
	}
	
	public Address(String city, String street, String zipcode) {
		this.city = city;
		this.street = street;
		this.zipcode = zipcode;
	}
	
}
