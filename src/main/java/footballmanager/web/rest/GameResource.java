package footballmanager.web.rest;

import footballmanager.domain.Game;
import footballmanager.service.GameService;
import footballmanager.service.GameService.PagedGames;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameResource {

    private final GameService gameService;

    public GameResource(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAll(
        @PageableDefault(size = 20, sort = "matchDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        long start = System.currentTimeMillis();
        PagedGames page = gameService.findAll(pageable);
        long elapsedMs = System.currentTimeMillis() - start;
        System.out.println(
            "GET api/games page" + pageable.getPageNumber() + " size=" + pageable.getPageSize() + " took " + elapsedMs + " ms"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.totalElements()));
        headers.add("X-Total-Pages", String.valueOf(page.totalPages()));
        return ResponseEntity.ok().headers(headers).body(page.content());
    }

    @GetMapping("/{id}")
    public Game get(@PathVariable Long id) {
        return gameService.findOne(id);
    }

    @GetMapping("/team/{teamId}")
    public List<Game> getByHomeTeam(@PathVariable Long teamId) {
        return gameService.findByHomeTeam(teamId);
    }

    @PostMapping
    public Game create(@RequestBody Game game) {
        return gameService.save(game);
    }

    @PutMapping("/{id}")
    public Game update(@PathVariable Long id, @RequestBody Game game) {
        return gameService.save(game);
    }

    @PostMapping("/generate")
    public int generate(@RequestParam(defaultValue = "500") int count) {
        return gameService.generateRandom(count);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        gameService.delete(id);
    }

    @DeleteMapping
    public void deleteAll() {
        gameService.deleteAll();
    }
}
