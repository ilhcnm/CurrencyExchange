package Money.com.CurrencyExchange.DB;

import Money.com.CurrencyExchange.Controller.CustomerValidateCurrency;
import Money.com.CurrencyExchange.Model.ExchangesRatesDTO;
import Money.com.CurrencyExchange.Model.ExchangesRatesModel;
import Money.com.CurrencyExchange.Model.ModelofCurrency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.*;

import lombok.Getter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class CustomerRepository {
    @Getter
    private static final String DB_URl = "jdbc:sqlite:/home/ilhcmm/Загрузки/MydaTaBase";
    private static final String DRIVER = "org.sqlite.JDBC";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DB_URl);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    public void createTableIfNotExists(){
        String createTableSQl = "CREATE TABLE IF NOT EXISTS Currencies (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "Code TEXT NOT NULL UNIQUE," +
                                "FullName TEXT NOT NULL," +
                                "Sign TEXT NOT NULL" +
                                ");";

        try(Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(createTableSQl)) {
            preparedStatement.execute();
        }catch (SQLException e){
            System.out.println("Connection error: " + e.getMessage());
        }

    }

    public void createTableExchangesRates(){
        String createTableSQL = "CREATE TABLE IF NOT EXISTS ExchangeRates (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "BaseCurrencyId INTEGER NOT NULL," +
                "TargetCurrencyId INTEGER NOT NULL," +
                "Rate DECIMAL NOT NULL," +
                "UNIQUE (BaseCurrencyId, TargetCurrencyId)" +
                ");";


        try(Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.execute();
        }catch (SQLException e){
            System.out.println("Connection error: " + e.getMessage());
        }

    }
    public void saveExchangesRate (ExchangesRatesDTO exchangesRatesDTO) {
        String insertSQL = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

                preparedStatement.setInt(1, exchangesRatesDTO.getBaseCurrencyCode());
                preparedStatement.setInt(2, exchangesRatesDTO.getTargetCurrencyCode());
                preparedStatement.setBigDecimal(3, BigDecimal.valueOf(exchangesRatesDTO.getRate()));
                preparedStatement.executeUpdate();

            System.out.println("Data inserted successfully");
        } catch (SQLException e) {
            System.out.println("Error saving currency: " + e.getMessage());
        }
    }



    public void saveCustomer(CustomerValidateCurrency customerValidateCurrency) {
        String insertSQL = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            // Вставка пользовательских данных
            preparedStatement.setString(1, customerValidateCurrency.getCode());
            preparedStatement.setString(2, customerValidateCurrency.getFullName());
            preparedStatement.setString(3, String.valueOf(customerValidateCurrency.getSign()));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error saving currency: " + e.getMessage());
        }
    }




    public boolean currencyExists(String code) throws SQLException{
        String query = "SELECT COUNT(*) FROM Currencies WHERE code = ?";

        try(Connection connection = connect();
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

        try (Connection connection = connect();
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
        String jsonResult;

        try (Connection connection = connect();
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

        try(Connection connection = connect();
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

        try(Connection connection = connect();
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
    public String getAllCurrencies() {
        String query = "SELECT * FROM Currencies";
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jsonArray = mapper.createArrayNode();

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                ObjectNode jsonObject = mapper.createObjectNode();
                jsonObject.put("Code", resultSet.getString("Code"));
                jsonObject.put("FullName", resultSet.getString("FullName"));
                jsonObject.put("Sign", resultSet.getString("Sign"));
                jsonArray.add(jsonObject);
            }

        } catch (SQLException e) {
            return null;
        }

        return jsonArray.toString();
    }




    public Object GetAllExchangesRates(){

        String query = "Select " +
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

        try(Connection connection = connect();
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
        String error;
        try(Connection connection = connect();
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

        try(Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, id1);
            preparedStatement.setInt(2, id2);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                double rate = rs.getDouble("Rate");
                double convertedAmount = amount * rate;

                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("BaseCurrencyId", rs.getInt("BaseCurrencyId"));
                resultMap.put("BaseCurrencyCode", rs.getString("BaseCurrencyCode"));
                resultMap.put("BaseCurrencyFullName", rs.getString("BaseCurrencyFullName"));
                resultMap.put("BaseCurrencySign", rs.getString("BaseCurrencySign"));
                resultMap.put("TargetCurrencyId", rs.getInt("TargetCurrencyId"));
                resultMap.put("TargetCurrencyCode", rs.getString("TargetCurrencyCode"));
                resultMap.put("TargetCurrencyFullName", rs.getString("TargetCurrencyFullName"));
                resultMap.put("TargetCurrencySign", rs.getString("TargetCurrencySign"));
                resultMap.put("Rate", rs.getDouble("rate"));
                resultMap.put("ConvertedAmount", convertedAmount);

                jsonResult = objectMapper.writeValueAsString(resultMap);
            }

        }catch (SQLException  | JsonProcessingException e){
            return null;
        }

        return jsonResult;
    }
}



