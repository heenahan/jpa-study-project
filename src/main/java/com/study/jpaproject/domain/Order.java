package com.study.jpaproject.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
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
	
	public void addOrderItems(OrderItem orderItem) {
		orderItems.add(orderItem);
		orderItem.setOrder(this);
	}
	
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
		delivery.setOrder(this);
	}
	
	// 생성 메서드 -> Order처럼 복잡한 생성의 경우 따로 생성자 메서드 정의 //
	
	public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
		Order order = new Order();
		order.setMember(member);
		order.setDelivery(delivery);
		for (OrderItem orderItem : orderItems) {
			order.addOrderItems(orderItem);
		}
		order.setStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());
		return order;
	}
	
	// 비즈니스 로직
	
	public void cancle() {
		// 배달 완료된 상태이면 주문 취소 불가능
		if (delivery.getStatus() == DeliveryStatus.COMP) {
			throw new IllegalStateException("이미 배달이 완료되어 주문을 취소할 수 없습니다.");
		}
		this.status = OrderStatus.CANCLE;
		for (OrderItem orderItem : orderItems) {
			orderItem.cancle(); // 재고 복구
		}
	}
	
	// 조회 로직
	/**
	 * 전체 주문 가격 조회
	 */
	public int getTotalPrice() {
		int totalPrice = 0;
		for (OrderItem orderItem : orderItems) {
			totalPrice += orderItem.getTotalPrice();
		}
		return totalPrice;
	}
}