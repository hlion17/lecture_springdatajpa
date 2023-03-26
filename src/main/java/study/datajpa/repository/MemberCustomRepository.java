package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.MemberEntity;

import java.util.List;

@Repository
public interface MemberCustomRepository {
    List<MemberEntity> selectCustomMember();
}
