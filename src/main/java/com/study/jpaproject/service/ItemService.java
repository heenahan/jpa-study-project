package com.study.jpaproject.service;

import com.study.jpaproject.domain.item.Book;
import com.study.jpaproject.domain.item.Item;
import com.study.jpaproject.dto.UpdateItemDto;
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
	
	/**
	 * 병합(merge)보다 변경 감지를 사용하라!
	 * 병합은 모든 데이터를 변경하므로 null값이 들어갈 수 있다.
	 * 반면 변경 감지는 원하는 데이터만 변경할 수 있다.
	 */
	@Transactional
	public void updateItem(Long itemId, UpdateItemDto param) {
		// findItem은 영속 상태이다.
		Item findItem = itemRepository.findOne(itemId);
		// 이렇게 service 계층에서 속성을 변경하지 않고 엔티티 내에서 값을 변경해야 한다. 그래야 어디서 값이 변경되는지 추적할 수 있다.
		// 예를 들어 Item 도메인에 change(String name, int price, int stockQuantity) 메서드를 정의한다.
		findItem.setName(param.getName());
		findItem.setPrice(param.getPrice());
		findItem.setStockQuantity(param.getStockQuantity());
		// commit하면 flush 날림, JPA는 변경 감지(dirty checking)함
	}
	
	public Item findOne(Long itemId) {
		return itemRepository.findOne(itemId);
	}
	
	public List<Item> findItems() {
		return itemRepository.findAll();
	}
	
}
