package com.study.jpaproject.repository;

import com.study.jpaproject.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// <엔티티, 아이디>
public interface MemberRepository extends JpaRepository<Member, Long> {
	
	// Optional 클래스 반환
	
	List<Member> findByName(String name);
	
}
