package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

@SpringBootTest
@Transactional
class MemberEntityTest {

    @PersistenceContext
    EntityManager em;

    @Test
    void testEntity() {
        TeamEntity teamA = new TeamEntity("teamA");
        TeamEntity teamB = new TeamEntity("teamB");
        em.persist(teamA);
        em.persist(teamB);

        MemberEntity member1 = new MemberEntity("member1", 10, teamA);
        MemberEntity member2 = new MemberEntity("member2", 20, teamA);
        MemberEntity member3 = new MemberEntity("member3", 30, teamB);
        MemberEntity member4 = new MemberEntity("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        List<MemberEntity> members = em.createQuery("select m from MemberEntity m", MemberEntity.class)
                .getResultList();

        for (MemberEntity member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team=" + member.getTeam());
        }

    }

}