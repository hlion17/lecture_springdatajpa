package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "MEMBER")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQuery(
        name = "Member.findByAge",
        query = "select m from MemberEntity m where m.age = :age"
)
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class MemberEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "TEAM_ID")
    private TeamEntity team;

    public void changeTeam(TeamEntity team) {
        this.team = team;
        team.getMembers().add(this);
    }

    public MemberEntity(String username) {
        this.username = username;
    }

    public MemberEntity(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public MemberEntity(String username, int age, TeamEntity team) {
        this.username = username;
        this.age = age;
        this.team = team;
    }
}
