package carsharing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Car {
    private Integer id;
    private String name;
    private int companyId;
}
