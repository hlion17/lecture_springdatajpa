package study.datajpa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String username;

    public MemberDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
