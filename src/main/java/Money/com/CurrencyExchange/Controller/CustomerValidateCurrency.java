package Money.com.CurrencyExchange.Controller;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@Getter
@Setter
public class CustomerValidateCurrency {

    private String Code;
    private String FullName;
    private char Sign;


    public List<String> validate() {
        List<String> errors = new ArrayList<>();


        if (getFullName() == null || getFullName().isEmpty() || getFullName().length() < 5) {
            errors.add("Error of Name!");
        }else if((getCode().length() < 3)){
            errors.add("Error of code!");
        }else{
            char sign = getSign();
            String er = "/u0000";
            if (String.valueOf(sign).equals(er)) {
                errors.add("Sign is null!");

            }
        }

        return  errors;
    }
}
