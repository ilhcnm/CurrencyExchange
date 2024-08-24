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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/currencies")
public class CustomerServlet extends HttpServlet {
    private final CustomerRepository repository = new CustomerRepository();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Получение данных от клиента
        CustomerDTO customerDTO = new CustomerDTO(
                request.getParameter("Code"),
                request.getParameter("FullName"),
                request.getParameter("Sign").charAt(0)
        );

        // Валидация данных
        CustomerValidateCurrency customerValidateCurrency = CustomerMapper.fromDTO(customerDTO);
        List<String> errors = customerValidateCurrency.validate();

        if (errors.isEmpty()) {
            try {
                // Создание таблицы, если она не существует
                repository.createTableIfNotExists();

                // Проверка на существование валюты
                if (repository.currencyExists(customerValidateCurrency.getCode())) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.write("{\"error\": \"Currency already exists.\"}");
                } else {
                    // Сохранение валюты в базу данных
                    repository.saveCustomer(customerValidateCurrency);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    out.write("{\"message\": \"Currency created successfully.\"}");
                }

            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"error\": \"Database error occurred.\"}");
            }
        } else {
            // Возвращение ошибок валидации
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"errors\": \"" + String.join(", ", errors) + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        String jsonResult = repository.getAllCurrencies();

        if (jsonResult != null && !jsonResult.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResult);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database is unavailable.\"}");
        }
    }
}
