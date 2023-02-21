package com.study.jpaproject.controller;

import com.study.jpaproject.domain.item.Book;
import com.study.jpaproject.domain.item.Item;
import com.study.jpaproject.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
	
	private final ItemService itemService;
	
	@GetMapping("/items/new")
	public String createForm(Model model) {
		model.addAttribute("items", new BookForm());
		return "items/createItemForm";
	}
	
	@PostMapping("/items/new")
	public String create(BookForm bookForm) {
		// setter을 모두 날리고 static한 생성자 메소드를 사용해야 한다.
		Book book = new Book();
		book.setId(bookForm.getId());
		book.setName(bookForm.getName());
		book.setPrice(bookForm.getPrice());
		book.setStockQuantity(bookForm.getStockQuantity());
		book.setAuthor(bookForm.getAuthor());
		book.setIsbn(bookForm.getIsbn());
		
		itemService.saveItem(book);
		return "redirect:/items"; // /items로 get 요청을 보낸다
	}
	
	@GetMapping("/items")
	public String list(Model model) {
		List<Item> items = itemService.findItems();
		model.addAttribute("items", items);
		
		return "items/itemList";
	}
	
}
