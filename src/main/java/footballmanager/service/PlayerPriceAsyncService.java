package footballmanager.service;

import footballmanager.domain.Player;
import footballmanager.domain.Transfer;
import footballmanager.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerPriceAsyncService {

    private static final Logger log = LoggerFactory.getLogger(PlayerPriceAsyncService.class);

    private final PlayerService playerService;
    private final ApplicationScriptService applicationScriptService;
    private final TransferRepository transferRepository;

    public PlayerPriceAsyncService(
        PlayerService playerService,
        ApplicationScriptService applicationScriptService,
        TransferRepository transferRepository
    ) {
        this.playerService = playerService;
        this.applicationScriptService = applicationScriptService;
        this.transferRepository = transferRepository;
    }

    @Async("playerPriceTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void runPlayerPriceScriptAsync(Long playerId, Long transferId) {
        log.info("runPlayerPriceScriptAsync() running in thread: {}", Thread.currentThread().getName());
        Player player = playerService.findOne(playerId);
        Transfer transfer = transferRepository.findById(transferId).orElseThrow(() -> new RuntimeException("Transfer not found"));

        try {
            applicationScriptService.updatePlayerPrice(player, transfer);
            playerService.save(player);
        } catch (Exception e) {
            log.error("Failed to update player price for transfer id={}: {} ", transferId, e.getMessage(), e);
        }
    }
}
