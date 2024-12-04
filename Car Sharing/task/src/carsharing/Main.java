package carsharing;

import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class Main {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static String DB_URL = "jdbc:h2:./src/carsharing/db/";

    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    static final String STEP_EXIT = "EXIT";
    static final String STEP_MAIN = "MAIN";
    static final String STEP_MANAGER = "MANAGER";
    static final String STEP_CUSTOMER_LIST = "CUSTOMER_LIST";
    static final String STEP_CUSTOMER_CREATE = "CUSTOMER_CREATE";
    static final String STEP_CUSTOMER_MENU = "STEP_CUSTOMER_CHOOSED";
    static final String STEP_COMPANY_LIST = "COMPANY_LIST";
    static final String STEP_COMPANY_CREATE = "COMPANY_CREATE";
    static final String STEP_COMPANY_CHOOSED = "COMPANY_CHOOSED";
    static final String STEP_CAR_LIST = "CAR_LIST";
    static final String STEP_CAR_CREATE = "CAR_CREATE";
    static final String STEP_CUSTOMER_RETURN_CAR = "STEP_CUSTOMER_RETURN_CAR";
    static final String STEP_CUSTOMER_SHOW_CAR   = "STEP_CUSTOMER_SHOW_CAR";
    static final String STEP_CUSTOMER_RENT_CAR_SHOW_COMPANIES_LIST = "STEP_CUSTOMER_RENT_CAR_SHOW_COMPANIES_LIST";
    static final String STEP_CUSTOMER_RENT_CAR_SHOW_CARS_LIST = "STEP_CUSTOMER_RENT_CAR_SHOW_CARS_LIST";
    static final String STEP_CUSTOMER_RENT_CAR = "STEP_CUSTOMER_RENT_CAR";

    static CompanyService companyService;
    static CarService carService;
    static CustomerService customerService;
    static int companyId = 0;
    static int carId = 0;
    static int customerId = 0;
    static Map<Integer, Integer> idsByPosition;

    private static String processStep(String currentStep){
        log.info("processStep(+) currentStep="+currentStep);
        switch (currentStep) {
            case STEP_MAIN -> System.out.println("1. Log in as a manager\n" +
                    "2. Log in as a customer\n" +
                    "3. Create a customer\n" +
                    "0. Exit");
            case STEP_MANAGER -> System.out.println("1. Company list\n2. Create a company\n0. Back");
            case STEP_COMPANY_LIST -> {
                if (companyService.printAllCompanies() == 0) {
                    currentStep = STEP_MANAGER;
                    return processStep(currentStep);
                }
            }
            case STEP_COMPANY_CREATE -> {
                companyService.createCompany();
                currentStep = STEP_MANAGER;
                return processStep(currentStep);
            }
            case STEP_COMPANY_CHOOSED -> companyService.printCompany(companyId);
            case STEP_CAR_LIST -> {
                carService.printCarsByCompany(companyId);
                //if (carService.printAllCars() == 0) {
                    currentStep = STEP_COMPANY_CHOOSED;
                    return processStep(currentStep);
                //}
            }
            case STEP_CAR_CREATE -> {
                carService.createCar(companyId);
                currentStep = STEP_COMPANY_CHOOSED;
                return processStep(currentStep);
            }
            case STEP_CUSTOMER_CREATE -> {
                customerService.createCustomer();
                currentStep = STEP_MAIN;
                return processStep(currentStep);
            }
            case STEP_CUSTOMER_LIST -> {
                if (customerService.printAllCustomers() == 0) {
                    currentStep = STEP_MAIN;
                    return processStep(currentStep);
                }
            }
            case STEP_CUSTOMER_MENU -> System.out.println("1. Rent a car\n" +
                    "2. Return a rented car\n" +
                    "3. My rented car\n" +
                    "0. Back");
            case STEP_CUSTOMER_RETURN_CAR -> {
                customerService.returnCar(customerId);
                currentStep = STEP_CUSTOMER_MENU;
                return processStep(currentStep);
            }
            case STEP_CUSTOMER_SHOW_CAR -> {
                customerService.printRentedCar(customerId);
                currentStep = STEP_CUSTOMER_MENU;
                return processStep(currentStep);
            }
            case STEP_CUSTOMER_RENT_CAR_SHOW_COMPANIES_LIST -> {
                Object object = customerService.rentCarShowCompaniesList(customerId);
                if(object instanceof Integer){ // 0 - no available companies
                    currentStep = STEP_CUSTOMER_MENU;
                    return processStep(currentStep);
                }else{
                    idsByPosition = (Map<Integer, Integer>) object;
                }
            }
            case STEP_CUSTOMER_RENT_CAR_SHOW_CARS_LIST -> {
                Object object = customerService.rentCarShowCarsList(companyId);
                if(object instanceof Integer){ // 0 - no available cars
                    currentStep = STEP_CUSTOMER_RENT_CAR_SHOW_COMPANIES_LIST;
                    return processStep(currentStep);
                }else{
                    idsByPosition = (Map<Integer, Integer>) object;
                }
            }
            case STEP_CUSTOMER_RENT_CAR -> {
                customerService.rentCar(customerId, carId);
                currentStep = STEP_CUSTOMER_MENU;
                return processStep(currentStep);
            }
        }
        return currentStep;
    }

    private static void initDB(Connection connection) throws SQLException {
        System.out.println("Creating table in given database...");
        connection.setAutoCommit(true);
        try(Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS COMPANY( "
                    + " ID INT PRIMARY KEY AUTO_INCREMENT "
                    + ",NAME varchar_ignorecase(255) UNIQUE NOT NULL"
                    + ")";
            stmt.executeUpdate(sql);
            System.out.println("Created table COMPANY in given database...");
        }
        try(Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS CAR( "
                    + " ID INT PRIMARY KEY AUTO_INCREMENT "
                    + ",NAME varchar_ignorecase(255) UNIQUE NOT NULL"
                    + ",COMPANY_ID INT NOT NULL"
                    + ",CONSTRAINT COMPANY_FK FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID)"
                    + ")";
            stmt.executeUpdate(sql);
            System.out.println("Created table CAR in given database...");
        }
        try(Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS CUSTOMER( "
                    + " ID INT PRIMARY KEY AUTO_INCREMENT "
                    + ",NAME varchar_ignorecase(255) UNIQUE NOT NULL"
                    + ",RENTED_CAR_ID INT"
                    + ",CONSTRAINT CAR_FK FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID)"
                    + ")";
            stmt.executeUpdate(sql);
            System.out.println("Created table CUSTOMER in given database...");
        }
    }

    private static String chooseStep(String currentStep, String input){
        return switch (currentStep) {
            case STEP_MAIN ->
                switch (input) {
                    case "0" -> STEP_EXIT;
                    case "1" -> STEP_MANAGER;
                    case "2" -> STEP_CUSTOMER_LIST;
                    case "3" -> STEP_CUSTOMER_CREATE;
                    default -> currentStep;
                };
            case STEP_MANAGER ->
                switch (input) {
                    case "0" -> STEP_MAIN;
                    case "1" -> STEP_COMPANY_LIST;
                    case "2" -> STEP_COMPANY_CREATE;
                    default -> currentStep;
                };
            case STEP_COMPANY_LIST ->
                switch (input) {
                    case "0" -> STEP_MANAGER;
                    default -> {
                        companyId = Integer.parseInt(input);
                        yield STEP_COMPANY_CHOOSED;
                    }
                };
            case STEP_COMPANY_CHOOSED ->
                switch (input) {
                    case "0" -> STEP_MANAGER;
                    case "1" -> STEP_CAR_LIST;
                    case "2" -> STEP_CAR_CREATE;
                    default -> currentStep;
                };
            case STEP_CUSTOMER_LIST ->
                    switch (input) {
                        case "0" -> STEP_MAIN;
                        default -> {
                            customerId = Integer.parseInt(input);
                            yield STEP_CUSTOMER_MENU;
                        }
                    };
            case STEP_CUSTOMER_MENU ->
                    switch (input) {
                        case "0" -> STEP_MANAGER;
                        case "1" -> STEP_CUSTOMER_RENT_CAR_SHOW_COMPANIES_LIST;
                        case "2" -> STEP_CUSTOMER_RETURN_CAR;
                        case "3" -> STEP_CUSTOMER_SHOW_CAR;
                        default -> currentStep;
                    };
            case STEP_CUSTOMER_RENT_CAR_SHOW_COMPANIES_LIST -> {
                if (input.equals("0")) {
                    yield STEP_CUSTOMER_MENU;
                } else {
                    companyId = idsByPosition.get(Integer.parseInt(input));
                    yield STEP_CUSTOMER_RENT_CAR_SHOW_CARS_LIST;
                }
            }
            case STEP_CUSTOMER_RENT_CAR_SHOW_CARS_LIST -> {
                if(input.equals("0")){
                    yield STEP_CUSTOMER_RENT_CAR_SHOW_COMPANIES_LIST;
                }else {
                    carId = idsByPosition.get(Integer.parseInt(input));
                    yield STEP_CUSTOMER_RENT_CAR;
                }
            }
            default -> currentStep;
        };
    }

    public static void main(String[] args) throws ClassNotFoundException {
        // write your code here
        if(args.length>=2){
            DB_URL += args[1];
        }else{
            DB_URL += "db_name.h2";
        }
        // STEP 1: Register JDBC driver
        Class.forName(JDBC_DRIVER);

        //STEP 2: Open a connection
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DB_URL);
        System.out.println("Connecting to database...");
        try( Connection connection = dataSource.getConnection(); ){
            initDB(connection);
            companyService = new CompanyService(connection);
            carService = new CarService(connection);
            customerService = new CustomerService(connection);
            String input = null;
            Scanner scanner = new Scanner(System.in);
            String currentStep = STEP_MAIN;
            processStep(currentStep);
            Boolean toExit = false;
            do {
                input = scanner.nextLine();
                //тут мы меняем шаг на следующий (выбранный), либо устанавливаем параметры
                log.info("before switching "+currentStep+" input="+input);
                currentStep = chooseStep(currentStep, input);
                /*switch (currentStep) {
                    case STEP_MAIN -> {
                        switch (input) {
                            case "0" -> toExit = true;
                            case "1" -> currentStep = STEP_MANAGER;
                        }
                    }
                    case STEP_MANAGER -> {
                        switch (input) {
                            case "0" -> currentStep = STEP_MAIN;
                            case "1" -> currentStep = STEP_COMPANY_LIST;
                            case "2" -> currentStep = STEP_COMPANY_CREATE;
                        }
                    }
                    case STEP_COMPANY_LIST -> {
                        switch (input) {
                            case "0" -> currentStep = STEP_MANAGER;
                            default -> {
                                currentStep = STEP_COMPANY_CHOOSED;
                                companyId = Integer.parseInt(input);
                            }
                        }
                    }
                    case STEP_COMPANY_CHOOSED -> {
                        switch (input) {
                            case "0" -> currentStep = STEP_MANAGER;
                            case "1" -> currentStep = STEP_CAR_LIST;
                            case "2" -> currentStep = STEP_CAR_CREATE;
                        }
                    }
                }*/
                log.info("switched to "+currentStep);
                //тут мы выполняем действие в зависимости от шага
                currentStep = processStep(currentStep);

            }while(! currentStep.equals(STEP_EXIT));
        }catch(SQLException se) {
            //Handle errors for Class.forName
            se.printStackTrace();
        } //end try
    }
}