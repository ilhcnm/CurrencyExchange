package Money.com.CurrencyExchange.Controller;

import Money.com.CurrencyExchange.DB.CustomerRepository;
import Money.com.CurrencyExchange.Model.CustomerDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import java.util.List;

@WebServlet("/currencies")
public class CustomerServlet extends HttpServlet {
    private final CustomerRepository repository = new CustomerRepository();
    @Override
    protected void doPost(HttpServletRequest request , HttpServletResponse response)
            throws ServletException, IOException {
            super.doPost(request, response);

        PrintWriter out = response.getWriter();

        CustomerDTO customerDTO = new CustomerDTO(request.getParameter("Code") , request.getParameter("FullName") , request.getParameter("Sign").charAt(0));
        CustomerValidateCurrency customerValidateCurrency = CustomerMapper.fromDTO(customerDTO);

           List<String> errors = customerValidateCurrency.validate();

            if(errors.isEmpty()){
                try{
                    repository.createTableIfNotExists();
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

            try{
                if(!repository.currencyExists(customerValidateCurrency.getCode())) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                }else{
                    String resultJson = repository.GetByCode(customerValidateCurrency.getCode());
                    response.setContentType("application/json");
                    response.getWriter().write(resultJson);
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }catch (SQLException e){
                    out.write("Database cannot answer !");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
    }



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
            super.doGet(request, response);
        //A bit more clean code!
        //A bit more clean code!
        //A bit more clean code!

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
                response.getWriter().write("Cannot select table! " + e.getMessage());
            }
        } catch (SQLException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Database is not available! " + e.getMessage());
            }



    }


}
