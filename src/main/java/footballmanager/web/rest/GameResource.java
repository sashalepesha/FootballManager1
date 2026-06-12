package footballmanager.web.rest;

import footballmanager.domain.Game;
import footballmanager.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameResource {

    private final GameService gameService;

    public GameResource(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<Game> getAll() {
        return gameService.findAll();
    }

    @GetMapping("/{id}")
    public Game get(@PathVariable Long id) {
        return gameService.findOne(id);
    }

    @PostMapping
    public Game create(@RequestBody Game game) {
        return gameService.save(game);
    }

    @PutMapping("/{id}")
    public Game update(
        @PathVariable Long id,
        @RequestBody Game game
    ) {
        return gameService.save(game);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        gameService.delete(id);
    }
}
