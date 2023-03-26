package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.MemberEntity;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository repository;

    /**
     * Web 확장 - 페이징과 정렬
     *  - Spring Data의 페이징 객체를 Spring MVC에서 편하게 사용할 수 있다.
     */
    @GetMapping("/v1/members")
    public Page<MemberDto> list(Pageable pageable) {
        return repository.findAll(pageable).map(e -> new MemberDto(e.getId(), e.getUsername()));
    }
}
