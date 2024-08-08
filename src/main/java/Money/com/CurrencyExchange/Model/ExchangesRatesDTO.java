package Money.com.CurrencyExchange.Model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExchangesRatesDTO {

    private int baseCurrencyCode;
    private int targetCurrencyCode;
    private double rate;



}
