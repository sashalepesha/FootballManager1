package footballmanager.service;

import footballmanager.domain.Player;
import footballmanager.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public List<Player> findByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    public Player save(Player player) {
        return playerRepository.save(player);
    }

    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    public Player findOne(Long id) {
        return playerRepository.findById(id).orElseThrow();
    }
}
