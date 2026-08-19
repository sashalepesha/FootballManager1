package footballmanager.service;

import footballmanager.domain.ApplicationScript;
import footballmanager.domain.Player;
import footballmanager.domain.Transfer;
import footballmanager.repository.ApplicationScriptRepository;
import footballmanager.script.PlayerPriceScript;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mdkt.compiler.InMemoryJavaCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApplicationScriptService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationScriptService.class);

    public static final String PLAYER_PRICE_SCRIPT_NAME = "PLAYER_PRICE_UPDATE_SCRIPT";

    private final ApplicationScriptRepository applicationScriptRepository;

    private final Map<Long, CachedScript> cache = new ConcurrentHashMap<>();

    public ApplicationScriptService(ApplicationScriptRepository applicationScriptRepository) {
        this.applicationScriptRepository = applicationScriptRepository;
    }

    public void updatePlayerPrice(Player player, Transfer transfer) {
        ApplicationScript script = applicationScriptRepository
            .findByName(PLAYER_PRICE_SCRIPT_NAME)
            .orElseThrow(() -> new IllegalStateException("No ApplicationScript found with name '" + PLAYER_PRICE_SCRIPT_NAME + "'"));

        PlayerPriceScript compiledScript = getOrCompile(script);
        compiledScript.updatePrice(player, transfer);
    }

    private PlayerPriceScript getOrCompile(ApplicationScript script) {
        String currentHash = sha256(script.getCode());
        CachedScript cached = cache.get(script.getId());

        if (cached != null && cached.codeHash.equals(currentHash)) {
            return cached.instance;
        }

        PlayerPriceScript instance = compile(script.getCode());
        cache.put(script.getId(), new CachedScript(currentHash, instance));
        log.info("Compiled and cached ApplicationScript '{}' (id={})", script.getName(), script.getId());
        return instance;
    }

    @SuppressWarnings("unchecked")
    private PlayerPriceScript compile(String sourceCode) {
        try {
            Class<?> compiledClass = InMemoryJavaCompiler.newInstance().compile(PlayerPriceScript.GENERATED_CLASS_NAME, sourceCode);

            Object instance = compiledClass.getDeclaredConstructor().newInstance();

            if (!(instance instanceof PlayerPriceScript)) {
                throw new IllegalStateException(
                    "Compiled class " + PlayerPriceScript.GENERATED_CLASS_NAME + " does not implement PlayerPriceScript"
                );
            }

            return (PlayerPriceScript) instance;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compile ApplicationScript: " + e.getMessage(), e);
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private record CachedScript(String codeHash, PlayerPriceScript instance) {}
}
