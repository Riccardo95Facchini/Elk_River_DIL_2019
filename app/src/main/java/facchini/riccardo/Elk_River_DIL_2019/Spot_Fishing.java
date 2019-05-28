package facchini.riccardo.Elk_River_DIL_2019;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class Spot_Fishing implements Parcelable
{
    private String uid;
    private String name;
    private long latitude;
    private long longitude;
    private double averageReviews;
    private int numReviews;
    
    public Spot_Fishing(String uid, String name, long latitude, long longitude, double averageReviews, int numReviews)
    {
        this.uid = uid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageReviews = averageReviews;
        this.numReviews = numReviews;
    }
    
    public Spot_Fishing(Map<String, Object> m)
    {
        this.uid = (String) m.get("uid");
        this.name = (String) m.get("name");
        this.latitude = (long) m.get("latitude");
        this.longitude = (long) m.get("longitude");
        this.numReviews = (int) ((long) m.get("numReviews"));
        
        try
        {
            this.averageReviews = (double) m.get("averageReviews");
        } catch (Exception e)
        {
            this.averageReviews = (long) m.get("averageReviews");
        }
    }
    
    
    public String getUid() {return uid;}
    
    public String getName() {return name;}
    
    public long getLatitude() {return latitude;}
    
    public long getLongitude() {return longitude;}
    
    public double getAverageReviews() {return averageReviews;}
    
    public int getNumReviews() {return numReviews;}
    
    public String displayCoordinates()
    {
        return String.format("Latitude: %d\nLongitude: %d", latitude, longitude);
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeDouble(averageReviews);
        dest.writeInt(numReviews);
        dest.writeLong(latitude);
        dest.writeLong(longitude);
    }
    
    private Spot_Fishing(Parcel in)
    {
        this.uid = in.readString();
        this.name = in.readString();
        this.averageReviews = in.readDouble();
        this.numReviews = in.readInt();
        this.latitude = in.readLong();
        this.latitude = in.readLong();
    }
    
    public static final Parcelable.Creator<Spot_Fishing> CREATOR = new Parcelable.Creator<Spot_Fishing>()
    {
        @Override
        public Spot_Fishing createFromParcel(Parcel source)
        {
            return new Spot_Fishing(source);
        }
        
        @Override
        public Spot_Fishing[] newArray(int size)
        {
            return new Spot_Fishing[size];
        }
    };
    
    public Spot_Fishing()
    {
    
    }
    
    @Override
    public int describeContents()
    {
        return 0;
    }
}
