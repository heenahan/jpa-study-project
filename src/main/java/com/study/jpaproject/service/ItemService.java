package com.study.jpaproject.service;

import com.study.jpaproject.domain.item.Item;
import com.study.jpaproject.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
	
	private final ItemRepository itemRepository;
	
	/**
	 * ItemRepository를 위임만 하는 클래스
	 * 이 경우 클래스를 생성하지 않고 Controller에서 바로 Repository로 접근해도 괜찮다.
 	 */
	
	@Transactional
	public void saveItem(Item item) {
		itemRepository.save(item);
	}
	
	public Item findOne(Long itemId) {
		return itemRepository.findOne(itemId);
	}
	
	public List<Item> findItems() {
		return itemRepository.findAll();
	}
	
}
