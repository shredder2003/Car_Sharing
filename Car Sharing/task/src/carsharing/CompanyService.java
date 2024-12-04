package carsharing;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class CompanyService {
    private final DbCompanyDao dbCompanyDao;

    public CompanyService(Connection connection){
        dbCompanyDao = new DbCompanyDao(connection);
    }

    public int printAllCompanies(){
        log.info("printAllCompanies(+)");
        List<Company> companyList = dbCompanyDao.findAll();
        if( companyList.isEmpty() ){
            System.out.println("The company list is empty!");
        }else{
            System.out.println("Choose the company:");
            companyList.forEach(company -> System.out.println(company.getId()+". "+company.getName()) );
            System.out.println("0. Back");
        }
        return companyList.size();
    }

    public void printCompany(int id){
        log.info("printCompany(+) id="+id);
        Company company = dbCompanyDao.findById(id);
        System.out.println(
        "'"+company.getName()+"' company\n" +
        "1. Car list\n" +
        "2. Create a car\n" +
        "0. Back"
        );
    }

    public void createCompany(){
        log.info("createCompany(+)");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the company name:");
        String input = scanner.nextLine();
        dbCompanyDao.add( new Company(null, input));
        System.out.println("The company was created!");
    }

}
