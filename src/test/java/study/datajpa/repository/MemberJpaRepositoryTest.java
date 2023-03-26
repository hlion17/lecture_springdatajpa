package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.MemberEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {
        MemberEntity member = new MemberEntity("memberA");
        MemberEntity savedMember = memberJpaRepository.save(member);
        MemberEntity findMember = memberJpaRepository.find(savedMember.getId());
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }

    @Test
    public void basicCRUD() {
        MemberEntity member1 = new MemberEntity("member1");
        MemberEntity member2 = new MemberEntity("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건 조회 검증
        MemberEntity findMember1 = memberJpaRepository.findById(member1.getId()).get();
        MemberEntity findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<MemberEntity> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();

        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void testNamedQueryMethod() {
        MemberEntity member = new MemberEntity("memberA", 20);

        memberJpaRepository.save(member);

        List<MemberEntity> memberA = memberJpaRepository.findByAge(20);

        System.out.println("memberA = " + memberA);

    }

    /**
     * 순수 JPA Paging Test
     */
    @Test
    void paging() {
        // given
        memberJpaRepository.save(new MemberEntity("member1", 10));
        memberJpaRepository.save(new MemberEntity("member2", 10));
        memberJpaRepository.save(new MemberEntity("member3", 10));
        memberJpaRepository.save(new MemberEntity("member4", 10));
        memberJpaRepository.save(new MemberEntity("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        // when
        List<MemberEntity> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(10);

        // then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    /**
     * 순수 JPA Bulk Update Query test
     */
    @Test
    void bulkUpdate() {
        // given
        memberJpaRepository.save(new MemberEntity("member1", 10));
        memberJpaRepository.save(new MemberEntity("member2", 19));
        memberJpaRepository.save(new MemberEntity("member3", 20));
        memberJpaRepository.save(new MemberEntity("member4", 21));
        memberJpaRepository.save(new MemberEntity("member5", 40));

        // when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

}