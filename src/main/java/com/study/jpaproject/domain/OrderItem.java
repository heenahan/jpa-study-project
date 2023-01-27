package com.study.jpaproject.domain;

import com.study.jpaproject.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class OrderItem {

	@Id @GeneratedValue
	@Column(name = "order_item_id")
	private Long id;
	
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "item_id")
	private Item item;
	
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "order_id")
	private Order order;
	
	private int orderPrice; // 주문 가격
	private int count; // 주문 수량
	
	// 생성 메소드 -> 전역 메소드
	// 가격은 할인 등으로 바뀔 수 있다.
	public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
		OrderItem orderItem = new OrderItem();
		orderItem.setItem(item);
		orderItem.setOrderPrice(orderPrice);
		orderItem.setCount(count);
		
		item.removeStock(count); // 재고에서 주문 수량만큼 빠짐
		return orderItem;
	}
	
	// 비즈니스 로직
	
	/**
	 * 취소로 인한 재고 증가
	 */
	public void cancle() {
		getItem().addStock(count);
	}
	
	/**
	 * 주문한 아이템의 전체 가격
	 */
	public int getTotalPrice() {
		return orderPrice * count;
	}
}
