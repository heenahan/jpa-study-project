package com.study.jpaproject.service;

import com.study.jpaproject.domain.Member;
import com.study.jpaproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// public 메서드는 transactional 적용
@Transactional(readOnly = true) // 데이터 변경이 없는 읽기에는 readOnly = true 성능 약간 향상
@RequiredArgsConstructor // final 필드만 생성자 주입
public class MemberService {
	
	private final MemberRepository memberRepository;
	
	// 필드 주입보다 생성자 주입 -> 테스트시 MemberService를 생성할 때 어떠한 Mock을 주입해야 하는지 바로 알 수 있다.
	// setter를 이용해 주입한다면 런타임 도중 변경될 위험이 있다.
	
	/**
	 * 회원가입
	 * @param member
	 * @return Long
	 */
	@Transactional
	public Long join(Member member) {
		validateDuplicateMember(member);
		memberRepository.save(member);
		return member.getId();
	}
	
	private void validateDuplicateMember(Member member) {
		List<Member> findMembers = memberRepository.findByName(member.getName());
		if (!findMembers.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 아이디 입니다.");
		}
	}
	
	// 회원 전체 조회
	public List<Member> findMembers() {
		return memberRepository.findAll();
	}
	
	public Member findOne(Long memberId) {
		return memberRepository.findOne(memberId);
	}
	
}
