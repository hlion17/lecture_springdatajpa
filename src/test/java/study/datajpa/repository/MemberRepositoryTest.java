package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.MemberEntity;
import study.datajpa.entity.TeamEntity;
import study.datajpa.repository.projection.MemberProjection;
import study.datajpa.repository.projection.NestedClosedProjections;
import study.datajpa.repository.specification.MemberSpec;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        MemberEntity member = new MemberEntity("memberA");
        MemberEntity savedMember = memberRepository.save(member);
        Optional<MemberEntity> foundMemberOpt = memberRepository.findById(savedMember.getId());
        MemberEntity foundMember = foundMemberOpt.get();
        assertThat(foundMember.getId()).isEqualTo(member.getId());
        assertThat(foundMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(foundMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }

    @Test
    public void basicCRUD() {
        MemberEntity member1 = new MemberEntity("member1");
        MemberEntity member2 = new MemberEntity("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        MemberEntity findMember1 = memberRepository.findById(member1.getId()).get();
        MemberEntity findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<MemberEntity> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();

        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void testQueryMethod() {
        MemberEntity member = new MemberEntity("memberA", 20);

        memberRepository.save(member);

        List<MemberEntity> memberA = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 19);

        System.out.println("memberA = " + memberA);

    }

    @Test
    void testNamedQueryMethod() {
        MemberEntity member = new MemberEntity("memberA", 20);

        memberRepository.save(member);

        List<MemberEntity> memberA = memberRepository.findPageByAge(20);

        System.out.println("memberA = " + memberA);

    }

    /**
     * Spring Data JPA Paging Test
     */
    @Test
    void paging() {
        // given
        memberRepository.save(new MemberEntity("member1", 10));
        memberRepository.save(new MemberEntity("member2", 10));
        memberRepository.save(new MemberEntity("member3", 10));
        memberRepository.save(new MemberEntity("member4", 10));
        memberRepository.save(new MemberEntity("member5", 10));

        int age = 10;
        // (참고)페이지는 0부터 시작
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<MemberEntity> page = memberRepository.findPageByAge(age, pageRequest);

        // then
        List<MemberEntity> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    /**
     * Spring Data JPA Slice Test
     */
    @Test
    void slice() {
        // given
        memberRepository.save(new MemberEntity("member1", 10));
        memberRepository.save(new MemberEntity("member2", 10));
        memberRepository.save(new MemberEntity("member3", 10));
        memberRepository.save(new MemberEntity("member4", 10));
        memberRepository.save(new MemberEntity("member5", 10));

        int age = 10;
        // (참고)페이지는 0부터 시작
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Slice<MemberEntity> page = memberRepository.findSliceByAge(age, pageRequest);
        // Dto 변환이 필요한 경우
        Slice<MemberDto> pageWithDto = page.map(e -> new MemberDto(e.getId(), e.getUsername()));

        // then
        List<MemberEntity> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    /**
     * Spring Data JPA Bulk Update Query test
     */
    @Test
    void bulkUpdate() {
        // given
        memberRepository.save(new MemberEntity("member1", 10));
        memberRepository.save(new MemberEntity("member2", 19));
        memberRepository.save(new MemberEntity("member3", 20));
        memberRepository.save(new MemberEntity("member4", 21));
        memberRepository.save(new MemberEntity("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.clear();  // 영속성컨텍스트 직접 Clear

        MemberEntity member5 = memberRepository.findByUsername("member5").get(0);
        /**
         * Bulk Update 사용시 주의사항
         *  - Bulk Update로 DB에 직접 쿼리한 경우 영속성컨텍스트를 비워주지 앖으면
         *    영속성 컨텍스트와 DB간 데이터 차이가 발생하기 떄문에 데이터 정합성이 깨진다.
         *  - @Modifying(clearAutomatically = true) 옵션을 사용하거나
         *    EntityManager를 사용하여 직접 영속성컨텍스트를 Clear 해줘야 한다.
         */
        System.out.println(member5.getAge());  // BulkUpdate 이후 영속성컨텍스트

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    /**
     * EntityGraph
     */
    @Test
    void findMemberLazy() {
        // given
        // Member1 -> teamA
        // Member2 -> teamB
        TeamEntity teamA = new TeamEntity("TeamA");
        TeamEntity teamB = new TeamEntity("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        MemberEntity member1 = new MemberEntity("member1", 10, teamA);
        MemberEntity member2 = new MemberEntity("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        // select Member
//        List<MemberEntity> members = memberRepository.findMemberFetchJoin();
        List<MemberEntity> members = memberRepository.findAll();

        for (MemberEntity member : members) {
            System.out.println("member = " + member);
            System.out.println("teamProxy = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    /**
     * JPA Hint
     *  - readOnly
     */
    @Test
    void queryHint() {
        // given
        MemberEntity member1 = new MemberEntity("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        /**
         * Hibernate의 readOnly Hint를 사용할 경우 snapshot 기능을 제공하지 않아 변경감지가 동작하지 않는다.
         *  - 변경감지가 필요하지 않은 단순 조회의 경우 성능개선
         *  - 실제로 Hint가 필요한지는 실무에서 직접 판단하여 사용해야 한다.
         */
//        MemberEntity findMemberEntity = memberRepository.findById(member1.getId()).get();
        MemberEntity findMemberEntity = memberRepository.findReadOnlyByUsername("member1").get(0);
        findMemberEntity.setUsername("member2");

        em.flush();
    }

    /**
     * JPA Hint
     *  - lock
     */
    @Test
    void lock() {
        // given
        MemberEntity member1 = new MemberEntity("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<MemberEntity> lockByUsername = memberRepository.findLockByUsername("member1");
    }

    /**
     * CustomRepository
     *  - 복잡한 쿼리를 사용해야 하는 경우 별도의 CustomRepository 인터페이스와 구현체를 만들고
     *    기존의 repository가 해당 CustomRepository를 다중 상속하도록 한다.
     *  - CutomRepository 구현체에는 ~Impl Postfix가 붙도록 한다(설정에서 변경가능)
     *  - 핵심 비즈니스 관련 쿼리와 화면이나 특정 API에 맞춘 궈리는 Repository를 구분하기도 한다.
     */
    @Test
    void customRepository() {
        // given
        MemberEntity memberA = new MemberEntity("memberA", 10);
        MemberEntity memberB = new MemberEntity("memberB", 20);

        em.persist(memberA);
        em.persist(memberB);

        // when
        List<MemberEntity> findMember = memberRepository.selectCustomMember();

        // then
        assertThat(findMember.size()).isEqualTo(1);
        assertThat(findMember.get(0).getAge()).isGreaterThanOrEqualTo(19);
    }

    /**
     * Audit Column
     */
    @Test
    void audit() throws Exception {
        // given
        MemberEntity memberA = new MemberEntity("memberA", 20);
        memberRepository.save(memberA);

        Thread.sleep(1000);

        memberA.setUsername("updatedMemberA");

        em.flush();
        em.clear();

        // when
        MemberEntity foundMember = memberRepository.findById(memberA.getId()).get();

        // then
        System.out.println("foundMember.getCreateBy() = " + foundMember.getCreateBy());
        System.out.println("foundMember.getCreatedDate() = " + foundMember.getCreatedDate());
        System.out.println("foundMember.getLastModifiedBy() = " + foundMember.getLastModifiedBy());
        System.out.println("foundMember.getLastModifiedDate() = " + foundMember.getLastModifiedDate());
    }

    /**
     * Specification
     *  - JPA Criteria Specification을 파라미터로 넘겨서 조회할 수 있도록 스프링 데이터 JPA에서 제공하는 기능
     *  - JPA Criteria는 너무 복잡하기 때문에 QueryDSL을 사용하자...
     */
    @Test
    void specBasic() {
        // given
        TeamEntity teamA = new TeamEntity("teamA");
        em.persist(teamA);

        MemberEntity memberA = new MemberEntity("memberA", 10, teamA);
        MemberEntity memberB = new MemberEntity("memberB", 10, teamA);
        em.persist(memberA);
        em.persist(memberB);

        em.flush();
        em.clear();

        // when
        Specification<MemberEntity> spec = MemberSpec.username("memberA").and(MemberSpec.teamName("teamA"));
        List<MemberEntity> result = memberRepository.findAll(spec);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    /**
     * QueryByExample
     *  - JpaRepository에서 기본적으로 상속받아 구현됨
     *  - Example로 감싼 Entity를 직접 조건으로 전달하여 조회 가능
     *  - 매칭조건이 단순하고, Left Join을 제공하지 않기 떄문에 실무에서는 QueryDSL을 사용하자...
     */
    @Test
    public void basic() throws Exception {
        // given
        TeamEntity teamA = new TeamEntity("teamA");
        em.persist(teamA);
        em.persist(new MemberEntity("m1", 0, teamA));
        em.persist(new MemberEntity("m2", 0, teamA));
        em.flush();

        // when
        // Probe 생성
        MemberEntity member = new MemberEntity("m1");
        TeamEntity team = new TeamEntity("teamA"); // 내부조인으로 teamA 가능
        member.setTeam(team);

        // ExampleMatcher 생성, age 프로퍼티는 무시
        ExampleMatcher matcher = ExampleMatcher.matching()
                                               .withIgnorePaths("age");
        Example<MemberEntity> example = Example.of(member, matcher);

        List<MemberEntity> result = memberRepository.findAll(example);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    /**
     * Projections
     */
    @Test
    public void projections() throws Exception {
        // given
        TeamEntity teamA = new TeamEntity("teamA");
        em.persist(teamA);
        em.persist(new MemberEntity("m1", 0, teamA));
        em.persist(new MemberEntity("m2", 0, teamA));
        em.flush();
        em.clear();

        // when
//        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsDtoByUsername("m1");
        // 동적 Projection
//        List<UsernameOnly> result = memberRepository.findProjectionTypeByUsername("m1", UsernameOnly.class);
        // 중첩 Projection
        List<NestedClosedProjections> result = memberRepository.findProjectionTypeByUsername("m1", NestedClosedProjections.class);

        // then
        for (NestedClosedProjections usernameOnly : result) {
            System.out.println("usernameOnly.getUsername() = " + usernameOnly.getUsername());
            System.out.println("usernameOnly.getTeam() = " + usernameOnly.getTeam());
        }

    }

    /**
     * Native Query
     *  - QueryDSL이나 Spring JDBC Template 사용을 권장...
     */
    @Test
    void nativeQuery() {

        // given
        TeamEntity teamA = new TeamEntity("teamA");
        em.persist(teamA);

        em.persist(new MemberEntity("m1", 0, teamA));
        em.persist(new MemberEntity("m2", 0, teamA));

        em.flush();
        em.clear();

        // when
//        MemberEntity result = memberRepository.findByNativeQuery("m1");
        // Spring Data JPA Projection 사용 native query
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));

        // then
//        assertThat(result.getUsername()).isEqualTo("m1");

        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection);
        }
    }

}