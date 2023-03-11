package com.study.jpaproject.service.query;

import com.study.jpaproject.api.OrderAPIController;
import com.study.jpaproject.domain.Address;
import com.study.jpaproject.domain.Order;
import com.study.jpaproject.domain.OrderItem;
import com.study.jpaproject.domain.OrderStatus;
import com.study.jpaproject.dto.OrderDto;
import com.study.jpaproject.dto.OrderSearch;
import com.study.jpaproject.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {
	
	private final OrderRepository orderRepository;
	// 화면에 맞춘 메서드
	public List<OrderDto> findAll() {
		List<Order> orders = orderRepository.findAll(new OrderSearch());
		List<OrderDto> orderDtos = orders.stream()
				.map(OrderDto::new)
				.collect(toList());
		
		return orderDtos;
	}
	
}
