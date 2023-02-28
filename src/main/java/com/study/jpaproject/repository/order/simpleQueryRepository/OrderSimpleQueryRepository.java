package com.study.jpaproject.repository.order.simpleQueryRepository;

import com.study.jpaproject.dto.SimpleOrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
	
	private final EntityManager em;
	
	// API 스펙에 맞춰짐 -> 결국은 계층이 깨짐거임, Repository가 Persentation을 의존하는 거임
	// 그래서 따로 Repository 클래스를 만듦!
	public List<SimpleOrderQueryDto> findOrderDtos() {
		return em.createQuery(
				// 그냥 엔티티(o)를 넘기면 안됨 식별자(Long)만 넘어감
				// address는 value 타입이므로 그냥 넘겨도 됨
				"select new com.study.jpaproject.dto.SimpleOrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) "
						+ "from Order o"
						+ " join fetch o.member m"
						+ " join fetch  o.delivery d", SimpleOrderQueryDto.class
		).getResultList();
	}
	
}
