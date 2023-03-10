package com.study.jpaproject.repository;

import com.study.jpaproject.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepositoryOld {

	// 스프링으로부터 JPA의 EntityManager를 주입받는다.
	@PersistenceContext
	private EntityManager em;
	
	public void save(Member member) {
		em.persist(member);
	}
	
	public Member findOne(Long id) {
		// 반환타입 Member.class, 단건 조회로 pk를 넣음
		return em.find(Member.class, id);
	}
	
	public List<Member> findAll() {
		// JPQL from 대상이 테이블이 아닌 엔티티 객체로 쿼리를 작성
		return em.createQuery("select m from Member m", Member.class)
				.getResultList();
	}
	
	public List<Member> findByName(String name) {
		// :변수
		return em.createQuery("select m from Member m  where m.name = :name", Member.class)
				       .setParameter("name", name)
				.getResultList();
	}

}
