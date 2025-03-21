package com.example.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.sqlite.Function;
import com.example.constants.Constants;
import com.example.records.Worker;
import com.example.utils.Logger;
import com.example.utils.RandomDataGenerator;

public class WorkersTable {
    private final String CREATE_DATABASE_QUERY = "create table if not exists workers " + //
                "(full_name TEXT check(full_name regexp '^[A-Z][a-z]+\\s[A-Z][a-z]+\\s[A-Z][a-z]+$'), " + //
                "sex TEXT check(sex in ('male', 'female')) default 'female', " + //
                "birth_date TEXT check(birth_date IS date(birth_date)));";
    private final String CREATE_INDEX_QUERY = "create index if not exists workers_index on workers (sex, substr(full_name, 1, 1));";
    private final String INSERT_QUERY = "insert into workers values (?, ?, ?);";
    private final String SELECT_UNIQUE_QUERY = "select * from workers group by full_name, birth_date having count(*) = 1 order by full_name;";
    private final String SELECT_MALES_QUERY_LIMITED = "select * from workers where sex = 'male' and full_name like 'F%' limit 100;";
    private final String SELECT_MALES_QUERY = "select * from workers where sex = 'male' and full_name like 'F%';";
    private final String SELECT_MALES_QUERY_OPTIMIZED = "select * from workers where sex = 'male' and substr(full_name, 1, 1) = 'F';";
    private final String UPDATE_QUERY = "update workers set full_name = ?, birth_date = ?, sex = ? where full_name = ? and birth_date = ? and sex = ?;";
    private final String BEGIN_TRANSACTION_QUERY = "BEGIN;";
    private final String END_TRANSACTION_QUERY = "COMMIT;";
    private final String JDBC_CONNECTION_PATH = String.format("jdbc:sqlite:%s/task.db", Constants.DATABASE_FOLDER);
    private Connection connection;

    public WorkersTable() {
        try {
            this.connection = DriverManager.getConnection(JDBC_CONNECTION_PATH);
            Function.create(connection, "regexp", new Function() {
                @Override
                protected void xFunc() throws SQLException {
                    String expression = value_text(0);
                    String value = value_text(1);
                    if (value == null)
                        value = "";

                    Pattern pattern=Pattern.compile(expression);
                    result(pattern.matcher(value).find() ? 1 : 0);
                }
            });
        }
        catch (SQLException e) {
            System.out.println("An error occurred when trying to establish a database connection. Check the error logs for more details.");
            Logger.getInstance().error(e.getMessage());
        }
    }

    private void execute(String query, Object... args) {
        try
        (
            PreparedStatement statement = this.connection.prepareStatement(query);
        ) {
            int currentIndex = 1;

            for (Object obj : args) {
                statement.setObject(currentIndex, obj);
                currentIndex++;
            }
            statement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("An error occurred when executing a query. Check the error logs for more details.");
            Logger.getInstance().error(e.getMessage());
        }
    }

    private List<Worker> select(String query, Object... args) {
        List<Worker> workers = new ArrayList<Worker>();

        try
        (
            PreparedStatement statement = this.connection.prepareStatement(query);
        ) {
            int currentIndex = 1;

            for (Object obj : args) {
                statement.setObject(currentIndex, obj);
                currentIndex++;
            }

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String fullName = result.getString("full_name");
                String birthDate = result.getString("birth_date");
                String sex = result.getString("sex");
                workers.add(new Worker(fullName, birthDate, sex));
            }
        }
        catch (SQLException e) {
            System.out.println("An error occurred when executing the select query. Check the error logs for more details.");
            Logger.getInstance().error(e.getMessage());
        }

        return workers;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        }
        catch (SQLException e) {
            System.out.println("An error occurred when trying to close database connection. Check the error logs for more details.");
            Logger.getInstance().error(e.getMessage());
        }
    }
    
    public void createDatabase() {
        this.execute(CREATE_DATABASE_QUERY);
    }

    public void insertIntoDatabase(Worker record) {
        this.execute(INSERT_QUERY, record.fullName(), record.sex(), record.dateOfBirth());
    }

    public void showUniqueWorkers() {
        System.out.println("|\tFull name\t|\tDate of birth\t|\tSex\t|\tAge\t|");

        for (Worker worker : this.select(SELECT_UNIQUE_QUERY)) {
            worker.print();
        }
    }

    public void beginTransaction() {
        this.execute(BEGIN_TRANSACTION_QUERY);
    }

    public void endTransaction() {
        this.execute(END_TRANSACTION_QUERY);
    }

    public void updateRows() {
        System.out.println("\n|\tFull name\t|\tDate of birth\t|\tSex\t|\tAge\t|");
        for (Worker worker : this.select(SELECT_MALES_QUERY_LIMITED)) {
            Worker newWorker = RandomDataGenerator.generateRandomWorker();
            worker.print();

            this.execute(UPDATE_QUERY, newWorker.fullName(), newWorker.dateOfBirth(), newWorker.sex(), worker.fullName(), worker.dateOfBirth(), worker.sex());
        }
    }

    public void selectAllMales(boolean optimize) {
        if (optimize) {
            this.execute(CREATE_INDEX_QUERY);
        }

        long startTime = System.nanoTime();

        List<Worker> workers = !optimize ? this.select(SELECT_MALES_QUERY) : this.select(SELECT_MALES_QUERY_OPTIMIZED);

        long executionTime = (System.nanoTime() - startTime) / 1000000;

        System.out.println("|\tFull name\t|\tDate of birth\t|\tSex\t|\tAge\t|");
        for (Worker worker : workers) {
            worker.print();
        }

        System.out.println("Total records found: " + workers.size());
        System.out.println("Execution time: " + executionTime + " ms");
    }

    public void insertMultipleValues(List<Worker> workers) {
        for (Worker worker : workers) {
            this.execute(INSERT_QUERY, worker.fullName(), worker.sex(), worker.dateOfBirth());
        }
    }
}
