package footballmanager.web.rest;

import footballmanager.domain.Team;
import footballmanager.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamResource {

    private final TeamService teamService;

    public TeamResource(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public List<Team> getAll() {
        return teamService.findAll();
    }

    @GetMapping("/{id}")
    public Team get(@PathVariable Long id) {
        return teamService.findOne(id);
    }

    @PostMapping
    public Team create(@RequestBody Team team) {
        return teamService.save(team);
    }

    @PutMapping("/{id}")
    public Team update(
        @PathVariable Long id,
        @RequestBody Team team
    ) {
        team.setId(id);
        return teamService.save(team);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teamService.delete(id);
    }
}
