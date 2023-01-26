package com.study.jpaproject.service;

import com.study.jpaproject.domain.Member;
import com.study.jpaproject.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest // 스프링부트를 띄우고 테스트 -> 스프링 컨테이너 안에서 테스트
@Transactional // 테스트 실행할 때마다 트랜잭션 시작, 테스트 끝나면 강제 롤백 -> 반복적 테스트 가능
public class MemberServiceTest {
	
	@Autowired MemberService memberService;
	@Autowired MemberRepository memberRepository;
	@Autowired EntityManager em;
	
	@Test
	// @Rollback(value = false) // 1. Committed transaction for test : rollback을 안하고 commit하여 flush(데이터 변경)함
	public void 회원가입() throws Exception {
		// Given
		Member member = new Member();
		member.setName("kim");
		
		// When
		Long saveId = memberService.join(member);
		
		// Then
		em.flush(); // 2. Rolled back transaction for test : insert문을 날렸지만 rollback 됨
		assertEquals(member, memberRepository.findOne(saveId));
	}
	
	@Test(expected = IllegalStateException.class)
	public void 중복_회원_예외() throws Exception {
		// Given
		Member member1 = new Member();
		member1.setName("kim");
		
		Member member2 = new Member();
		member2.setName("kim");
		
		// when
		memberService.join(member1);
		memberService.join(member2); // 예외 발생
		
		// Then
		fail("예외가 발생해야 한다.");
	}
	
}