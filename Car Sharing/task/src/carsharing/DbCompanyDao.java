package carsharing;

import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DbCompanyDao implements CompanyDao{
    private final Connection connection;

    @Override
    public List<Company> findAll() {
        List<Company> companyList = new ArrayList<>();
        try(Statement statement = connection.createStatement()) {
            String sql = "select id, name from COMPANY "
                    + " order by id "
                    ;
            try (ResultSet companies = statement.executeQuery(sql)) {
                while (companies.next()) {
                    // Retrieve column values
                    int id = companies.getInt("id");
                    String name = companies.getString("name");
                    companyList.add( new Company(id, name) );
                }
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return companyList;
    }

    @Override
    public Company findById(int id) {
        Company company = null;
        String sql = "select id, name from COMPANY "
                + " where id = ? "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet companies = preparedStatement.executeQuery()) {
                while (companies.next()) {
                    // Retrieve column values
                    String name = companies.getString("name");
                    company = new Company(id, name);
                }
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return company;
    }

    @Override
    public void add(Company company) {
        String sql = "insert into COMPANY(name) "
                + " values( ? ) "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, company.getName());
            preparedStatement.executeUpdate();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void update(Company company) {
        String sql = "update COMPANY "
                + " set name = ? "
                + " where id = ? "
                ;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, company.getName());
            preparedStatement.setInt(2, company.getId());
            preparedStatement.executeUpdate();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "delete from COMPANY "
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
