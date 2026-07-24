package footballmanager.service;

import footballmanager.domain.Game;
import footballmanager.domain.Team;
import footballmanager.repository.GameRepository;
import footballmanager.repository.TeamRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private static final Random RANDOM = new Random();

    private static final int MAX_SCORE = 6;

    private static final int DAYS_RANGE = 365;

    private final GameRepository gameRepository;

    private final TeamRepository teamRepository;

    public GameService(GameRepository gameRepository, TeamRepository teamRepository) {
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
    }

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public Game findOne(Long id) {
        return gameRepository.findById(id).orElseThrow();
    }

    public Game save(Game game) {
        return gameRepository.save(game);
    }

    public void delete(Long id) {
        gameRepository.deleteById(id);
    }

    /**
     * Генерирует случайные матчи между существующими командами.
     *
     * @param count сколько матчей сгенерировать
     * @return количество реально созданных матчей
     */
    public int generateRandom(int count) {
        List<Team> teams = teamRepository.findAll();

        if (teams.size() < 2) {
            throw new IllegalStateException("Для генерации матчей нужно как минимум 2 команды");
        }

        Instant now = Instant.now();

        List<Game> generated = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Team home = teams.get(RANDOM.nextInt(teams.size()));
            Team away = teams.get(RANDOM.nextInt(teams.size()));

            while (away.getId().equals(home.getId())) {
                away = teams.get(RANDOM.nextInt(teams.size()));
            }

            Game game = new Game();
            game.setHomeTeam(home);
            game.setAwayTeam(away);
            game.setHomeScore(RANDOM.nextInt(MAX_SCORE));
            game.setAwayScore(RANDOM.nextInt(MAX_SCORE));
            game.setStadium(home.getStadium() != null && !home.getStadium().isBlank() ? home.getStadium() : "Central Stadium");
            game.setMatchDate(now.minus(RANDOM.nextInt(DAYS_RANGE), ChronoUnit.DAYS));

            generated.add(game);
        }

        gameRepository.saveAll(generated);

        return generated.size();
    }
}
