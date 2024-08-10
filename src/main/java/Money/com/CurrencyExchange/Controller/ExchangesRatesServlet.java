package Money.com.CurrencyExchange.Controller;

import Money.com.CurrencyExchange.DB.CustomerRepository;
import Money.com.CurrencyExchange.Model.ExchangesRatesDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/exchangeRates")
public class ExchangesRatesServlet extends HttpServlet {
    CustomerRepository customerRepository = new CustomerRepository();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        String requestBaseCode = request.getParameter("baseCurrencyCode");
        String requestTargetCode = request.getParameter("targetCurrencyCode");
        double requestRate = Double.parseDouble(request.getParameter("rate"));

        int idforBaseCode = customerRepository.FindByCodeforExchangesRates(requestBaseCode);
        int idforTargetCode = customerRepository.FindByCodeforExchangesRates(requestTargetCode);


        ExchangesRatesDTO exchangesRatesDTO = new ExchangesRatesDTO(idforBaseCode, idforTargetCode, requestRate);


        if(!(requestBaseCode == null && requestTargetCode == null)){
            out.write("Good!");
            response.setStatus(HttpServletResponse.SC_OK);
        }else{
            out.write("Statement is missing");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("Good!");
            response.setStatus(HttpServletResponse.SC_OK);
        }if(idforBaseCode == -1 && idforTargetCode == -1){
            out.write("Not found Currency in database !");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }else if(!(requestRate == 0.0)){
            customerRepository.createTableExchangesRates();
            customerRepository.saveExchangesRate(exchangesRatesDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);

        }else{
            out.write("Database is unavailable");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        try {
            if(customerRepository.ExchangesRatesExists(idforBaseCode , idforTargetCode )){
                out.write("This code's capable already exist");
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }else {
               String ResultJson = customerRepository.FindById(idforBaseCode , idforTargetCode);
               response.setContentType("application/json");
               response.getWriter().write(ResultJson);
               response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (SQLException e) {
            out.write("Ops! Something went wrong with database");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if(customerRepository.GetAllExchangesRates() == null){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Database is unavailable, for ExchangesRates ! ");
        }else{
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            Object ResultJson = customerRepository.GetAllExchangesRates();
            resp.getWriter().write(ResultJson.toString());
        }


    }
}
