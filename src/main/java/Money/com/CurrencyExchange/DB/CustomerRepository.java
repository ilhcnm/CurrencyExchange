package Money.com.CurrencyExchange.DB;

import Money.com.CurrencyExchange.Controller.CustomerValidateCurrency;
import Money.com.CurrencyExchange.Dao.ModelofCurrency;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class CustomerRepository {
    @Getter
    private static final String DB_URl = "jdbc:sqlite:mydatabase.db";




    public void createTableIfNotExists(){
        String createTableSQl = "CREATE TABLE IF NOT EXIST Currencies (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "Code TEXT NOT NULL UNIQUE," +
                                "FullName TEXT NOT NULL," +
                                "Sign TEXT NOT NULL" +
                                ");";

        try(Connection connection = DriverManager.getConnection(DB_URl);
            PreparedStatement preparedStatement = connection.prepareStatement(createTableSQl)) {


            if(connection != null){
                System.out.println("Connection to SQLite has been established");
            }else{
                System.out.println("Ops database is unavailable!");
            }
            preparedStatement.execute();
        }catch (SQLException e){
            System.out.println("Connection error: " + e.getMessage());
        }

    }
    public void saveCustomer(CustomerValidateCurrency customerValidateCurrency) {
        String insertSQL = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URl);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, customerValidateCurrency.getCode());
            preparedStatement.setString(2, customerValidateCurrency.getFullName());
            preparedStatement.setString(3, String.valueOf(customerValidateCurrency.getSign()));
            preparedStatement.executeUpdate();

            preparedStatement.setString(1, "USD");
            preparedStatement.setString(2, "Dollar");
            preparedStatement.setString(3, "$");
            preparedStatement.executeUpdate();

            System.out.println("Data inserted successfully");
        } catch (SQLException e) {
            System.out.println("Error saving currency: " + e.getMessage());
        }
    }

    public boolean currencyExists(String code) throws SQLException{
        String query = "SELECT COUNT(*) FROM Currencies WHERE code = ?";

        try(Connection connection = DriverManager.getConnection(DB_URl);
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, code);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getInt(1) > 0;
            }
        }catch (SQLException e){
            System.out.println("Ops!" + e.getMessage());
        }
        return false;
    }
    public String FindbyCode(String code) {
        String query = "SELECT Code, FullName, Sign FROM Currencies WHERE code = ?";
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult = null;

        try (Connection connection = DriverManager.getConnection(DB_URl);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, code);
            ResultSet rs = preparedStatement.executeQuery();


            if (rs.next()) {

               ModelofCurrency modelofCurrency = new ModelofCurrency(
                        rs.getString("Code"),
                        rs.getString("FullName"),
                        rs.getString("Sign")
                );
                jsonResult = objectMapper.writeValueAsString(modelofCurrency);


            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonResult ;

    }
}



