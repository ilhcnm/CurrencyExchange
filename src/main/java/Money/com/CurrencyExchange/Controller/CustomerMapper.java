package Money.com.CurrencyExchange.Controller;

import Money.com.CurrencyExchange.Model.CustomerDTO;

public class CustomerMapper{
    public static CustomerValidateCurrency fromDTO(CustomerDTO dto){
        Long id = null;
        return  new CustomerValidateCurrency(
                id,
                dto.getCode(),
                dto.getFullName(),
                dto.getSign()
        );
    }
}

