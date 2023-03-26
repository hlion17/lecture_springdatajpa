package study.datajpa.repository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.MemberEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {
    @PersistenceContext
    private EntityManager em;
    public MemberEntity save(MemberEntity member) {
        em.persist(member);
        return member;
    }
    public void delete(MemberEntity member) {
        em.remove(member);
    }
    public List<MemberEntity> findAll() {
        return em.createQuery("select m from MemberEntity m", MemberEntity.class)
                .getResultList();
    }
    public Optional<MemberEntity> findById(Long id) {
        MemberEntity member = em.find(MemberEntity.class, id);
        return Optional.ofNullable(member);
    }
    public long count() {
        return em.createQuery("select count(m) from MemberEntity m", Long.class)
                .getSingleResult();
    }
    public MemberEntity find(Long id) {
        return em.find(MemberEntity.class, id);
    }

    public List<MemberEntity> findByAge(int age) {
        return em.createNamedQuery("Member.findByAge", MemberEntity.class)
                .setParameter("age", age)
                .getResultList();
    }

    /**
     * 순수 JPA를 사용한 페이징 메소드
     */
    public List<MemberEntity> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from MemberEntity m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * 순수 JPA를 사용한 페이징 메소드
     *  - totalCount값을 얻기위한 별도 메소드 필요
     */
    public long totalCount(int age) {
        return em.createQuery("select count(*) from MemberEntity m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    /**
     * Bulk update Query
     */
    public int bulkAgePlus(int age) {
        int resultCount = em.createQuery("update MemberEntity m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
        return resultCount;
    }
}