package Money.com.CurrencyExchange.Controller;

import Money.com.CurrencyExchange.DB.CustomerRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange/*")
public class ExchangeCurrency extends HttpServlet {
    CustomerRepository customerRepository = new CustomerRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String BaseCurrencyCode = req.getParameter("from");
        String TargetCurrencyCode = req.getParameter("to");
        double amount = Double.parseDouble(req.getParameter("amount"));

        if(BaseCurrencyCode.trim().isEmpty() || TargetCurrencyCode.trim().isEmpty() || amount == 0){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Parameter is missing");
            return;
        }
        int idBaseCurrency = customerRepository.FindByCodeforExchangesRates(BaseCurrencyCode);
        int idTargetCurrencyCode = customerRepository.FindByCodeforExchangesRates(TargetCurrencyCode);

        if(idBaseCurrency == -1 && idTargetCurrencyCode == -1){
            resp.getWriter().write("Not found Currency in database !");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }else{
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        String JsonResult = customerRepository.GetExchangeCurrency(amount, idBaseCurrency , idTargetCurrencyCode);
        if(JsonResult == null){
            resp.getWriter().write("Not found Currency");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }else{
            resp.setContentType("application/json");
            String JsonResult1 = customerRepository.GetExchangeCurrency(amount, idBaseCurrency, idTargetCurrencyCode);
            resp.getWriter().write(JsonResult1);
            resp.setStatus(HttpServletResponse.SC_OK);

        }

    }
}
