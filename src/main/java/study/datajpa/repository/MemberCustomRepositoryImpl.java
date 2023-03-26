package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.MemberEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<MemberEntity> selectCustomMember() {
        return em.createQuery("select m from MemberEntity m where m.age >= 19").getResultList();
    }
}
