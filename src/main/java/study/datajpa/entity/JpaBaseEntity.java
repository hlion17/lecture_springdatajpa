package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity {
    @Column(updatable = false)
    private LocalDateTime creationTime;
    private LocalDateTime updateTime;
    @Column(updatable = false)
    private String createUser;
    private String updateUser;

    @PrePersist
    public void preInsert() {
        LocalDateTime now = LocalDateTime.now();
        creationTime = now;
        updateTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }

}
