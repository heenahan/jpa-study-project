package com.study.jpaproject.service;

import com.study.jpaproject.domain.*;
import com.study.jpaproject.domain.item.Item;
import com.study.jpaproject.repository.ItemRepository;
import com.study.jpaproject.repository.MemberRepository;
import com.study.jpaproject.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;
	private final ItemRepository itemRepository;
	
	/**
	 * 주문
	 */
	public Long order(Long memberId, Long itemId, int count) {
		// 엔티티 조회
		Member member = memberRepository.findOne(memberId);
		Item item = itemRepository.findOne(itemId);
		
		// 배달 생성
		Delivery delivery = new Delivery();
		delivery.setAddress(member.getAddress());
		delivery.setStatus(DeliveryStatus.READY);
		
		// 주문 아이템 생성
		OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
		
		// 주문 생성
		Order order = Order.createOrder(member, delivery, orderItem);
		// Order temp_order = new Order(); 기본 생성자 protect이므로 불가
		
		// 주문 저장
		orderRepository.save(order);
		
		return order.getId();
	}
	
	/**
	 * 주문 취소
	 * @param orderId
	 */
	@Transactional
	public void cancleOrder(Long orderId) {
		// 주문 조회
		Order order = orderRepository.findOne(orderId);
		// 주문 취소 -> !!핵심 비즈니스 로직은 엔티티 내에 있고 service는 위임하는 역할
		order.cancle(); // 엔티티 내에서 데이터 변경이 일어나면 JPA는 변경 감지를 하여 update문 날림
	}
	
}
