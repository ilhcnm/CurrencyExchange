package Money.com.CurrencyExchange.Controller;

public class CustomerMapper{
    public static CustomerValidateCurrency fromDTO(CustomerDTO dto){
        return  new CustomerValidateCurrency(
                dto.getCode(),
                dto.getFullName(),
                dto.getSign()
        );
    }
}

