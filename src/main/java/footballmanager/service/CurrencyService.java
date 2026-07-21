package footballmanager.service;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal getUsdRate() {
        String url = "https://api.nbrb.by/exrates/rates/USD?parammode=2";

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        return new BigDecimal(response.get("Cur_OfficialRate").toString());
    }
}
