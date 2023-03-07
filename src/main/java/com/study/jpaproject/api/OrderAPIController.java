package com.study.jpaproject.api;

import com.study.jpaproject.domain.Address;
import com.study.jpaproject.domain.Order;
import com.study.jpaproject.domain.OrderItem;
import com.study.jpaproject.domain.OrderStatus;
import com.study.jpaproject.dto.OrderFlatDto;
import com.study.jpaproject.dto.OrderItemQueryDto;
import com.study.jpaproject.dto.OrderQueryDto;
import com.study.jpaproject.dto.OrderSearch;
import com.study.jpaproject.repository.OrderRepository;
import com.study.jpaproject.repository.order.queryRepository.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderAPIController {
	
	private final OrderRepository orderRepository;
	private final OrderQueryRepository orderQueryRepository;
	
	@GetMapping("/api/v1/orders")
	public List<Order> ordersV1() {
		List<Order> orders = orderRepository.findAll(new OrderSearch());
		for (Order order : orders) {
			order.getMember().getName();
			order.getDelivery().getAddress();
			// Lazy 강제 초기화
			List<OrderItem> orderItems = order.getOrderItems();
			orderItems.stream().forEach(o -> o.getItem());
		}
		return orders;
	}
	// entity -> dto
	@GetMapping("/api/v2/orders")
	public List<OrderDto> ordersV2() {
		List<Order> orders = orderRepository.findAll(new OrderSearch());
		List<OrderDto> orderDtos = orders.stream()
										.map(OrderDto::new)
										.collect(toList());
		
		return orderDtos;
	}
	
	@GetMapping("/api/v3/orders")
	public List<OrderDto> ordersV3() {
		// inner join으로 인해 order과 연관된 orderItem이 2개라면 조회 결과 행이 2개다.
		// JPA는 DB 조회 결과 수만큼 컬렉션을 만든다. 그 결과 동일한 엔티티가 두 번 조회된다.
		List<Order> orders = orderRepository.findWithItems();
		List<OrderDto> orderDtos = orders.stream()
			                           .map(OrderDto::new)
			                           .collect(toList());
		
		return orderDtos;
	}
	
	/**
	 * 우리는 Order(1)를 기준으로 페이징하고 싶다. 그러나 DB는 OrderItem(many)를 기준으로 데이터가 조회되므로 페이징이 어렵다.
	 * 1. XToOne 관계의 엔티티는 fetch join 후 페이징
	 * 2. batch fetching을 사용하여 컬렉션 조회 최적화 (데이터 중복 X, N + 1 문제 해결)
	 */
	@GetMapping("/api/v3.1/orders")
	public List<OrderDto> ordersV3_paging(
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "limit", defaultValue = "100") int limit
	) {
		List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
		List<OrderDto> orderDtos = orders.stream()
			                           .map(OrderDto::new)
			                           .collect(toList());
		return orderDtos;
	}
	
	/**
	 * 엔티티 조회 vs DTO 조회
	 * 엔티티 조회 시 엔티티의 모든 필드 조회
	 * DTO 조회 시 필요한 필드만 조회
	 * 성능면에서 엔티티 조회보다 DTO 조회가 좋음
	 */
	
	@GetMapping("/api/v4/orders")
	public List<OrderQueryDto> ordersV4() {
		return orderQueryRepository.findOrderQueryDtos();
	}
	
	@GetMapping("/api/v5/orders")
	public List<OrderQueryDto> ordersV5() {
		return orderQueryRepository.findOrderQueryDtosOptimization();
	}
	
	@GetMapping("/api/v6/orders")
	public List<OrderQueryDto> ordersV6() {
		/**
		 * 단 한 번의 쿼리로 Order, Member, Delivery, OrderItem, Item 모두 가져온다.
		 * 이렇게 하면 Many에 맞춰 데이터 증가하여 중복 발생한다.
		 * 전달되는 쿼리는 적지만 메모리에서 처리하는 데이터 양이 엄청 증가한다. 오히려 성능 안좋을 수도 있음.
		 * 그리고 페이징 불가.
		 */
		List<OrderFlatDto> orderFlatDtos = orderQueryRepository.findORderFlatDtos();
		// 메모리에서 중복을 거르고 OrderQueryDto로 rebuild
		return orderFlatDtos.stream()
				.collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
						mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getTotalPrice(), o.getCount()), toList())))
				.entrySet().stream()
				.map(e -> {
					OrderQueryDto orderQueryDto = new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress());
					orderQueryDto.setOrderItemDto(e.getValue());
					return orderQueryDto;
				})
				.collect(toList());
	}
	
	@Data
	static class OrderDto {
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
