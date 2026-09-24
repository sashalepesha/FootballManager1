package footballmanager.service.imports;

import footballmanager.domain.Player;
import footballmanager.domain.Team;
import footballmanager.repository.PlayerRepository;
import footballmanager.repository.TeamRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    public Player mapRecord(Map<String, String> record) throws ImportRecordException {
        String firstName = required(record, "firstName");
        String lastName = required(record, "lastName");

        Player player = new Player();
        player.setFirstName(firstName);
        player.setLastName(lastName);
        player.setNationality(record.get("nationality"));
        player.setPosition(record.get("position"));

        String birthDateRaw = record.get("birthDate");
        if (birthDateRaw != null && !birthDateRaw.isBlank()) {
            try {
                player.setBirthDate(LocalDate.parse(birthDateRaw.trim()));
            } catch (NumberFormatException e) {
                throw new ImportRecordException(
                    "Некорретный формат birthDate у игрока" + firstName + " " + lastName + " (ожидается yyyy-MM-dd): " + birthDateRaw
                );
            }
        }

        String marketValueRaw = record.get("marketValue");
        if (marketValueRaw != null && !marketValueRaw.isBlank()) {
            try {
                player.setMarketValue(new BigDecimal(marketValueRaw.trim()));
            } catch (NumberFormatException e) {
                throw new ImportRecordException(
                    "Некорректное значение marketValue у игрока " + firstName + " " + lastName + ": " + marketValueRaw
                );
            }
        }

        String teamIdRaw = record.get("teamId");
        if (teamIdRaw != null && !teamIdRaw.isBlank()) {
            Long teamId;
            try {
                teamId = Long.valueOf(teamIdRaw.trim());
            } catch (NumberFormatException e) {
                throw new ImportRecordException("teamId должен быть числом, у игрока " + firstName + " " + lastName + ": " + teamIdRaw);
            }
            Team team = teamRepository.findById(teamId).orElse(null);
            if (team == null) {
                throw new ImportRecordException("Команда с id=" + teamId + " не найдена (игрок " + firstName + " " + lastName + ")");
            }
            player.setTeam(team);
        }

        return player;
    }

    @Override
    public void saveAll(List<Player> entities) {
        playerRepository.saveAll(entities);
    }

    private String required(Map<String, String> record, String field) throws ImportRecordException {
        String value = record.get(field);
        if (value == null || value.isBlank()) {
            throw new ImportRecordException("Обязательное поле '" + field + "' отсутствует или пустое в записи: " + record);
        }
        return value.trim();
    }
}
