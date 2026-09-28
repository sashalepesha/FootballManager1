package footballmanager.service.imports;

import footballmanager.domain.Player;
import footballmanager.domain.Team;
import java.util.List;

public record TeamImportPayload(Team team, List<Player> players) {}
