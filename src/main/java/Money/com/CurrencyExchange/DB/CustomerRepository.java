package Money.com.CurrencyExchange.Dao;

import Money.com.CurrencyExchange.Controller.CustomerValidCurrency;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerRepository {

    private static final String DB_URl = "jdbc:sqlite:mydatabase.db";
    public void createTableIfNotExists(){
        // Down we Define sql - request for create table input variable: createTableSQl
        String createTableSQl = "Create TABLE IF NOT EXIST Currencies (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "Code INTEGER NOT NULL," +
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
    public void saveCustomer(CustomerValidCurrency customerValidCurrency) {
        // Down Sql - request which insert information into table later we mention name of столбцов.
        String insertSQL = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URl);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setInt(1, customerValidCurrency.getCode());
            preparedStatement.setString(2, customerValidCurrency.getFullName());
            preparedStatement.setString(3, String.valueOf(customerValidCurrency.getSign()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving currency: " + e.getMessage());
        }
    }
}
