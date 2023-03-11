package com.study.jpaproject.repository.order.queryRepository;

import com.study.jpaproject.dto.OrderFlatDto;
import com.study.jpaproject.dto.OrderItemQueryDto;
import com.study.jpaproject.dto.OrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Repository
@RequiredArgsConstructor
// 화면에 fit한
public class OrderQueryRepository {
	
	private final EntityManager em;
	
	/**
	 * 1. XToOne join으로 함께 조회
	 * 2. 리스트의 모든 Order에 대한 OrderItem 조회
	 * N + 1문제 발생
	 */
	public List<OrderQueryDto> findOrderQueryDtos() {
		List<OrderQueryDto> orderQueryDtos = findOrders();
		orderQueryDtos.stream().forEach(order  -> {
			List<OrderItemQueryDto> orderItemQueryDtos = findOrderItems(order.getOrderId());
			order.setOrderItemDto(orderItemQueryDtos);
		});
		return orderQueryDtos;
	}
	
	public List<OrderQueryDto> findOrderQueryDtosOptimization() {
		List<OrderQueryDto> orderQueryDtos = findOrders();
		// orderId 추출
		List<Long> orderIds = orderQueryDtos.stream().map(OrderQueryDto::getOrderId).collect(toList());
		// in 쿼리를 이용해 단 한 번의 쿼리로 Order과 연관된 OrderItem 가져옴
		List<OrderItemQueryDto> orderItemQueryDtos = em.createQuery(
						"select new com.study.jpaproject.dto.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
								" from OrderItem oi" +
								" join oi.item i" +
								" where oi.order.id in :orderIds", OrderItemQueryDto.class)
				.setParameter("orderIds", orderIds)
				.getResultList();
		// 그리고 메모리 내에서 orderId를 기준으로 분류
		Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItemQueryDtos.stream().collect(groupingBy(OrderItemQueryDto::getOrderId));
		// map을 이용해 O(1)으로 Order의 OrderItem set
		orderQueryDtos.forEach(o -> o.setOrderItemDto(orderItemMap.get(o.getOrderId())));
		
		return orderQueryDtos;
	}
	
	/**
	 * 왜 OrderAPIController에 있는 OrderDto를 재사용하지 않는가?
	 * 순환 참조 문제가 발생하기 때문이다.
	 * Repository가 Controller의 inner class를 의존한다는 것은 순환참조이다.
	 *
	 * fetch join과 달리 선택한 컬럼만 가져오기 때문에 데이터 양이 줄어든다.
	 */
	private List<OrderQueryDto> findOrders() {
		return em.createQuery(
				// JPQL에서 컬렉션을 바로 넣을 수 없다. 플랫 데이터만 가능
				"select new com.study.jpaproject.dto.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
						" from Order o" +
						" join o.member m" +
						" join o.delivery d", OrderQueryDto.class)
				.getResultList();
	}
	
	private List<OrderItemQueryDto> findOrderItems(Long orderId) {
		return em.createQuery(
				"select new com.study.jpaproject.dto.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
						" from OrderItem oi" +
						" join oi.item i" +
						" where oi.order.id =: orderId", OrderItemQueryDto.class)
		        .setParameter("orderId", orderId)
				.getResultList();
	}
	
	public List<OrderFlatDto> findORderFlatDtos() {
		return em.createQuery(
				"select new com.study.jpaproject.dto.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
						" from Order o" +
						" join o.member m" +
						" join o.delivery d" +
						" join o.orderItems oi" +
						" join oi.item i", OrderFlatDto.class)
				.getResultList();
	}
	
}
