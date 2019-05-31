package facchini.riccardo.Elk_River_DIL_2019.Reservation_Package;

import java.util.Date;

import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;
import facchini.riccardo.Elk_River_DIL_2019.Fishing_Spot.Fishing_Spot;

public class Reservation_Customer_Home extends Reservation
{
    private Employee employee;
    private Fishing_Spot fishingSpot;
    private String resUid;
    
    public Reservation_Customer_Home(Date date, String type, Employee employee, String resUid)
    {
        super(date, type);
        this.employee = employee;
        this.fishingSpot = null;
        this.resUid = resUid;
    }
    
    public Reservation_Customer_Home(Date date, String type, Fishing_Spot fishingSpot, String resUid)
    {
        super(date, type);
        this.employee = null;
        this.fishingSpot = fishingSpot;
        this.resUid = resUid;
    }
    
    public Fishing_Spot getFishingSpot() {return fishingSpot;}
    
    public Employee getEmployee() {return employee;}
    
    public String getResUid() {return resUid;}
}
