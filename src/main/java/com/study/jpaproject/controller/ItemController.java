package com.study.jpaproject.controller;

import com.study.jpaproject.domain.item.Book;
import com.study.jpaproject.domain.item.Item;
import com.study.jpaproject.dto.BookForm;
import com.study.jpaproject.dto.UpdateItemDto;
import com.study.jpaproject.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	// 이때 itemId가 조작될 수 있다. 해당 item에 대한 수정 권한이 없는 유저가 수정하지 못하도록 해야한다.
	// 따라서 유저가 해당 item에 대한 권한이 있는지 확인해주어야 한다.
	@GetMapping("/items/{itemId}/edit")
	public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
		// 타입 캐스트는 좋지 않음
		Book item = (Book) itemService.findOne(itemId);
		
		BookForm form = new BookForm();
		form.setId(item.getId());
		form.setName(item.getName());
		form.setPrice(item.getPrice());
		form.setStockQuantity(item.getStockQuantity());
		
		form.setAuthor(item.getAuthor());
		form.setIsbn(item.getIsbn());
		
		return "items/updateItemForm";
	}
	
	@PostMapping("item/{itemId}/edit")
	public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("item") BookForm bookForm) {
//		Book book = new Book();
		// id가 존재하므로 db에 한 번 저장된 값이다.
		// 따라서 준영속 상태이다. 준영속이란 영속성 컨텍스트(JPA)가 더이상 관리하지 않는 엔티티이다.
//		book.setId(bookForm.getId());
//		book.setName(bookForm.getName());
//		book.setPrice(bookForm.getPrice());
//		book.setStockQuantity(bookForm.getStockQuantity());
//		book.setAuthor(bookForm.getAuthor());
//		book.setIsbn(bookForm.getIsbn());
		
		// controller에서 엔티티를 생성하지 않고 Dto를 생성해서 넘겨줘야 한다.
		UpdateItemDto updateItemDto = new UpdateItemDto();
		updateItemDto.setName(bookForm.getName());
		updateItemDto.setPrice(bookForm.getPrice());
		updateItemDto.setStockQuantity(bookForm.getStockQuantity());
		itemService.updateItem(itemId, updateItemDto);
		return "redirect:/items";
	}
	
}
