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

        String name = getFullName();
        String nam = String.valueOf(name);
        if (getFullName() == null || getFullName().isEmpty() || nam.length() < 5) {
            errors.add("Error of Name!");
        }


        String code = getCode();
        String s = String.valueOf(code);
        if (s.length() < 3) {

            errors.add("Error of code!");
        }

        char sign = getSign();
        String er = "/u0000";
        if (String.valueOf(sign).equals(er)) {
            errors.add("Sign is null!");
        }
        return  errors;
    }
}
