package com.study.jpaproject.domain;

import com.study.jpaproject.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Category {
	
	@Id @GeneratedValue
	@Column(name = "category_id")
	private Long id;
	
	private String name;
	
	@ManyToMany
	@JoinTable(name = "category_item", // 중간 테이블 category_item 테이블 추가, 컬럼 추가가 어려우므로 실무에서 사용X
		joinColumns = @JoinColumn(name = "category_id"),
			inverseJoinColumns = @JoinColumn(name = "item_id")
	)
	private List<Item> items = new ArrayList<>();

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "parent_id")
	private Category parent;
	
	// 계층 관계
	@OneToMany(mappedBy = "parent")
	private List<Category> child = new ArrayList<>();
	
	// 연관 관계 메소드 //
	public void setChild(Category child) {
		this.child.add(child);
		child.setParent(this);
	}
	
}
