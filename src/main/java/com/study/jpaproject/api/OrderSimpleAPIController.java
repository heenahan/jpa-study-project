package com.study.jpaproject.api;

import com.study.jpaproject.domain.Address;
import com.study.jpaproject.domain.Order;
import com.study.jpaproject.domain.OrderStatus;
import com.study.jpaproject.dto.OrderSearch;
import com.study.jpaproject.dto.SimpleOrderQueryDto;
import com.study.jpaproject.repository.OrderRepository;
import com.study.jpaproject.repository.order.simpleQueryRepository.OrderSimpleQueryRepository;
import com.study.jpaproject.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * xToOne (manyToOne, OneToMany) 관계 최적화
 * Order
 * Order <-> Member
 * Order <-> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleAPIController {
	
	private final OrderService orderService;
	private final OrderRepository orderRepository;
	private final OrderSimpleQueryRepository orderSimpleQueryRepository;
	
	/**
	 * 발생하는 문제점
	 * 1. 무한 루프에 빠진다.
	 *  -> 양방향 관계에서 발생
	 *     객체를 JSON로 만드는 jackson 라이브러리가 Order객체에서 Member를 Member객체에서 Order를 계속 가져오면서 발생함.
	 *  sol. 한 쪽 관계를 JsonIgnore로 끊어줌
	 *  2. ByteBuddyInterceptor
	 *   -> 지연 로딩으로 Order만 조회하고 Member와 Delivery는 DB에서 조회 안함
	 *      JPA가 Order의 Member에 Member 객체를 상속받은 프록시 객체(ByteBuddyInterceptor)를 넣어준다.
	 */
	@GetMapping("/api/v1/simple-orders")
	public List<Order> ordersV1() {
		// 모든 주문 가져옴
		List<Order> orders = orderService.searchOrder(new OrderSearch());
		for (Order order : orders) {
			// 우리가 원하는 데이터만 Lazy 강제 초기화
			order.getMember().getName();
			order.getDelivery().getAddress();
		}
		// 엔티티만 넘길 때 json으로 변환하는 과정에서 Hibernate5Module 실행
		// 따라서 연관된 엔티티 Member, OrderItem, Delivery에 대한 쿼리 모두 생김
		return orders;
	}
	
	@GetMapping("/api/v2/simple-orders")
	public List<SimpleOrderDto> ordersV2() {
		List<Order> orders = orderService.searchOrder(new OrderSearch());
		/**
		 * 1 + N 문제 발생 order를 조회하는 쿼리에 대한 응답 n개만큼 더 쿼리가 발생
		 * 1 order -> Member 2 + Delivery 2 최악의 경우 5개의 쿼리가 나감감
		 * 근데 왜 최악의 경우인가?
		 * 예를 들어 A, B 주문은 동일한 멤버가 주문했다.
		 * A 주문에서 멤버를 조회하면 이제 이 멤버는 영속 엔티티이다.
		 * 따라서 B 주문에서 멤버를 조회할 때  DB에서 조회하는게 아닌 영속성 컨텍스트 1차 캐시에서 조회하면 된다.
		 */
		List<SimpleOrderDto> orderDto = orders.stream().map(SimpleOrderDto::new).collect(toList());
		return orderDto;
	}
	
	/**
	 * v3와 v4 모두 fetch join을 사용하여 성능 튜닝을 하지만 차이점이 존재한다.
	 * 1. 네트워크 (최적화)
	 * v3의 경우 원하지 않는 컬럼(데이터)을 모두 select한다.
	 * 반면 v4는 원하는 컬럼만 select하여 네트워크를 더 적게 쓴다.
	 * 하지만 성능 차이가 미비하다. 보통 join과 where에서 성능 이슈 발생
	 * 트래픽이 정말 많이 들어오는 API라면 고민할 필요는 있음.
	 * 2. 재사용성
	 * v3는 엔티티를 조회하고 Dto로 변환 v4는 처음부터 Dto를 조회한다.
	 * v3의 경우 서비스 레이어에서 다른 Dto로 변환할 수 있지만 v4는 아예 불가능하다.
	 * 따라서 v3가 재사용성이 높다.
	 */
	@GetMapping("/api/v3/simple-orders")
	public List<SimpleOrderDto> ordersV3() {
		List<Order> orders = orderRepository.findAllWithMemberDelivery();
		// 쿼리 한번만 나감
		return orders.stream().map(SimpleOrderDto::new).collect(toList());
	}
	
	@GetMapping("/api/v4/simple-ordsers")
	public List<SimpleOrderQueryDto> ordersV4() {
		return orderSimpleQueryRepository.findOrderDtos();
	}
	
	@Data
	static class SimpleOrderDto {
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		// Dto는 엔티티를 의존해도 되지만 엔티티는 Dto를 의존해선 안된다.
		public SimpleOrderDto(Order o) {
			orderId = o.getId();
			name = o.getMember().getName();
			orderDate = o.getOrderDate();
			orderStatus = o.getStatus();
			address = o.getDelivery().getAddress();
		}
	}
	
}
