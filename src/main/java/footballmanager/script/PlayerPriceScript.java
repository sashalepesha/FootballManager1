package footballmanager.script;

import footballmanager.domain.Player;
import footballmanager.domain.Transfer;

public interface PlayerPriceScript {
    String GENERATED_CLASS_NAME = "GeneratedPlayerPriceScript";

    void updatePrice(Player player, Transfer transfer);
}
