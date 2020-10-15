package ru.stc;

import java.sql.*;
import java.util.Arrays;
import java.util.Random;

public class JDBCDemo {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        //регистрация драйвера (без него тоже будет работать, но в программе мб обращения к разным СУБД и это как подсказка)
        Class.forName("org.postgresql.Driver"); //инициализация нового класса (static метод), динамическая подгрузка

        //инициализация переменных (обычно делается в property файле)
        String url = "jdbc:postgresql://localhost:5433/postgres";
        String user = "postgres";
        String pass = "12345";

        //подключение
        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            //создание таблицы
            createTable(connection);

            int randomId = new Random().nextInt();
            //запись в таблицу
            doInsert(connection, randomId);

            //чтение из таблицы
            printData(connection);
        }
        System.out.println("done!");
    }

    /**
     * создание таблицы
     *
     * @param connection
     * @throws SQLException
     */
    private static void createTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS test(id INT PRIMARY KEY);");
            //execute return true/false
        }
    }

    /**
     * запись в таблицу
     *
     * @param connection
     * @param randomId
     * @throws SQLException
     */
    private static void doInsert(Connection connection, int randomId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO test VALUES (?)"); //Prepared против sql injection
        statement.setInt(1, randomId); //записи начинаются с 0
        statement.executeUpdate(); //нужно обновить, записать statement
        //executeUpdate return int
    }

    /**
     * чтение данных из БД и их печать на экране
     *
     * @param connection
     */
    private static void printData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM test")) {
            //executeQuery return resultSet
            while (resultSet.next()) { //true если есть запись
                System.out.println(resultSet.getInt("id")); //как setInt в preparedStatement
            }

            //"пачка" запросов
            statement.addBatch("CREATE TABLE IF NOT EXISTS t3(t INT PRIMARY KEY )");
            statement.addBatch("CREATE TABLE IF NOT EXISTS t2(t INT PRIMARY KEY )");
            int[] ints = statement.executeBatch();
            //System.out.println(Arrays.toString(ints)); //or
            //Arrays.stream(ints).forEach(value -> System.out.println(value)); //or
            Arrays.stream(ints).forEach(System.out::println);
        }
    }
}
