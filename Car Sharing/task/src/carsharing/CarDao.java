package carsharing;

import java.util.List;

public interface CarDao {
    List<Car> findAll();
    Car findById(int id);
    List<Car> findByCompany(int companyId);
    void add(Car car);
    void update(Car car);
    void deleteById(int id);
}
