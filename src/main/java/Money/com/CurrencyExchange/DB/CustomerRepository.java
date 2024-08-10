package Money.com.CurrencyExchange.DB;

import Money.com.CurrencyExchange.Controller.CustomerValidateCurrency;
import Money.com.CurrencyExchange.Model.ExchangesRatesDTO;
import Money.com.CurrencyExchange.Model.ExchangesRatesModel;
import Money.com.CurrencyExchange.Model.ModelofCurrency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.*;

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
                connection.close();
                System.out.println("Connection to SQLite has been established");
            }else{
                System.out.println("Ops database is unavailable!");
            }
            preparedStatement.execute();
        }catch (SQLException e){
            System.out.println("Connection error: " + e.getMessage());
        }

    }

    public void createTableExchangesRates(){
        String createTableSQl = "CREATE TABLE IF NOT EXIST ExchangeRates (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "BaseCurrencyId INTEGER NOT NULL UNIQUE," +
                "TargetCurrencyId INTEGER NOT NULL UNIQUE," +
                "Rate DECIMAL NOT NULL" +
                ");";

        try(Connection connection = DriverManager.getConnection(DB_URl);
            PreparedStatement preparedStatement = connection.prepareStatement(createTableSQl)) {


            if(connection != null){
                connection.close();
                System.out.println("Connection to SQLite has been established");
            }else{
                System.out.println("Ops database is unavailable!");
            }
            preparedStatement.execute();
        }catch (SQLException e){
            System.out.println("Connection error: " + e.getMessage());
        }

    }
    public void saveExchangesRate (ExchangesRatesDTO exchangesRatesDTO) {
        String insertSQL = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URl);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

                preparedStatement.setInt(1, exchangesRatesDTO.getBaseCurrencyCode());
                preparedStatement.setInt(2, exchangesRatesDTO.getTargetCurrencyCode());
                preparedStatement.setBigDecimal(3, BigDecimal.valueOf(exchangesRatesDTO.getRate()));


            System.out.println("Data inserted successfully");
        } catch (SQLException e) {
            System.out.println("Error saving currency: " + e.getMessage());
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
            preparedStatement.setString(3, String.valueOf('$'));
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
    public String GetByCode(String code)  {
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
            System.out.println("Ops!" + e.getMessage());
        }
        return jsonResult;
    }

    public String FindById(int id1, int id2) {
        String query = "SELECT " +
                "c1.Id AS BaseCurrencyId, " +
                "c1.Code AS BaseCurrencyCode, " +
                "c1.FullName AS BaseCurrencyFullName, " +
                "c1.Sign AS BaseCurrencySign, " +
                "c2.Id AS TargetCurrencyId, " +
                "c2.Code AS TargetCurrencyCode, " +
                "c2.FullName AS TargetCurrencyFullName, " +
                "c2.Sign AS TargetCurrencySign, " +
                "er.Rate " +
                "FROM Currencies c1 " +
                "JOIN ExchangeRates er ON c1.Id = er.BaseCurrencyId " +
                "JOIN Currencies c2 ON c2.Id = er.TargetCurrencyId " +
                "WHERE c1.Id = ? AND c2.Id = ?";

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult = null;

        try (Connection connection = DriverManager.getConnection(DB_URl);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id1);
            preparedStatement.setInt(2, id2);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {

                 ModelofCurrency BaseCurrency = new ModelofCurrency(
                         rs.getString("BaseCurrencyCode"),
                         rs.getString("BaseCurrencyFullName"),
                         rs.getString("BaseCurrencySign")
                );
                 ModelofCurrency TargetCurrencyCode = new ModelofCurrency(
                         rs.getString("TargetCurrencyCode"),
                         rs.getString("TargetCurrencyFullName"),
                         rs.getString("TargetCurrencySign")
                 );

                ExchangesRatesModel exchangesRatesModel = new ExchangesRatesModel(
                        BaseCurrency,
                        TargetCurrencyCode,
                        rs.getDouble("Rate")
                );
                 jsonResult = objectMapper.writeValueAsString(exchangesRatesModel);
            }else{
                jsonResult = "{\"error\":\"Exchange rate not found\"}";

            }

        } catch (JsonProcessingException | SQLException e) {
            System.out.println("Ops!" + e.getMessage());
            jsonResult = "{\"error\":\"Internal server error\"}";
        }

        return jsonResult;
    }
    public int FindByCodeforExchangesRates(String code){
        String query = "SELECT Id FROM Currencies WHERE code = ?";

        try(Connection connection = DriverManager.getConnection(DB_URl);
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
        preparedStatement.setString(1, code);
        ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()){
                return rs.getInt("Id");
            }

        }catch (SQLException e){
            System.out.println("Search for id failed");
        }
        return -1;
    }
    public boolean ExchangesRatesExists(int id, int id1) throws SQLException{
        String query = "SELECT COUNT(*) FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";

        try(Connection connection = DriverManager.getConnection(DB_URl);
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, id1);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getInt(1) > 0;
            }
        }catch (SQLException e){
            System.out.println("Ops!" + e.getMessage());
        }
        return false;
    }
    public Object GetAllCurrencies(){
        String query = "Select * FROM Currencies";
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        try(Connection connection = DriverManager.getConnection(DB_URl);
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                ObjectNode jsonObject = objectMapper.createObjectNode();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    jsonObject.put(columnName, rs.getString(i));
                }
                arrayNode.add(jsonObject);
            }
            return objectMapper.writeValueAsString(arrayNode);

        }catch (SQLException | JsonProcessingException e){
            System.out.println("Ops!" + e.getMessage());
        }
        return null;
    }

    public Object GetAllExchangesRates(){

        String query = "Select" +
                "c1.Id AS BaseCurrencyId, " +
                "c1.Code AS BaseCurrencyCode, " +
                "c1.FullName AS BaseCurrencyFullName, " +
                "c1.Sign AS BaseCurrencySign, " +
                "c2.Id AS TargetCurrencyId, " +
                "c2.Code AS TargetCurrencyCode, " +
                "c2.FullName AS TargetCurrencyFullName, " +
                "c2.Sign AS TargetCurrencySign, " +
                "er.Rate " +
                "From Currencies c1 " +
                "JOIN ExchangeRates er ON c1.Id = er.BaseCurrencyId " +
                "JOIN Currencies c2 ON c2.Id = er.TargetCurrencyId ";

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        try(Connection connection = DriverManager.getConnection(DB_URl);
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

        ResultSet rs = preparedStatement.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                ObjectNode jsonObject = objectMapper.createObjectNode();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    jsonObject.put(columnName, rs.getString(i));
                }
                arrayNode.add(jsonObject);
            }
            return objectMapper.writeValueAsString(arrayNode);
        }catch (SQLException | JsonProcessingException e){
            System.out.println("Ops!" + e.getMessage());
        }

        return null;
    }
    public String update(double rate, int id1, int id2){

        String query = "Update ExchangeRates SET Rate = ? WHERE BaseCurrencyId = ? AND TargetCurrencyId = ? ";
        String error = null;
        try(Connection connection = DriverManager.getConnection(DB_URl);
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setDouble(1, rate);
            preparedStatement.setInt(2, id1);
            preparedStatement.setInt(3, id2);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                error = ("Rate was updated successfully!");
            } else {
                error = ("No records were updated. Please check if the provided IDs exist.");
            }

        }catch (SQLException e){
            error = ("error with database!" + e.getMessage());
        }
        return error;
    }


    public String GetExchangeCurrency(double amount, int id1 , int id2){
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult = null;

        String query = "Select" +
                "c1.Id AS BaseCurrencyId, " +
                "c1.Code AS BaseCurrencyCode, " +
                "c1.FullName AS BaseCurrencyFullName, " +
                "c1.Sign AS BaseCurrencySign, " +
                "c2.Id AS TargetCurrencyId, " +
                "c2.Code AS TargetCurrencyCode, " +
                "c2.FullName AS TargetCurrencyFullName, " +
                "c2.Sign AS TargetCurrencySign, " +
                "er.Rate " +
                "? AS amount, " +
                "? * er.Rate AS convertedAmount " +
                "From Currencies c1 " +
                "JOIN ExchangeRates er ON c1.Id = er.BaseCurrencyId " +
                "JOIN Currencies c2 ON c2.Id = er.TargetCurrencyId " +
                "WHERE c1.Id = ? AND c2.Id = ?";
        try(Connection connection = DriverManager.getConnection(DB_URl);
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setDouble(1, amount);
            preparedStatement.setDouble(2, amount);
            preparedStatement.setInt(3, id1);
            preparedStatement.setInt(4, id2);
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()){
                jsonResult = objectMapper.writeValueAsString(rs);
                return jsonResult;
            }


        }catch (SQLException  | JsonProcessingException e){
            System.out.println(e.getMessage());
        }

        return jsonResult;
    }
}



