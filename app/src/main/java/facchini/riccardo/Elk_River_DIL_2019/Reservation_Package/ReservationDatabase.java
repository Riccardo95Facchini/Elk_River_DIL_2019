package facchini.riccardo.Elk_River_DIL_2019.Reservation_Package;

import java.util.Date;

public class ReservationDatabase
{
    
    private String employeeUid;
    private String spotUid;
    private String customerUid;
    private String customerName;
    private Date time;
    
    public ReservationDatabase(String serviceUid, String customerUid, String customerName, Date time, boolean isSpot)
    {
        if (isSpot)
            this.spotUid = serviceUid;
        else
            this.employeeUid = serviceUid;
        this.customerUid = customerUid;
        this.customerName = customerName;
        this.time = time;
    }
    
    public String getSpotUid() {return spotUid;}
    
    public String getEmployeeUid() {return employeeUid;}
    
    public String getCustomerUid() {return customerUid;}
    
    public String getCustomerName() {return customerName;}
    
    public Date getTime() {return time;}
}
