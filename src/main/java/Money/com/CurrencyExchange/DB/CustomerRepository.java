package Money.com.CurrencyExchange.DB;

import Money.com.CurrencyExchange.Controller.CustomerValidateCurrency;
import lombok.Getter;

import java.sql.*;

public class CustomerRepository {
    @Getter
    private static final String DB_URl = "jdbc:sqlite:mydatabase.db";




    public void createTableIfNotExists(){
        // Down we Define sql - request for create table input variable: createTableSQl
        String createTableSQl = "CREATE TABLE IF NOT EXIST Currencies (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "Code TEXT NOT NULL UNIQUE," +
                                "FullName TEXT NOT NULL," +
                                "Sign TEXT NOT NULL" +
                                ");";

        try(Connection connection = DriverManager.getConnection(DB_URl);
            PreparedStatement preparedStatement = connection.prepareStatement(createTableSQl)) {
            //PreparedStatement = it is the tool which help us for incapsulation a request

            if(connection != null){
                System.out.println("Connection to SQLite has been established");
            }
            preparedStatement.execute(); // Выполнения SQL - request / create the table
        }catch (SQLException e){
            System.out.println("Connection error: " + e.getMessage());
        }

    }
    //Methods which has opportunity for save our date in a table
    public void saveCustomer(CustomerValidateCurrency customerValidateCurrency) {
        // Down Sql - request which insert information into table later we mention name of столбцов.
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
        String query = "SELECT COUNT(*) FROM customers WHERE code = ?";

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

}
