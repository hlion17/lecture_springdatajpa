package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.MemberEntity;
import study.datajpa.repository.projection.MemberProjection;
import study.datajpa.repository.projection.UsernameOnly;
import study.datajpa.repository.projection.UsernameOnlyDto;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, MemberCustomRepository, JpaSpecificationExecutor<MemberEntity> {
    List<MemberEntity> findByUsername(String username);
    List<MemberEntity> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByAge")
    List<MemberEntity> findPageByAge(@Param("age") int age);

    @Query(value = "select m from MemberEntity m left join m.team t",
            countQuery = "select count(m.username) from MemberEntity m")
    Page<MemberEntity> findPageByAge(int age, Pageable pageable);

    Slice<MemberEntity> findSliceByAge(int age, Pageable pageable);

    /**
     * (참고)QueryMethod 기능을 사용한 TopN 쿼리
     */
    List<MemberEntity> findTop3ByAge(int age);

    /**
     * Spring Data JPA Bulk Update Query
     *  - @Modifying 어노테이션이 없으면 Update 쿼리로 인식하지 않는다. -> invalidDataAccessApiUsageException 발생
     */
    @Modifying(clearAutomatically = true)
    @Query("update MemberEntity m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     * EntityGraph 탐색방법
     *  - FetchJoin하여 간단하게 객체를 탐색 할 때 사용(복잡할 땐 보통 JPQL에서 바로 FetchJoin 하여 객체 탐색)
     *  - Spring Data JPA의 @EntityGraph 어노테이션 사용(FetchJoin할 Node를 옵션에 선언)
     *  Case:
     *  1. 단순 JPA에서 Fetch Join을 사용한 객체그래프 탐색
     *  2. JpaRepository의 findAll 메소드를 직접 Override 하여 사용
     *  3. 직접 JPQL + @EntityGraph
     *  4. QueryMethod + @EntityGraph
     *  5. NamedQuery + @EntityGraph (Entity에 @NamedEntityGraph 선언 필요)
     */
    // 1.
    @Query("select m from MemberEntity m join fetch m.team")
    List<MemberEntity> findMemberFetchJoin();
    // 2.
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<MemberEntity> findAll();
    // 3.
    @Query("select m from MemberEntity m")
    @EntityGraph(attributePaths = {"team"})
    List<MemberEntity> findMemberEntityGraph();
    // 4.
    @EntityGraph(attributePaths = {"team"})
    List<MemberEntity> findEntityGraphByUsername(@Param("username") String username);
    // 5.
    @EntityGraph("Member.all")
    List<MemberEntity> findNamedEntityGraphByUsername(@Param("username") String username);

    /**
     * JPA 기본 스펙에서는 제공하지는 않지만 JPA 구현체에서 제공하는 기능을 쓸 수 있는 길을 열어놓음
     *  - readOnly: dirtyChecking 등 1차 캐시기능이 굳이 필요없는 경우 snapshot 기능을 제공하지 않아 성능 개선
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<MemberEntity> findReadOnlyByUsername(String username);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<MemberEntity> findLockByUsername(String username);

    @Override
    List<MemberEntity> selectCustomMember();

    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

    List<UsernameOnlyDto> findProjectionsDtoByUsername(@Param("username") String username);

    <T> List<T> findProjectionTypeByUsername(@Param("username") String username, Class<T> type);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    MemberEntity findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m left join team t",
            countQuery = "select coutn(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
