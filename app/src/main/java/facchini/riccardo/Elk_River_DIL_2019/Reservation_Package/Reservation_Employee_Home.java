package facchini.riccardo.Elk_River_DIL_2019.Reservation_Package;

import java.util.Date;

import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Customer;
import facchini.riccardo.Elk_River_DIL_2019.Reservation_Package.Reservation;

public class Reservation_Employee_Home extends Reservation
{
    private Customer customer;
    
    public Reservation_Employee_Home(Date date, Customer customer)
    {
        super(date);
        this.customer = customer;
    }
    
    public Customer getCustomer() {return customer;}
    
    public String getInfo()
    {
        return String.format("%s    at: %s\n%s", date.toString(), date.toString(), customer.displayInfo());
    }
}
