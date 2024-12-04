package carsharing;

import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DbCarDao implements CarDao{
    private final Connection connection;

    @Override
    public List<Car> findAll() {
        List<Car> carList = new ArrayList<>();
        try(Statement statement = connection.createStatement()) {
            String sql = "select id, name, company_id from car "
                    + " order by id "
                    ;
            try (ResultSet cars = statement.executeQuery(sql)) {
                while (cars.next()) {
                    // Retrieve column values
                    int id = cars.getInt("id");
                    String name = cars.getString("name");
                    int companyId = cars.getInt("company_id");
                    carList.add( new Car(id, name, companyId) );
                }
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return carList;
    }

    @Override
    public Car findById(int id) {
        Car car = null;
        String sql = "select id, name, company_id from car "
                + " where id = ? "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet cars = preparedStatement.executeQuery()) {
                while (cars.next()) {
                    // Retrieve column values
                    String name = cars.getString("name");
                    int companyId = cars.getInt("company_id");
                    car = new Car(id, name, companyId);
                }
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return car;
    }

    @Override
    public List<Car> findByCompany(int companyId) {
        List<Car> carList = new ArrayList<>();
        String sql = "select id, name, company_id from car "
                + " where company_id = ? "
                + " order by id "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, companyId);
            try (ResultSet cars = preparedStatement.executeQuery()) {
                while (cars.next()) {
                    // Retrieve column values
                    int id = cars.getInt("id");
                    String name = cars.getString("name");
                    carList.add( new Car(id, name, companyId) );
                }
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return carList;
    }

    @Override
    public void add(Car car) {
        String sql = "insert into car(name, company_id) "
                + " values( ? , ? ) "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, car.getName());
            preparedStatement.setInt(2, car.getCompanyId());
            preparedStatement.executeUpdate();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void update(Car car) {
        String sql = "update car "
                + " set name = ? "
                + " , company_id = ? "
                + " where id = ? "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, car.getName());
            preparedStatement.setInt(2, car.getCompanyId());
            preparedStatement.setInt(3, car.getId());
            preparedStatement.executeUpdate();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "delete from car "
                + " where id = ? "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    public List<Car> findAvailableByCompany(int companyId) {
        List<Car> carList = new ArrayList<>();
        String sql = "select id, name, company_id from car "
                + " where company_id = ? "
                + " and not exists ( select null from customer where customer.rented_car_id = car.id ) "
                + " order by id "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, companyId);
            try (ResultSet cars = preparedStatement.executeQuery()) {
                while (cars.next()) {
                    // Retrieve column values
                    int id = cars.getInt("id");
                    String name = cars.getString("name");
                    carList.add( new Car(id, name, companyId) );
                }
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return carList;
    }

}
