package carsharing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
    private Integer id;
    private String name;
    private Integer rentedCarId;
}
