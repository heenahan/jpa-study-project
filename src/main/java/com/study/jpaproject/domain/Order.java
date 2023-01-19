package com.study.jpaproject.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

	@Id @GeneratedValue
	@Column(name = "order_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id") // 연관관계 주인임을 선언 -> fk를 가진 엔티티자 주인이 됨
	private Member member;
	
	@OneToMany(mappedBy = "order")
	private List<OrderItem> orderItems = new ArrayList<>();
	
	@OneToOne
	@JoinColumn(name = "delivery_id") // 시스템 상 주문을 통해 배달을 보니 order에 fk 존재
	private Delivery delivery;

	private LocalDateTime orderDate;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	
}