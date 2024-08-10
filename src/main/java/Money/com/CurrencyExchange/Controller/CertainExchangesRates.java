package Money.com.CurrencyExchange.Controller;

import Money.com.CurrencyExchange.DB.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;


@WebServlet("/exchangeRate/*")
public class CertainExchangesRates extends HttpServlet {
    CustomerRepository customerRepository = new CustomerRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String CurrencyExchangesCode = pathInfo.substring(1);

        String BasedCurrencyCode = CurrencyExchangesCode.substring(0,3);
        String TargetCurrencyCode = CurrencyExchangesCode.substring(3,6);
        int idBasedCurrencyCode = customerRepository.FindByCodeforExchangesRates(BasedCurrencyCode);
        int idTargetCurrencyCode = customerRepository.FindByCodeforExchangesRates(TargetCurrencyCode);

        if(pathInfo.trim().isEmpty() || pathInfo.equals("/")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("Request parameter is missing");
            return;
        }
        else if(CurrencyExchangesCode.length() != 6){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("CurrencyExchangesCode is missing");
            return;
        }if(idBasedCurrencyCode == -1 || idTargetCurrencyCode == -1){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"One or both currencies not found in the database\"}");
        }else{
            try {
                if(customerRepository.FindById(idBasedCurrencyCode, idTargetCurrencyCode) != null){
                    response.setContentType("application/json");
                    String JsonResult = customerRepository.FindById(idBasedCurrencyCode, idTargetCurrencyCode);
                    response.getWriter().write(JsonResult);
                    response.setStatus(HttpServletResponse.SC_OK);
                    }
            } catch (JsonProcessingException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("error! not found ExchangesRates for those codes!");
            }

        }




    }
}
