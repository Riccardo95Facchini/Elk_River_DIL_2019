package facchini.riccardo.Elk_River_DIL_2019.Reservation_Package;

import java.util.Date;

public class ReservationDatabase extends Reservation
{
    private String employeeUid;
    private String spotUid;
    private String customerUid;
    private String customerName;
    
    public ReservationDatabase(Date date, String type, String serviceUid, String customerUid, String customerName)
    {
        super(date, type);
        
        if (type.equals(SPOT))
            this.spotUid = serviceUid;
        else if (type.equals(RENTAL) || type.equals(INSTRUCTOR))
            this.employeeUid = serviceUid;
        
        this.customerUid = customerUid;
        this.customerName = customerName;
    }
    
    public String getSpotUid() {return spotUid;}
    
    public String getEmployeeUid() {return employeeUid;}
    
    public String getCustomerUid() {return customerUid;}
    
    public String getCustomerName() {return customerName;}
}
