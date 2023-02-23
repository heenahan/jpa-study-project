package com.study.jpaproject.controller;

import com.study.jpaproject.domain.Address;
import com.study.jpaproject.domain.Member;
import com.study.jpaproject.dto.MemberForm;
import com.study.jpaproject.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	
	@GetMapping("/members/new")
	public String createForm(Model model) {
		// 화면에서 MemberForm 객체에 접근할 수 있다.
		model.addAttribute("memberForm", new MemberForm());
		return "members/createMemberForm";
	}
	
	@PostMapping("/members/new")
	public String create(@Valid MemberForm form, BindingResult bind) {
		// valid 에러 발생시 members/createMemberForm으로 메시지를 보낸다.
		if (bind.hasErrors()) {
			return "members/createMemberForm";
		}
		
		Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
		
		Member member = new Member();
		member.setName(form.getName());
		member.setAddress(address);
		
		memberService.join(member);
		
		return "redirect:/"; // 첫번째 페이지로 돌아감
	}
	/**
	 * 만약 API로 개발할 때 절대 엔티티를 외부로 전달해선 안됨!
	 * DTO로 변환하여 필요한 데이터만 전달
	 * 엔티티 그대로 넘겼을 때, 엔티티가 변경되면 불필요한 정보가 노출되고 API 스펙이 변경되므로 위험함
	 */
	@GetMapping("/members")
	public String list(Model model) {
		List<Member> members = memberService.findMembers();
		model.addAttribute("members", members);
		
		return "members/memberList";
	}
	
}
