package Money.com.CurrencyExchange.Model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ExchangesRatesModel {
    private Object BaseCurrencyCode;
    private Object TargetCurrencyCode;
    private double Rate;
}
