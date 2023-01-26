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
			em.merge(item); // update
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
