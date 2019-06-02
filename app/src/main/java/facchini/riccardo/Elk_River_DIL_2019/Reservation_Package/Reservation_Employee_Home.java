package facchini.riccardo.Elk_River_DIL_2019.Reservation_Package;

import java.util.Date;

import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Customer;

public class Reservation_Employee_Home extends Reservation
{
    private Customer customer;
    
    public Reservation_Employee_Home(Date date, String type, Customer customer)
    {
        super(date, type);
        this.customer = customer;
    }
    
    public Customer getCustomer() {return customer;}
    
    public String getInfo()
    {
        return String.format("%s    at: %s\n%s", time.toString(), time.toString(), customer.displayInfo());
    }
}
