package facchini.riccardo.Elk_River_DIL_2019.Reservation_Package;

import java.util.Date;

import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;

public class Reservation_Customer_Home extends Reservation
{
    private Employee employee;
    private String resUid;
    
    public Reservation_Customer_Home(Date date, Employee employee, String resUid)
    {
        super(date);
        this.employee = employee;
        this.resUid = resUid;
    }
    
    public Employee getEmployee() {return employee;}
    
    public String getResUid() {return resUid;}
}
