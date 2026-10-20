package footballmanager.web.rest;

import footballmanager.repository.TeamRepository;
import footballmanager.service.DynamicPlayerTableService;
import footballmanager.service.DynamicTeamService;
import footballmanager.service.dto.DynamicPlayerDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams/{teamId}/dynamic-players")
public class DynamicPlayerResource {

    private final DynamicPlayerTableService playerService;
    private final DynamicTeamService teamService;
    private final TeamRepository teamRepository;

    public DynamicPlayerResource(DynamicPlayerTableService playerService, DynamicTeamService teamService, TeamRepository teamRepository) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.teamRepository = teamRepository;
    }

    private void checkTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new EntityNotFoundException("Team not found: " + teamId);
        }

        teamService.ensurePlayerTableExists(teamId);
    }

    @PostMapping
    public ResponseEntity<DynamicPlayerDTO> createPlayer(@PathVariable Long teamId, @Valid @RequestBody DynamicPlayerDTO player) {
        checkTeam(teamId);

        DynamicPlayerDTO result = playerService.addPlayer(teamId, player);

        return ResponseEntity.created(URI.create("/api/teams/" + teamId + "/dynamic-players/" + result.id())).body(result);
    }

    @GetMapping
    public List<DynamicPlayerDTO> getPlayers(@PathVariable Long teamId) {
        checkTeam(teamId);
        return playerService.getPlayers(teamId);
    }

    @GetMapping("/{playerId}")
    public DynamicPlayerDTO getPlayer(@PathVariable Long teamId, @PathVariable Long playerId) {
        checkTeam(teamId);
        return playerService.getPlayer(teamId, playerId);
    }

    @DeleteMapping("/{playerId}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long teamId, @PathVariable Long playerId) {
        checkTeam(teamId);
        playerService.deletePlayer(teamId, playerId);
        return ResponseEntity.noContent().build();
    }
}
