package com.study.jpaproject.api;

import com.study.jpaproject.domain.Member;
import com.study.jpaproject.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController // controller + responsebody
@RequiredArgsConstructor
public class MemberAPIController {
	
	private final MemberService memberService;
	
	@GetMapping("/api/v1/members")
	public List<Member> memberV1() {
		return memberService.findMembers();
	}
	
	@GetMapping("/api/v2/members")
	public Result membersV2() {
		List<Member> members = memberService.findMembers();
		List<MemberDto> memberDtos = members.stream()
											.map(i -> new MemberDto(i.getName()))
											.collect(Collectors.toList());
		return new Result(memberDtos);
	}
	
	@Data
	@AllArgsConstructor
	static class Result<T> {
		private T data;
	}
	
	@Data
	@AllArgsConstructor
	static class MemberDto {
		private String name;
	}
	
	// 엔티티 외부 노출은 문제가 많이 발생한다.
	@PostMapping("/api/v1/members")
	public CreateMemberResponse joinV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}
	// 따라서 API SPEC을 위한 별도의 DTO를 생성
	@PostMapping("/api/v2/members")
	public CreateMemberResponse joinV2(@RequestBody @Valid CreateMemberRequest createMemberRequest) {
		Member member = new Member();
		member.setName(createMemberRequest.getName());
		Long id = memberService.join(member);
		
		return new CreateMemberResponse(id);
	}
	@PutMapping("/api/v2/members/{memberId}")
	public UpdateMemberResponse updateV2(@PathVariable("memberId") Long memberId,
	                                     @RequestBody @Valid UpdateMemberRequest updateMemberRequest) {
		// Command와 Query 분리 CQS -> 읽기와 쓰기 분리 (유지보수성 높아짐)
		memberService.update(memberId, updateMemberRequest.getName());
		Member member = memberService.findOne(memberId);
		return new UpdateMemberResponse(member.getId(), member.getName());
	}
	
	@Data
	static class UpdateMemberRequest {
		@NotEmpty
		private String name;
	}
	
	@Data
	@AllArgsConstructor // 엔티티에서는 사용하지 않는 것이 좋다.
	static class UpdateMemberResponse {
		private Long id;
		private String name;
	}
	
	@Data
	static class CreateMemberResponse {
		private Long id;
		
		public CreateMemberResponse(Long id) {
			this.id = id;
		}
	}
	
	@Data
	static class CreateMemberRequest {
		@NotEmpty(message = "회원 이름은 입력되어야 합니다.")
		private String name;
	}
}
