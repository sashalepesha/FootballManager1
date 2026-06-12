package footballmanager.web.rest;

import footballmanager.domain.Player;
import footballmanager.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerResource {

    private final PlayerService playerService;

    public PlayerResource(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<Player> getAll() {
        return playerService.findAll();
    }

    @GetMapping("/{id}")
    public Player get(@PathVariable Long id) {
        return playerService.findOne(id);
    }

    @GetMapping("/team/{teamId}")
    public List<Player> getByTeam(@PathVariable Long teamId) {
        return playerService.findByTeam(teamId);
    }

    @PostMapping
    public Player create(@RequestBody Player player) {
        return playerService.save(player);
    }

    @PutMapping("/{id}")
    public Player update(
        @PathVariable Long id,
        @RequestBody Player player
    ) {
        return playerService.save(player);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        playerService.delete(id);
    }
}
