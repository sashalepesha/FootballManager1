package footballmanager.service;

import footballmanager.domain.Player;
import footballmanager.repository.PlayerRepository;
import footballmanager.security.AuthoritiesConstants;
import footballmanager.security.SecurityUtils;
import java.io.Serializable;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
        key = "T(footballmanager.security.SecurityUtils).getCurrentUserLoginOrAnonymous() + " +
            "'-v' + @playerCacheVersionService.getCurrentVersion(T(footballmanager.security.SecurityUtils).getCurrentUserLoginOrAnonymous()) + " +
            "'-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
    )
    public PagedPlayers findAll(Pageable pageable) {
        Page<Player> page = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
            ? playerRepository.findAll(pageable)
            : playerRepository.findAllByTeam_Manager_Login(SecurityUtils.getCurrentUserLoginOrAnonymous(), pageable);
        return new PagedPlayers(page.getContent(), page.getTotalElements(), page.getTotalPages());
    }

    public List<Player> findByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public Player save(Player player) {
        return internalSave(player, SecurityUtils.getCurrentUserLoginOrAnonymous());
    }

    public Player internalSave(Player player, String username) {
        Player saved = playerRepository.save(player);
        playerCacheVersionService.incrementVersion(username);
        return saved;
    }

    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public void delete(Long id) {
        playerRepository.deleteById(id);
        playerCacheVersionService.incrementVersion(SecurityUtils.getCurrentUserLoginOrAnonymous());
    }

    @Cacheable(
        value = "player",
        key = "T(footballmanager.security.SecurityUtils).getCurrentUserLoginOrAnonymous() + " +
            "'-v' + " +
            "@playerCacheVersionService.getCurrentVersion(T(footballmanager.security.SecurityUtils).getCurrentUserLoginOrAnonymous()) + " +
            "'-' + #id"
    )
    public Player findOne(Long id) {
        return playerRepository.findById(id).orElseThrow();
    }

    public Player internalFindOne(Long id) {
        return playerRepository.findById(id).orElseThrow();
    }

    public record PagedPlayers(List<Player> content, long totalElements, int totalPages) implements Serializable {}
}
