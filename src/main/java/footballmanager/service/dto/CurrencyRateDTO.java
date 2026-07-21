package footballmanager.service.dto;

import java.math.BigDecimal;

public class CurrencyRateDTO {

    private String Cur_Abbreviation;

    private Integer Cur_Scale;

    private BigDecimal Cur_OfficialRate;

    public String getCur_Abbreviation() {
        return Cur_Abbreviation;
    }

    public void setCur_Abbreviation(String cur_Abbreviation) {
        Cur_Abbreviation = cur_Abbreviation;
    }

    public Integer getCur_Scale() {
        return Cur_Scale;
    }

    public void setCur_Scale(Integer cur_Scale) {
        Cur_Scale = cur_Scale;
    }

    public BigDecimal getCur_OfficialRate() {
        return Cur_OfficialRate;
    }

    public void setCur_OfficialRate(BigDecimal cur_OfficialRate) {
        Cur_OfficialRate = cur_OfficialRate;
    }
}
