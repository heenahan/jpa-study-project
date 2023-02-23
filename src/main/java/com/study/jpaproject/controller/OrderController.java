package com.study.jpaproject.controller;

import com.study.jpaproject.domain.Member;
import com.study.jpaproject.domain.Order;
import com.study.jpaproject.domain.item.Item;
import com.study.jpaproject.dto.OrderSearch;
import com.study.jpaproject.service.ItemService;
import com.study.jpaproject.service.MemberService;
import com.study.jpaproject.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final MemberService memberServcie;
	private final ItemService itemService;
	
	@GetMapping("/order")
	public String createForm(Model model) {
		List<Member> memberList = memberServcie.findMembers();
		List<Item> itemList = itemService.findItems();
		
		model.addAttribute("members", memberList);
		model.addAttribute("items", itemList);
		
		return "orders/orderForm";
	}
	
	@PostMapping("/order")
	public String create(@RequestParam("memberId") Long memberId,
	                     @RequestParam("itemId") Long itemId,
	                     @RequestParam("count") int count) {
		
		orderService.order(memberId, itemId, count);
		return "redirect:/orders"; // 주문 조회 API로 get요청 보냄
	}
	
	@GetMapping("/orders") // ModelAttribue 어노테이션 : 모델 객체에 담김
	public String orderList(@ModelAttribute("orderSearch")OrderSearch orderSearch, Model model) {
		List<Order> orderList = orderService.searchOrder(orderSearch);
		model.addAttribute("orders", orderList);
		
		return "orders/orderList";
	}

	@PostMapping("/orders/{orderId}/cancel")
	public String cancleOrder(@PathVariable("orderId") Long orderId) {
		orderService.cancleOrder(orderId);
		
		return "redirect:/orders";
	}
	
}
