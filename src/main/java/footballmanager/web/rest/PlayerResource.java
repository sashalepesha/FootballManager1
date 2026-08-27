package footballmanager.web.rest;

import footballmanager.domain.Player;
import footballmanager.service.PlayerService;
import footballmanager.service.PlayerService.PagedPlayers;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
public class PlayerResource {

    private final PlayerService playerService;

    public PlayerResource(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAll(@PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        long start = System.currentTimeMillis();
        PagedPlayers page = playerService.findAll(pageable);
        long elapsedMs = System.currentTimeMillis() - start;
        System.out.println(
            "GET /api/players page=" + pageable.getPageNumber() + " size=" + pageable.getPageSize() + " took " + elapsedMs + " ms"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.totalElements()));
        headers.add("X-Total-Pages", String.valueOf(page.totalPages()));
        return ResponseEntity.ok().headers(headers).body(page.content());
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
    public Player update(@PathVariable Long id, @RequestBody Player player) {
        return playerService.save(player);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        playerService.delete(id);
    }
}
