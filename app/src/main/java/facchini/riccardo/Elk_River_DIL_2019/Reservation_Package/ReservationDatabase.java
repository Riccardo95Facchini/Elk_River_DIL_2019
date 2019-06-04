package facchini.riccardo.Elk_River_DIL_2019.Reservation_Package;

import java.util.Date;

public class ReservationDatabase extends Reservation
{
    private String employeeUid;
    private String spotUid;
    private String customerUid;
    private String customerName;
    private String customerPic;
    private String employeePic;
    
    
    public ReservationDatabase(Date time, String type, String serviceUid, String customerUid, String customerName, String customerPic, String employeePic)
    {
        super(time, type);
        
        if (type.equals(SPOT))
            this.spotUid = serviceUid;
        else if (type.equals(RENTAL) || type.equals(INSTRUCTOR))
        {
            this.employeeUid = serviceUid;
            this.employeePic = employeePic;
        }
        
        this.customerPic = customerPic;
        this.customerUid = customerUid;
        this.customerName = customerName;
    }
    
    public String getCustomerPic() {return customerPic;}
    
    public String getEmployeePic() {return employeePic;}
    
    public String getSpotUid() {return spotUid;}
    
    public String getEmployeeUid() {return employeeUid;}
    
    public String getCustomerUid() {return customerUid;}
    
    public String getCustomerName() {return customerName;}
}
