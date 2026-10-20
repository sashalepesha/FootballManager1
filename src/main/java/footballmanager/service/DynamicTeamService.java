package footballmanager.service;

import footballmanager.domain.Team;
import footballmanager.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DynamicTeamService {

    private final TeamRepository teamRepository;
    private final DynamicPlayerTableService playerTableService;

    public DynamicTeamService(TeamRepository teamRepository, DynamicPlayerTableService playerTableService) {
        this.teamRepository = teamRepository;
        this.playerTableService = playerTableService;
    }

    @Transactional
    public Team createTeam(Team team) {
        Team savedTeam = teamRepository.saveAndFlush(team);

        playerTableService.createTableForTeam(savedTeam.getId());

        return savedTeam;
    }

    @Transactional
    public void ensurePlayerTableExists(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new EntityNotFoundException("Team not found: " + teamId);
        }

        playerTableService.createTableForTeam(teamId);
    }
}
