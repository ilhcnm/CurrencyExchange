package Money.com.CurrencyExchange.Dao;


import Money.com.CurrencyExchange.Controller.CustomerValidCurrency;

//the method which we write by myself it is trasformer which since the one object to  another transformed
//CustomerMapper: Это класс, который отвечает за преобразование данных из одного формата в другой.
// В данном случае он преобразует данные из объекта Custoy
//By the way CustomerValidCurrency it is type of object (why - because we need have example methods, constructor which has validcurrency
//in our case it is constructor which we use for initilisation his variables since DTO)
public class CustomerMapper{
    public static CustomerValidCurrency fromDTO(CustomerDTO dto){
        return  new CustomerValidCurrency(
                dto.getCode(),
                dto.getFullName(),
                dto.getSign()
        );
    }
}

