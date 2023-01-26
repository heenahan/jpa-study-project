package com.study.jpaproject.domain.item;

import com.study.jpaproject.domain.Category;
import com.study.jpaproject.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 한 테이블에 상속 클래스 모두 넣음
@DiscriminatorColumn(name = "dtype") // 구분을 위한 컬럼
@Getter
public abstract class Item {

	@Id @GeneratedValue
	@Column(name = "item_id")
	private Long id;
	
	private String name;
	private int price;
	private int stockQuantity;
	
	@ManyToMany(mappedBy = "items")
	private List<Category> categories = new ArrayList<>();
	
	// == 비즈니스 로직 == //
	/** 도메인 주도 개발
	 * -> stock entity를 변경해야 한다면 setter을 이용해 바깥에서 계산하는게 아니다.
	 * 데이터를를 가진 곳에 비즈니스 메서드 작성하여 변경한다.
	 */
	
	public void addStock(int quantity) {
		this.stockQuantity += quantity;
	}
	
	public void removeStock(int quantity) {
		int restStock = this.stockQuantity - quantity;
		if (restStock < 0) throw new NotEnoughStockException("재고가 부족합니다.");
		this.stockQuantity = restStock;
	}
}
