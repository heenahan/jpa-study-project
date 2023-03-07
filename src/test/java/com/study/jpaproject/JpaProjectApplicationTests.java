package com.study.jpaproject;

import com.study.jpaproject.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaProjectApplicationTests {

	@Autowired private EntityManager em;
	
	@Test
	public void contextLoads() {
		List<Order> serveral = em.createQuery(
				"select o from Order o" +
						" join fetch o.orderItems oi" +
						" join fetch oi.item i", Order.class)
				.getResultList();
		
		List<Order> orders1 = em.createQuery(
				"select o from Order o" +
						" join fetch o.orderItems oi", Order.class)
				.getResultList();
		// 데이터 무결성 깨짐
		List<Order> orders2 = em.createQuery(
				"select o from Order o" +
						" join fetch o.orderItems oi" +
				" where oi.orderPrice > 10000", Order.class)
				.getResultList();
		
		List<Order> orders3 = em.createQuery(
				"select o from Order o" +
						" join fetch o.member m", Order.class)
                .getResultList();
		// 데이터 무결성 지킴
		String memberName = "memberA";
		List<Order> orders4 = em.createQuery(
				"select o from Order o" +
						" join fetch o.member m" +
				" where m.name =: memberName", Order.class)
				.setParameter("memberName", memberName)
				.getResultList();
	}

}
