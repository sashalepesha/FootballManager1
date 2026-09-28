package footballmanager.service.imports;

import footballmanager.domain.Player;
import footballmanager.domain.Team;
import footballmanager.repository.PlayerRepository;
import footballmanager.repository.TeamRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PlayerEntityImporter implements EntityImporter<Player> {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerEntityImporter(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public String entityType() {
        return "player";
    }

    @Override
    public Player mapRecord(Map<String, Object> record) throws ImportRecordException {
        Player player = PlayerFieldMapper.mapBasicFields(record);

        String teamIdRaw = PlayerFieldMapper.optionalString(record, "teamId");
        if (teamIdRaw != null && !teamIdRaw.isBlank()) {
            Long teamId;
            try {
                teamId = Long.parseLong(teamIdRaw.trim());
            } catch (NumberFormatException e) {
                throw new ImportRecordException("teamId должен быть числом: " + teamIdRaw);
            }
            Team team = teamRepository.findById(teamId).orElse(null);
            if (team == null) {
                throw new ImportRecordException(
                    "Команда с id=" + teamId + " не найдена (игрок " + player.getFirstName() + " " + player.getLastName() + ")"
                );
            }
            player.setTeam(team);
        }

        return player;
    }

    @Override
    public void saveAll(List<Player> entities) {
        playerRepository.saveAll(entities);
    }
}
