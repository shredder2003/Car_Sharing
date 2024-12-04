package carsharing;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.*;

@Slf4j
public class CustomerService {
    private final DbCustomerDao dbCustomerDao;
    private final DbCarDao dbCarDao;
    private final DbCompanyDao dbCompanyDao;

    public CustomerService(Connection connection){
        dbCustomerDao = new DbCustomerDao(connection);
        dbCarDao = new DbCarDao(connection);
        dbCompanyDao = new DbCompanyDao(connection);
    }

    public int printAllCustomers(){
        log.info("printAllCustomers(+)");
        List<Customer> customerList = dbCustomerDao.findAll();
        if(customerList==null || customerList.isEmpty()){
            System.out.println("The customer list is empty!");
        }else {
            System.out.println("The customer list:");
            customerList.forEach(customer -> System.out.println(customer.getId() + ". " + customer.getName()));
            System.out.println("0. Back");
        }
        return customerList.size();
    }

    /*public int printCustomersByCar(int carId){
        log.info("printCustomersByCar(+)");
        List<Customer> customerList = dbCustomerDao.findByCar(carId);
        if(customerList==null || customerList.isEmpty()){
            System.out.println("The customer list is empty!");
        }else {
            System.out.println("Customer:");
            customerList.forEach(customer -> System.out.println( (1+customerList.indexOf(customer)) + ". " + customer.getName()));
            //System.out.println("0. Back");
        }
        return customerList.size();
    }*/

    public void printCustomer(int id){
        log.info("printCustomer(+)");
        Customer customer = dbCustomerDao.findById(id);
        System.out.println(
        "'"+customer.getName()+"' customer\n" +
        "1. Customer list\n" +
        "2. Create a customer\n" +
        "0. Back"
        );
    }

    public void createCustomer(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the customer name:");
        String input = scanner.nextLine();
        dbCustomerDao.add( new Customer(null, input, null));
        System.out.println("The customer was added!");
    }

    public void printRentedCar(int customerId){
        log.info("printRentedCar(+) customerId="+customerId);
        Customer customer = dbCustomerDao.findById(customerId);
        //log.info("printRentedCar customer.getRentedCarId()="+customer.getRentedCarId());
        if(customer.getRentedCarId()==0){
            System.out.println("You didn't rent a car!");
        }else {
            Car car = dbCarDao.findById(customer.getRentedCarId());
            Company company = dbCompanyDao.findById(car.getCompanyId());
            System.out.println("Your rented car:\n" +
                    car.getName()+"\n" +
                    "Company:\n" +
                    company.getName());
        }
    }

    private Map<Integer, Integer> printAnyList(List objectList){
        Map<Integer, Integer> integerMap = new HashMap<>();
        int i=0;
        for( Object object: objectList){
            i++;
            switch (object){
                case Company temp -> {
                    integerMap.put(i,temp.getId());
                    System.out.println( i + ". " + temp.getName());
                }
                case Car temp -> {
                    integerMap.put(i,temp.getId());
                    System.out.println( i + ". " + temp.getName());
                }
                default -> throw new IllegalStateException("Unexpected value: " + object);
            }
        }
        System.out.println("0. Back");
        return integerMap;
    }

    public Object rentCarShowCompaniesList(int customerId){
        log.info("rentCarShowCompaniesList(+) customerId="+customerId);
        Customer customer = dbCustomerDao.findById(customerId);
        List<Company> companyList;
        if(customer.getRentedCarId()==0){
            companyList = dbCompanyDao.findAll();
            /*List<Integer> integerList = new ArrayList<>();
            int i=0;
            System.out.println("Choose a company:");
            for( Company company: companyList){
                i++;
                integerList.set(i,company.getId());
                System.out.println( i + ". " + company.getName());
            }
            return integerList;*/
            return printAnyList(companyList);
        }else{
            System.out.println("You've already rented a car!");
            return 0;
        }
    }

    public void returnCar(int customerId){
        log.info("returnCar(+)");
        Customer customer = dbCustomerDao.findById(customerId);
        if(customer.getRentedCarId()==0){
            System.out.println("You didn't rent a car!");
        }else{
            customer.setRentedCarId(null);
            dbCustomerDao.update(customer);
            System.out.println("You've returned a rented car!");
        }
    }

    public Object rentCarShowCarsList(int companyId){
        log.info("rentCarShowCarsList(+)");
        Company company = dbCompanyDao.findById(companyId);
        List<Car> carList = dbCarDao.findAvailableByCompany(companyId);
        if(carList.isEmpty()){
            System.out.println("No available cars in the '"+company.getName()+"' company");
            return 0;
        }else{
            /*List<Integer> integerList = new ArrayList<>();
            int i=0;
            for( Car car: carList){
                i++;
                integerList.set(i,car.getId());
                System.out.println( i + ". " + car.getName());
            }
            return integerList;*/
            return printAnyList(carList);
        }
    }

    public void rentCar(int customerId, int carId){
        log.info("rentCar(+)");
        Customer customer = dbCustomerDao.findById(customerId);
        Car car = dbCarDao.findById(carId);
        customer.setRentedCarId(carId);
        dbCustomerDao.update(customer);
        System.out.println("You rented '"+car.getName()+"'");
    }

}
