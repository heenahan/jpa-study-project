package com.study.jpaproject.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
public class Order {

	@Id @GeneratedValue
	@Column(name = "order_id")
	private Long id;

	@ManyToOne(fetch = LAZY, cascade = CascadeType.ALL) // XToOne의 경우 FetchType이 EAGER이 디폴트 -> N + 1 문제 발생
	@JoinColumn(name = "member_id") // 연관관계 주인임을 선언 -> fk를 가진 엔티티자 주인이 됨
	private Member member;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>(); // 컬렉션 필드에서 초기화 -> 1. null 예외 안전 2. 내장 컬렉션 변경 X
	
	@OneToOne(fetch = LAZY, cascade = CascadeType.ALL) // 두 인스턴스 모두 persist할 필요 없음
	@JoinColumn(name = "delivery_id") // 시스템 상 주문을 통해 배달을 보니 order에 fk 존재
	private Delivery delivery;

	private LocalDateTime orderDate;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	
	// 연관 관계 메소드 -> 양방향 연관관계일 때 시스템 상 주도권이 있는 Entity에 정의 //
	public void setMember(Member member) {
		this.member = member;
		member.getOrders().add(this);
	}
	
	public void setOrderItems(OrderItem orderItem) {
		orderItems.add(orderItem);
		orderItem.setOrder(this);
	}
	
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
		delivery.setOrder(this);
	}
	
}