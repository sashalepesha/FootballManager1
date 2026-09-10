package footballmanager.repository;

import footballmanager.domain.Game;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByHomeTeamId(Long homeTeamId);

    @Query("select g from Game g where g.homeTeam.manager.login = :login or g.awayTeam.manager.login = :login")
    Page<Game> findAllByManagerLogin(@Param("login") String login, Pageable pageable);
}
