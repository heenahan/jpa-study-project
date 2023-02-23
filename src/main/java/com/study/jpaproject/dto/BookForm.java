package com.study.jpaproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookForm {
	// 공통 속성
	private Long id;
	
	private String name;
	private int price;
	private int stockQuantity;
	
	private String author;
	private String isbn;
}
