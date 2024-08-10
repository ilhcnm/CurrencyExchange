package Money.com.CurrencyExchange.Controller;

import Money.com.CurrencyExchange.DB.CustomerRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/currency/*")
public class  CertainCurrency extends  HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        CustomerRepository customerRepository = new CustomerRepository();
        PrintWriter out = response.getWriter();

        String CurrencyCode = request.getPathInfo();

            if(CurrencyCode == null || CurrencyCode.equals("/")){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("Currency code is missing ");

            }else{
                String currencyCode = CurrencyCode.substring(1);
                String jsonResult = customerRepository.GetByCode(currencyCode);
                if(jsonResult == null){
                    out.write("This currency did not found");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }else{
                    response.setContentType("application/json");
                    response.getWriter().write(jsonResult);
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("We found this currency by code!");
                }
            }


        }





    }

