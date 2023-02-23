package com.study.jpaproject.dto;

import com.study.jpaproject.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

	private String memberName;
	private OrderStatus orderStatus;

}
