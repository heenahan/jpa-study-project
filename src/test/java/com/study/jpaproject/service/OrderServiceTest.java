package com.study.jpaproject.service;

import com.study.jpaproject.domain.Address;
import com.study.jpaproject.domain.Member;
import com.study.jpaproject.domain.Order;
import com.study.jpaproject.domain.OrderStatus;
import com.study.jpaproject.domain.item.Book;
import com.study.jpaproject.domain.item.Item;
import com.study.jpaproject.exception.NotEnoughStockException;
import com.study.jpaproject.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
	
	/**
	 * 아래와 같은 테스트는 디비와 스프링을 연결한 통합 테스트이다.
	 * 좋은 단위 테스트는 디비 없이 스프링도 없이 순수하게 그 메소드를 테스트하는 것이다.
	 * 따라서 비즈니스 로직을 가진 Order 엔티티에 대한 단위 테스트를 작성할 필요가 있다.
	 */
	
	@Autowired EntityManager em;
	@Autowired OrderService orderService;
	@Autowired OrderRepository orderRepository;
	
	@Test
	public void 상품주문() throws Exception {
		// given
		Member member = createMember();
		
		Item book = createItem("스프링 부트와 JPA", 10000, 10);
		
		int orderCount = 2;
		// when
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
		
		// then
		Order order = orderRepository.findOne(orderId);
		assertEquals(OrderStatus.ORDER, order.getStatus(), "주문 상태는 ORDER이어야 한다.");
		assertEquals(1, order.getOrderItems().size(), "주문한 아이템은 한 개 이다.");
		assertEquals(20000, order.getTotalPrice(), "총 가격은 주문 수량 * 가격이다.");
		assertEquals(8, book.getStockQuantity(), "아이템의 재고는 총 재고 - 주문 수량이다.");
	}
	
	@Test(expected = NotEnoughStockException.class)
	public void 재고수량_초과_예외() {
		// given
		Member member = createMember();
		Item book = createItem("스프링 부트와 JPA", 10000, 10);
		
		int orderCount = 12;
		
		// when
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
	
		// then
		fail("재고수량 예외가 발생해야 한다.");
	}
	
	@Test
	public void 주문취소() throws Exception {
		// given
		Member member = createMember();
		Item item = createItem("스프링 부트와 JPA", 10000, 10);
		
		int orderCount = 2;
		
		Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
		// when <- 실제 테스트하고 싶은 것
		orderService.cancleOrder(orderId);

		// then
		Order getOrder = orderRepository.findOne(orderId);
	
		assertEquals(OrderStatus.CANCLE, getOrder.getStatus(), "주문 상태는 Cancle이어야 한다.");
		assertEquals(10, item.getStockQuantity(), "재고 수량은 주문 전 수량과 동일해야 한다.");
	}
	
	private Item createItem(String name, int price, int quantity) {
		Item book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(quantity);
		em.persist(book);
		return book;
	}
	
	private Member createMember() {
		Member member = new Member();
		member.setName("회원1");
		member.setAddress(new Address("서울", "강남구", "245123"));
		em.persist(member);
		return member;
	}
	
}