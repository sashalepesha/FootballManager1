package footballmanager.service.imports;

import footballmanager.domain.Team;
import footballmanager.repository.TeamRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TeamEntityImporter implements EntityImporter<Team> {

    private final TeamRepository teamRepository;

    public TeamEntityImporter(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public String entityType() {
        return "team";
    }

    @Override
    public Team mapRecord(Map<String, String> record) throws ImportRecordException {
        String name = record.get("name");
        if (name == null || name.isBlank()) {
            throw new ImportRecordException("Обязательное поле 'name' отсутствует или пустое в записи: " + record);
        }

        Team team = new Team();
        team.setName(name.trim());
        team.setCity(record.get("city"));
        team.setStadium(record.get("stadium"));

        String budgetRaw = record.get("budget");
        if (budgetRaw != null && !budgetRaw.isBlank()) {
            try {
                team.setBudget(new BigDecimal(budgetRaw.trim()));
            } catch (NumberFormatException e) {
                throw new ImportRecordException("Некорректное значение budget у команды " + name + ": " + budgetRaw);
            }
        }

        return team;
    }

    @Override
    public void saveAll(List<Team> entities) {
        teamRepository.saveAll(entities);
    }
}
