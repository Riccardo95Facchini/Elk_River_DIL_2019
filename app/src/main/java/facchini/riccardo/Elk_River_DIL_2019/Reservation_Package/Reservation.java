package facchini.riccardo.Elk_River_DIL_2019.Reservation_Package;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Reservation
{
    Date date;
    private String type;
    public static final String INSTRUCTOR = "expert_instructor";
    public static final String RENTAL = "rental";
    public static final String SPOT = "spot";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM HH:mm");
    
    Reservation(Date date, String type)
    {
        this.date = date;
        this.type = type;
    }
    
    public String getDateFormatted() { return dateFormat.format(date); }
    
    public Date getDate() {return date;}
    
    public String getType() {return type;}
}
