package footballmanager.service.imports;

import footballmanager.domain.Player;
import footballmanager.domain.Team;
import footballmanager.repository.PlayerRepository;
import footballmanager.repository.TeamRepository;
import footballmanager.service.BatchImportService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TeamEntityImporter implements EntityImporter<TeamImportPayload> {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final BatchImportService batchImportService;

    public TeamEntityImporter(TeamRepository teamRepository, PlayerRepository playerRepository, BatchImportService batchImportService) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.batchImportService = batchImportService;
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
        if (entities.isEmpty()) {
            return;
        }

        List<Team> teams = entities.stream().map(TeamImportPayload::team).toList();

        batchImportService.saveAllWithoutBatch(teams, teamRepository::saveAll);

        List<Player> allPlayers = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            Team team = teams.get(i);

            for (Player player : entities.get(i).players()) {
                player.setTeam(team);
                allPlayers.add(player);
            }
        }

        if (!allPlayers.isEmpty()) {
            batchImportService.saveAllWithoutBatch(allPlayers, playerRepository::saveAll);
        }
    }

    @Override
    public void batchSave(List<TeamImportPayload> entities, BatchImportService batchImportService) {
        if (entities.isEmpty()) {
            return;
        }

        List<Team> teams = entities.stream().map(TeamImportPayload::team).toList();

        batchImportService.batchImport(teams);

        List<Player> allPlayers = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            Team team = teams.get(i);

            for (Player player : entities.get(i).players()) {
                player.setTeam(team);
                allPlayers.add(player);
            }
        }
    }
}
