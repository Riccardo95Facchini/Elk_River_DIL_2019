package facchini.riccardo.Elk_River_DIL_2019.Reservation_Package;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Reservation
{
    Date time;
    private String type;
    public static final String INSTRUCTOR = "expert_instructor";
    public static final String RENTAL = "rental";
    public static final String SPOT = "spot";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM HH:mm");
    
    Reservation(Date time, String type)
    {
        this.time = time;
        this.type = type;
    }
    
    public String getDateFormatted() { return dateFormat.format(time); }
    
    public Date getTime() {return time;}
    
    public String getType() {return type;}
}
