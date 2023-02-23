package com.study.jpaproject.repository;

import com.study.jpaproject.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
	
	private final EntityManager em;
	
	public void save(Item item) {
		if (item.getId() == null) { // id가 없다면 db에 저장 안됨
			em.persist(item); // 신규 저장
		} else {
			// item(준영속 엔티티)의 id를 이용해 영속 엔티티를 찾고 해당 영속 엔티티의 값을 item의 값으로 변경한다.
			// 그리고 영속 엔티티를 반환한다. 따라서 mergeItem은 영속 엔티티이고 item은 여전히 준영속 엔티티이다.
			Item mergeItem = em.merge(item);
		}
	}
	
	public Item findOne(Long id) {
		return em.find(Item.class, id);
	}
	
	public List<Item> findAll() {
		return em.createQuery("select i from Item i", Item.class)
				       .getResultList();
	}
	
}
