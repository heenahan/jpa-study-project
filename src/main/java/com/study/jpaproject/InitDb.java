package com.study.jpaproject;

import com.study.jpaproject.domain.*;
import com.study.jpaproject.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 주문 2건
 * memberA
 * - JPA1 BOOK
 * - JPA2 BOOK
 *
 * memberB
 * - Spring1 Book
 * - Spring2 Book
 */
@Component
@RequiredArgsConstructor
public class InitDb {
	
	private final InitService initService;
	
	@PostConstruct // 스프링 빈이 모두 올라온 다음 애플리케이션 로딩 시점에 호출
	public void init() {
		initService.doInit1();
		initService.doInit2();
		initService.doInit3();
	}
	
	@Component
	@RequiredArgsConstructor
	@Transactional
	static class InitService {
		
		private final EntityManager em;
		
		public void doInit1() {
			Member member = getMember("memberA", "서울", "강남", "11111");
			em.persist(member);
			
			Book book1 = createBook("JPA1 Book", 10000, 99);
			Book book2 = createBook("JPA2 Book", 15000, 99);
			em.persist(book1);
			em.persist(book2);
	
			Delivery delivery = new Delivery();
			delivery.setAddress(member.getAddress());
			// Legacy 전략 확인을 위해 데이터 많이 넣음
			for (int i = 0; i < 15; i++) {
				OrderItem orderItem = OrderItem.createOrderItem(book1, book1.getPrice(), 1);
				
				Order order = Order.createOrder(member, delivery, orderItem);
				em.persist(order);
			}
		}
		
		public void doInit2() {
			Member member = getMember("memberB", "수원", "광교", "22222");
			em.persist(member);
			
			Book book1 = createBook("Spring1 Book", 20000, 99);
			Book book2 = createBook("Spring2 Book", 10000, 99);
			em.persist(book1);
			em.persist(book2);
			
			OrderItem orderItem1 = OrderItem.createOrderItem(book1, book1.getPrice(), 1);
			OrderItem orderItem2 = OrderItem.createOrderItem(book2, book2.getPrice(), 3);
			
			Delivery delivery = new Delivery();
			delivery.setAddress(member.getAddress());
			Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
			em.persist(order);
		}
		
		public void doInit3() {
			Member member = new Member();
			member.setName("memberC");
			member.setAddress(new Address("충북", "천안", "234567"));
			em.persist(member);
		}
		
		private Member getMember(String name, String city, String street, String zipCode) {
			Member member = new Member();
			member.setName(name);
			member.setAddress(new Address(city, street, zipCode));
			return member;
		}
		
		private Book createBook(String name, int price, int stockquantity) {
			Book book1 = new Book();
			book1.setName(name);
			book1.setPrice(price);
			book1.setStockQuantity(stockquantity);
			return book1;
		}
		
	}
}
