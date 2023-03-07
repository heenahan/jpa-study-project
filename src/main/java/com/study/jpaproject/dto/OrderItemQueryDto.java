package com.study.jpaproject.dto;

import lombok.Data;

@Data
public class OrderItemQueryDto {
	
	private Long orderId;
	private String itemName;
	private int totalPrice;
	private int count;
	
	public OrderItemQueryDto(Long orderId, String itemName, int totalPrice, int count) {
		this.orderId = orderId;
		this.itemName = itemName;
		this.totalPrice = totalPrice;
		this.count = count;
	}
	
}
