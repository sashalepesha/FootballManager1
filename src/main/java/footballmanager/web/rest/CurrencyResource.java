package footballmanager.web.rest;

import footballmanager.service.CurrencyService;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currency")
public class CurrencyResource {

    private final CurrencyService currencyService;

    public CurrencyResource(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/usd")
    public Map<String, BigDecimal> getUsdRate() {
        return Map.of("usdToByn", currencyService.getUsdRate());
    }
}
