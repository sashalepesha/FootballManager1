package footballmanager.service.imports;

import footballmanager.domain.Player;
import footballmanager.domain.Team;
import footballmanager.repository.PlayerRepository;
import footballmanager.repository.TeamRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TeamEntityImporter implements EntityImporter<TeamImportPayload> {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public TeamEntityImporter(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public String entityType() {
        return "team";
    }

    @Override
    @SuppressWarnings("unchecked")
    public TeamImportPayload mapRecord(Map<String, Object> record) throws ImportRecordException {
        String name = PlayerFieldMapper.optionalString(record, "name");
        if (name == null || name.isBlank()) {
            throw new ImportRecordException("Обязательное поле 'name' отсутствует или пустое в записи: " + record);
        }

        Team team = new Team();
        team.setName(name.trim());
        team.setCity(PlayerFieldMapper.optionalString(record, "city"));
        team.setStadium(PlayerFieldMapper.optionalString(record, "stadium"));

        String budgetRaw = PlayerFieldMapper.optionalString(record, "budget");
        if (budgetRaw != null && !budgetRaw.isBlank()) {
            try {
                team.setBudget(new BigDecimal(budgetRaw.trim()));
            } catch (NumberFormatException e) {
                throw new ImportRecordException("Некорректное значение budget у команды " + name + ": " + budgetRaw);
            }
        }

        List<Player> players = new ArrayList<>();
        Object playersRaw = record.get("players");

        if (playersRaw instanceof Map<?, ?> wrapper && wrapper.containsKey("player")) {
            Object single = wrapper.get("player");
            playersRaw = single instanceof List<?> ? single : List.of(single);
        }

        if (playersRaw instanceof List<?> playerList) {
            for (Object item : playerList) {
                if (item instanceof Map<?, ?> rawPlayerRecord) {
                    players.add(PlayerFieldMapper.mapBasicFields((Map<String, Object>) rawPlayerRecord));
                }
            }
        }

        return new TeamImportPayload(team, players);
    }

    @Override
    public void saveAll(List<TeamImportPayload> entities) {
        List<Player> allPlayersToSave = new ArrayList<>();

        for (TeamImportPayload payload : entities) {
            Team savedTeam = teamRepository.save(payload.team());
            for (Player player : payload.players()) {
                player.setTeam(savedTeam);
                allPlayersToSave.add(player);
            }
        }

        if (!allPlayersToSave.isEmpty()) {
            playerRepository.saveAll(allPlayersToSave);
        }
    }
}
