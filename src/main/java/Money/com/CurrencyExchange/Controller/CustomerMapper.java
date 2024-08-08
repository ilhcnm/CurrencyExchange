package Money.com.CurrencyExchange.Controller;

import Money.com.CurrencyExchange.Model.CustomerDTO;

public class CustomerMapper{
    public static CustomerValidateCurrency fromDTO(CustomerDTO dto){
        return  new CustomerValidateCurrency(
                dto.getCode(),
                dto.getFullName(),
                dto.getSign()
        );
    }
}

