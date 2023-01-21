package com.study.jpaproject.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
	
	@Id @GeneratedValue
	@Column(name = "member_id")
	private Long id;
	
	// service에서 중복 이름을 검증하긴 하지만 멀티 스레드를 고려하여 유니크 조건 추가
	@Column(unique = true)
	private String name;
	
	@Embedded
	private Address address;
	
	@OneToMany(mappedBy = "member") // order 테이블의 member 컬럼에 매핑
	private List<Order> orders = new ArrayList<>();
	
	protected Member() {} // JPA Entity 기본 생성자 필요
	
	public Member(Long id, String name, Address address, List<Order> orders) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.orders = orders;
	}
}
