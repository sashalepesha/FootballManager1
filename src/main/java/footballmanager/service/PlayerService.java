package footballmanager.service;

import footballmanager.domain.Player;
import footballmanager.repository.PlayerRepository;
import java.io.Serializable;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerCacheVersionService playerCacheVersionService;

    public PlayerService(PlayerRepository playerRepository, PlayerCacheVersionService playerCacheVersionService) {
        this.playerRepository = playerRepository;
        this.playerCacheVersionService = playerCacheVersionService;
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    @Cacheable(
        value = "players",
        key = "@playerCacheVersionService.currentVersion + '-' +#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
    )
    public PagedPlayers findAll(Pageable pageable) {
        Page<Player> page = playerRepository.findAll(pageable);
        return new PagedPlayers(page.getContent(), page.getTotalElements(), page.getTotalPages());
    }

    public List<Player> findByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    public Player save(Player player) {
        Player saved = playerRepository.save(player);
        playerCacheVersionService.incrementVersion();
        return saved;
    }

    public void delete(Long id) {
        playerRepository.deleteById(id);
        playerCacheVersionService.incrementVersion();
    }

    @Cacheable(value = "player", key = "#id")
    public Player findOne(Long id) {
        return playerRepository.findById(id).orElseThrow();
    }

    public record PagedPlayers(List<Player> content, long totalElements, int totalPages) implements Serializable {}
}
