package study.datajpa.repository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.TeamEntity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {
    @PersistenceContext
    private EntityManager em;
    public TeamEntity save(TeamEntity TeamEntity) {
        em.persist(TeamEntity);
        return TeamEntity;
    }
    public void delete(TeamEntity TeamEntity) {
        em.remove(TeamEntity);
    }
    public List<TeamEntity> findAll() {
        return em.createQuery("select t from TeamEntity t", TeamEntity.class)
                .getResultList();
    }
    public Optional<TeamEntity> findById(Long id) {
        TeamEntity TeamEntity = em.find(TeamEntity.class, id);
        return Optional.ofNullable(TeamEntity);
    }
    public long count() {
        return em.createQuery("select count(t) from TeamEntity t", Long.class)
                .getSingleResult();
    }
}