package com.study.jpaproject.dto;

import com.study.jpaproject.domain.Address;
import com.study.jpaproject.domain.OrderStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@EqualsAndHashCode(of = "orderId") // 식별자
public class OrderQueryDto {
	
	private Long orderId;
	private String name;
	private LocalDateTime orderDate;
	private OrderStatus orderStatus;
	private Address address;
	private List<OrderItemQueryDto> orderItemDto;
	
	public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
		this.orderId = orderId;
		this.name = name;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.address = address;
	}
	
	public void setOrderItemDto(List<OrderItemQueryDto> orderItemDto) {
		this.orderItemDto = orderItemDto;
	}
	
}
