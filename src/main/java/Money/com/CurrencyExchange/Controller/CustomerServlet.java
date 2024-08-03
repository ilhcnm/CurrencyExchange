package Money.com.CurrencyExchange.Controller;

import Money.com.CurrencyExchange.DB.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.security.DeclareRoles;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/currencies")
public class CustomerServlet extends HttpServlet {
    private final CustomerRepository repository = new CustomerRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void doPost(HttpServletRequest request , HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        CustomerDTO customerDTO = objectMapper.readValue(request.getInputStream(), CustomerDTO.class);
        CustomerValidateCurrency customerValidateCurrency = CustomerMapper.fromDTO(customerDTO);

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String DB_URl = CustomerRepository.getDB_URl();
        String selectSQL = "SELECT * FROM Currencies";
        try(Connection connection = DriverManager.getConnection(DB_URl);
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)){

            ResultSet rs = preparedStatement.executeQuery();
            int columnCount = rs.getMetaData().getColumnCount();

           JsonArray jsonArray = new JsonArray();
            try{
               while(rs.next()){
                   JsonObject jsonObject = new JsonObject();
                   for (int i = 1; i <= columnCount; i++) {
                       String columnName = rs.getMetaData().getColumnName(i);
                       String columnValue = rs.getString(i);
                       jsonObject.addProperty(columnName , columnValue);
                   }
                   jsonArray.add(jsonObject);
               }
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(jsonArray.toString());
            }catch (SQLException e){
              response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                System.out.println("Cannot select table! " + e.getMessage());
            }
        }catch (SQLException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println("Database is not available! " + e.getMessage());
        }

        //ResultSet = saving information from request.
        // executeQuery it is relation handle request good work for those which return result type:date

    }

}