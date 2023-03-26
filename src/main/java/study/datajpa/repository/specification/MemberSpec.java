package study.datajpa.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.MemberEntity;
import study.datajpa.entity.TeamEntity;

import javax.persistence.criteria.*;

public class MemberSpec {

    public static Specification<MemberEntity> teamName(final String teamName) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isEmpty(teamName)) {
                return null;
            }
            Join<Object, TeamEntity> t = root.join("team", JoinType.INNER);// 회원과 조인
            return criteriaBuilder.equal(t.get("name"), teamName);
        };
    }

    public static Specification<MemberEntity> username(final String username) {
        return ((root, query, criteriaBuilder) -> {
            if (StringUtils.isEmpty(username)) {
                return null;
            }
            return criteriaBuilder.equal(root.get("username"), username);
        });
    }
}
