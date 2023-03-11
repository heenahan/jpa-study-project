package com.study.jpaproject.dto;

import com.study.jpaproject.domain.Address;
import com.study.jpaproject.domain.Order;
import com.study.jpaproject.domain.OrderItem;
import com.study.jpaproject.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class OrderDto {
	
	private Long orderId;
	private String name;
	private LocalDateTime orderDate;
	private OrderStatus orderStatus;
	private Address address;
	// 참고로 Dto안에 엔티티를 넣는 것도 안된다. 엔티티와 의존을 모두 끊어야 한다!
	private List<OrderItemDto> orderItemDto;
	
	public OrderDto(Order o) {
		orderId = o.getId();
		name = o.getMember().getName();
		orderDate = o.getOrderDate();
		orderStatus = o.getStatus();
		address = o.getDelivery().getAddress();
		// 컬렉션 내의 엔티티 모두 강제 초기화 해준다.
		orderItemDto = o.getOrderItems()
				.stream()
				.map(OrderItemDto::new)
				.collect(toList());
	}
	
	@Data
	static class OrderItemDto {
		
		private String itemName;
		private int totalPrice;
		private int count;
		
		public OrderItemDto(OrderItem o) {
			itemName = o.getItem().getName();
			totalPrice = o.getTotalPrice();
			count = o.getCount();
		}
	}
	
}
