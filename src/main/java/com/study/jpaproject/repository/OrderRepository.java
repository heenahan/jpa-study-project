package com.study.jpaproject.repository;

import com.study.jpaproject.domain.Order;
import com.study.jpaproject.dto.OrderSearch;
import com.study.jpaproject.dto.SimpleOrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
// 순수한 엔티티만 조회하는 Repository, 비즈니스 로직 수행을 위한 repository
public class OrderRepository {

	private final EntityManager em;
	
	public void save(Order order) {
		em.persist(order);
	}
	
	public Order findOne(Long id) {
		return em.find(Order.class, id);
	}
	
	public List<Order> findAllWithMemberDelivery() {
		// LAZY로 두고 원하는 엔티티만 fetch join!!
		return em.createQuery(
				"select o from Order o"
				+ " join fetch o.member m"
				+ " join fetch o.delivery d", Order.class)
				.getResultList();
	}
	
	// 주문 검색 -> JPQL 쿼리, JPA Criteria 번거로움
	// 동적 쿼리 QueryDSL을 사용
	public List<Order> findAll(OrderSearch orderSearch) {
		String jpql = "select o From Order o join o.member m";
		boolean isFirstCondition = true; //주문 상태 검색
		if (orderSearch.getOrderStatus() != null) {
			if (isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			} else {
				jpql += " and";
			}
			jpql += " o.status = :status";
		}
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			if (isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			} else {
				jpql += " and";
			}
			jpql += " m.name like :name";
		}
		TypedQuery<Order> query = em.createQuery(jpql, Order.class)
				.setMaxResults(1000); //최대 1000건
		if (orderSearch.getOrderStatus() != null) {
			query = query.setParameter("status", orderSearch.getOrderStatus());
		}
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			query = query.setParameter("name", orderSearch.getMemberName());
		}
		return query.getResultList();
	}
	
	public List<Order> findWithItems() {
		return em.createQuery(
				/**
				 * db는 모든 컬럼의 값이 같아야 동일한 행이라고 판단한다.
				 * 하지만 영속성 컨텍스트는 id가 같다면 동일한 엔티티라고 판단한다.
				 * 따라서 distinct로 엔티티 중복을 걸러준다.
				 */
				"select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
						" join fetch o.orderItems oi" +
						// OrderItem에 item도 가져온다.
                        " join fetch oi.item i", Order.class ).getResultList();
	}
	
	public List<Order> findAllWithMemberDelivery(int offset, int limit) {
		return em.createQuery(
				"select o from Order o" +
						" join fetch o.member m" +
						" join fetch o.delivery d", Order.class)
				       .setFirstResult(offset)
				       .setMaxResults(limit)
				       .getResultList();
	}
}
