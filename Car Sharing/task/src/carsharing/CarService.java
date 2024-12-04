package carsharing;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class CarService {
    private final DbCarDao dbCarDao;

    public CarService(Connection connection){
        dbCarDao = new DbCarDao(connection);
    }

    public int printAllCars(){
        log.info("printAllCars(+)");
        List<Car> carList = dbCarDao.findAll();
        if(carList==null || carList.isEmpty()){
            System.out.println("The car list is empty!");
        }else {
            System.out.println("Car:");
            carList.forEach(car -> System.out.println(car.getId() + ". " + car.getName()));
            //System.out.println("0. Back");
        }
        return carList.size();
    }

    public int printCarsByCompany(int companyId){
        log.info("printCarsByCompany+)");
        List<Car> carList = dbCarDao.findByCompany(companyId);
        if(carList==null || carList.isEmpty()){
            System.out.println("The car list is empty!");
        }else {
            System.out.println("Car:");

            carList.forEach(car -> System.out.println( (1+carList.indexOf(car))/* + car.getId()*/ + ". " + car.getName()));
            //System.out.println("0. Back");
        }
        return carList.size();
    }

    public void printCar(int id){
        log.info("printCar(+)");
        Car car = dbCarDao.findById(id);
        System.out.println(
        "'"+car.getName()+"' company\n" +
        "1. Car list\n" +
        "2. Create a car\n" +
        "0. Back"
        );
    }

    public void createCar(int companyId){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the car name:");
        String input = scanner.nextLine();
        dbCarDao.add( new Car(null, input, companyId));
        System.out.println("The car was added!");
    }

}
