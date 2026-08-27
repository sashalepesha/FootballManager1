package footballmanager.service;

import footballmanager.domain.Player;
import footballmanager.repository.PlayerRepository;
import java.io.Serializable;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    @Cacheable(value = "players", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public PagedPlayers findAll(Pageable pageable) {
        Page<Player> page = playerRepository.findAll(pageable);
        return new PagedPlayers(page.getContent(), page.getTotalElements(), page.getTotalPages());
    }

    public List<Player> findByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    @CacheEvict(value = "players", allEntries = true)
    public Player save(Player player) {
        return playerRepository.save(player);
    }

    @CacheEvict(value = "players", allEntries = true)
    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    public Player findOne(Long id) {
        return playerRepository.findById(id).orElseThrow();
    }

    public record PagedPlayers(List<Player> content, long totalElements, int totalPages) implements Serializable {}
}
