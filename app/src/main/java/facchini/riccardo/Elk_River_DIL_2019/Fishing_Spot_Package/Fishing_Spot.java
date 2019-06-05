package facchini.riccardo.Elk_River_DIL_2019.Fishing_Spot_Package;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class Fishing_Spot implements Parcelable
{
    private String uid;
    private String name;
    private String nameLowercase;
    private double latitude;
    private double longitude;
    private double averageReviews;
    private int numReviews;
    
    public Fishing_Spot(String uid, String name, double latitude, double longitude, double averageReviews, int numReviews)
    {
        this.uid = uid;
        this.name = name;
        this.nameLowercase = name.toLowerCase();
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageReviews = averageReviews;
        this.numReviews = numReviews;
    }
    
    public Fishing_Spot(String uid, String name, double latitude, double longitude)
    {
        this.uid = uid;
        this.name = name;
        this.nameLowercase = name.toLowerCase();
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageReviews = 0;
        this.numReviews = 0;
    }
    
    public Fishing_Spot(Map<String, Object> m)
    {
        this.uid = (String) m.get("uid");
        this.name = (String) m.get("name");
        this.nameLowercase = name.toLowerCase();
        this.latitude = (double) m.get("latitude");
        this.longitude = (double) m.get("longitude");
        this.numReviews = (int) ((long) m.get("numReviews"));
        
        try
        {
            this.averageReviews = (double) m.get("averageReviews");
        } catch (Exception e)
        {
            this.averageReviews = (long) m.get("averageReviews");
        }
    }
    
    public String getNameLowercase() {return nameLowercase;}
    
    public String getUid() {return uid;}
    
    public String getName() {return name;}
    
    public double getLatitude() {return latitude;}
    
    public double getLongitude() {return longitude;}
    
    public double getAverageReviews() {return averageReviews;}
    
    public int getNumReviews() {return numReviews;}
    
    public String displayCoordinates()
    {
        return String.format("Latitude: %s\nLongitude: %s", latitude, longitude);
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(nameLowercase);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(averageReviews);
        dest.writeInt(numReviews);
    }
    
    private Fishing_Spot(Parcel in)
    {
        this.uid = in.readString();
        this.name = in.readString();
        this.nameLowercase = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.averageReviews = in.readDouble();
        this.numReviews = in.readInt();
    }
    
    public static final Parcelable.Creator<Fishing_Spot> CREATOR = new Parcelable.Creator<Fishing_Spot>()
    {
        @Override
        public Fishing_Spot createFromParcel(Parcel source)
        {
            return new Fishing_Spot(source);
        }
        
        @Override
        public Fishing_Spot[] newArray(int size)
        {
            return new Fishing_Spot[size];
        }
    };
    
    public Fishing_Spot()
    {
    
    }
    
    @Override
    public int describeContents()
    {
        return 0;
    }
    
    public int compareTo(Fishing_Spot o2)
    {
        if (this.averageReviews > o2.averageReviews)
            return 1;
        else if (averageReviews < o2.averageReviews)
            return -1;
        else return 0;
    }
}
