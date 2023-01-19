package com.study.jpaproject.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {
	
	@Id @GeneratedValue
	@Column(name = "delivery_id")
	private Long id;
	
	@OneToOne(mappedBy = "delivery")
	private Order order;
	
	@Embedded
	private Address address;
	
	// EnumType.ORDINAL을 사용할 경우 숫자로 들어감 중간에 상태가 추가되면 뒤로 밀리는 현상 발생
	@Enumerated(EnumType.STRING)
	private DeliveryStatus status;
}
