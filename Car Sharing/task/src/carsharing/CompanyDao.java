package carsharing;

import java.util.List;

public interface CompanyDao {
    List<Company> findAll();
    Company findById(int id);
    void add(Company company);
    void update(Company company);
    void deleteById(int id);
}
