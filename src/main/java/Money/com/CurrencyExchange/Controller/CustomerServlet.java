package Money.com.CurrencyExchange.Controller;

import Money.com.CurrencyExchange.DB.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/currencies")
public class CustomerServlet extends HttpServlet {
    private final CustomerRepository repository = new CustomerRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void doPost(HttpServletRequest request , HttpServletResponse response)
            throws ServletException, IOException {
        CustomerDTO customerDTO = objectMapper.readValue(request.getInputStream(), CustomerDTO.class);
        CustomerValidateCurrency customerValidateCurrency = CustomerMapper.fromDTO(customerDTO);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

           List<String> errors = customerValidateCurrency.validate();

           try{
              if(repository.currencyExists(customerValidateCurrency.getCode())) {
                  errors.add("Currency with the same code or name already exists");
              }
          }catch (SQLException e){
              response.setStatus(HttpServletResponse.SC_CONFLICT);

          }

            if(errors.isEmpty()){
                try{
                    repository.saveCustomer(customerValidateCurrency);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    out.write("{\"message\": \"Customer created successfully.\"}");
                }catch (Exception e){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST );
                out.write("{\"errors\": \"" + String.join(", ", errors) + "\"}");
            }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        //Get list of currencies
        //Handle errors with those codes: 200 successful , 500 error with database;
        //check out return handles



    }

}