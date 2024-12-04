package carsharing;

import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DbCustomerDao implements CustomerDao{
    private final Connection connection;

    @Override
    public List<Customer> findAll() {
        List<Customer> customerList = new ArrayList<>();
        try(Statement statement = connection.createStatement()) {
            String sql = "select id, name, rented_car_id from customer "
                    + " order by id "
                    ;
            try (ResultSet customers = statement.executeQuery(sql)) {
                while (customers.next()) {
                    // Retrieve column values
                    int id = customers.getInt("id");
                    String name = customers.getString("name");
                    int rentedCarId = customers.getInt("rented_car_id");
                    customerList.add( new Customer(id, name, rentedCarId) );
                }
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return customerList;
    }

    @Override
    public Customer findById(int id) {
        Customer customer = null;
        String sql = "select id, name, rented_car_id from customer "
                + " where id = ? "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet customers = preparedStatement.executeQuery()) {
                while (customers.next()) {
                    // Retrieve column values
                    String name = customers.getString("name");
                    int rentedCarId = customers.getInt("rented_car_id");
                    customer = new Customer(id, name, rentedCarId);
                }
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return customer;
    }

    @Override
    public List<Customer> findByCar(int carId) {
        List<Customer> customerList = new ArrayList<>();
        String sql = "select id, name, company_id from customer "
                + " where rented_car_id = ? "
                + " order by id "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, carId);
            try (ResultSet cars = preparedStatement.executeQuery()) {
                while (cars.next()) {
                    // Retrieve column values
                    int id = cars.getInt("id");
                    String name = cars.getString("name");
                    customerList.add( new Customer(id, name, carId) );
                }
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return customerList;
    }

    @Override
    public void add(Customer customer) {
        String sql = "insert into customer(name, rented_car_id) "
                + " values( ? , ? ) "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, customer.getName());
            if(customer.getRentedCarId()==null)
                 preparedStatement.setNull(2, java.sql.Types.INTEGER);
            else preparedStatement.setInt(2, customer.getRentedCarId());
            preparedStatement.executeUpdate();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void update(Customer customer) {
        String sql = "update customer "
                + " set name = ? "
                + " , rented_car_id = ? "
                + " where id = ? "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, customer.getName());
            if(customer.getRentedCarId()==null)
                 preparedStatement.setNull(2, Types.INTEGER);
            else preparedStatement.setInt(2, customer.getRentedCarId());
            preparedStatement.setInt(3, customer.getId());
            preparedStatement.executeUpdate();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "delete from customer "
                + " where id = ? "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }
}
