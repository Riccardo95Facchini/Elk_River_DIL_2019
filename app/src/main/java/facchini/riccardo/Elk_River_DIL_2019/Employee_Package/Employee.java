package facchini.riccardo.Elk_River_DIL_2019.Employee_Package;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Employee implements Parcelable
{
    private String uid;
    private String name;
    private String mail;
    private String address1;
    private String address2;
    private String city;
    private String zip;
    private String phone;
    private double averageReviews;
    private int numReviews;
    private ArrayList<String> tags;
    private Map<String, List<String>> hours;
    private String profilePicUrl;
    
    public Employee(String uid, String name, String mail, String address1, String address2, String city,
                    String zip, String phone, double averageReviews, int numReviews,
                    ArrayList<String> tags, Map<String, List<String>> hours, String profilePicUrl)
    {
        this.uid = uid;
        this.name = name;
        this.mail = mail;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.zip = zip;
        this.phone = phone;
        this.averageReviews = averageReviews;
        this.numReviews = numReviews;
        this.tags = new ArrayList<>(tags);
        this.hours = new HashMap<>(hours);
        this.profilePicUrl = profilePicUrl;
    }
    
    public Employee(Employee e)
    {
        this.uid = e.uid;
        this.name = e.name;
        this.mail = e.mail;
        this.address1 = e.address1;
        this.address2 = e.address2;
        this.city = e.city;
        this.zip = e.zip;
        this.phone = e.phone;
        this.averageReviews = e.averageReviews;
        this.numReviews = e.numReviews;
        this.tags = new ArrayList<>(e.tags);
        this.hours = new HashMap<>(e.hours);
        this.profilePicUrl = e.profilePicUrl;
    }
    
    public Employee(Map<String, Object> m)
    {
        this.uid = (String) m.get("uid");
        this.name = (String) m.get("name");
        this.mail = (String) m.get("mail");
        this.address1 = (String) m.get("address1");
        this.address2 = (String) m.get("address2");
        this.city = (String) m.get("city");
        this.zip = (String) m.get("zip");
        this.phone = (String) m.get("phone");
        
        try
        {
            this.averageReviews = (double) m.get("averageReviews");
        } catch (Exception e)
        {
            this.averageReviews = (long) m.get("averageReviews");
        }
        this.numReviews = (int) ((long) m.get("numReviews"));
        this.tags = new ArrayList<>((ArrayList<String>) m.get("tags"));
        this.hours = new HashMap<>((HashMap<String, List<String>>) m.get("hours"));
        this.profilePicUrl = (String) m.get("profilePicUrl");
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(mail);
        dest.writeString(address1);
        dest.writeString(address2);
        dest.writeString(city);
        dest.writeString(zip);
        dest.writeString(phone);
        dest.writeDouble(averageReviews);
        dest.writeInt(numReviews);
        dest.writeList(new ArrayList<>(tags));
        dest.writeMap(new HashMap<>(hours));
        dest.writeString(profilePicUrl);
    }
    
    private Employee(Parcel in)
    {
        this.uid = in.readString();
        this.name = in.readString();
        this.mail = in.readString();
        this.address1 = in.readString();
        this.address2 = in.readString();
        this.city = in.readString();
        this.zip = in.readString();
        this.phone = in.readString();
        this.averageReviews = in.readDouble();
        this.numReviews = in.readInt();
        this.tags = in.readArrayList(Employee.class.getClassLoader());
        this.hours = in.readHashMap(Employee.class.getClassLoader());
        this.profilePicUrl = in.readString();
    }
    
    public static final Parcelable.Creator<Employee> CREATOR = new Parcelable.Creator<Employee>()
    {
        @Override
        public Employee createFromParcel(Parcel source)
        {
            return new Employee(source);
        }
        
        @Override
        public Employee[] newArray(int size)
        {
            return new Employee[size];
        }
    };
    
    public Employee()
    {
    
    }
    
    public String displayFullAddress()
    {
        return String.format("%s %s %s %s", address1, address2, city, zip);
    }
    
    public String displayHoursFormat()
    {
        StringBuilder h = new StringBuilder();
        
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thursday");
        days.add("Friday");
        days.add("Saturday");
        days.add("Sunday");
        
        for (String day : days)
        {
            List<String> entry = null;
            try
            {
                entry = hours.get(day);
                
                if (!entry.get(0).equalsIgnoreCase("closed") && !entry.get(2).equalsIgnoreCase("closed"))
                    h.append(String.format("%s: \t %s-%s \t %s-%s\n", day,
                            entry.get(0), entry.get(1), entry.get(2), entry.get(3)));
                
                else if (!entry.get(0).equalsIgnoreCase("closed"))
                    h.append(String.format("%s: \t %s-%s\n", day, entry.get(0), entry.get(1)));
                
                else if (!entry.get(3).equalsIgnoreCase("closed"))
                    h.append(String.format("%s: \t %s-%s\n", day, entry.get(2), entry.get(3)));
            } catch (Exception e)
            {
                //Nothing
            }
            
        }
        return h.toString();
    }
    
    public String displayHoursDay(String day)
    {
        String ret;
        
        try
        {
            ret = String.format("%s-%s \t %s-%s", hours.get(day).get(0), hours.get(day).get(1), hours.get(day).get(2), hours.get(day).get(3));
        } catch (Exception e)
        {
            ret = "Closed-Closed \t Closed-Closed";
        }
        
        return ret;
    }
    
    public String getPhone() {return phone;}
    
    public String getMail() {return mail;}
    
    public String getUid() {return uid;}
    
    public String getName() {return name;}
    
    public String getAddress1() {return address1;}
    
    public String getAddress2() {return address2;}
    
    public String getCity() {return city;}
    
    public String getZip() {return zip;}
    
    public ArrayList<String> getTags() {return new ArrayList<String>(tags);}
    
    public Map<String, List<String>> getHours() {return new HashMap<>(hours);}
    
    public double getAverageReviews() {return averageReviews;}
    
    public int getNumReviews() {return numReviews;}
    
    public String getProfilePicUrl() {return profilePicUrl;}
    
    @Override
    public int describeContents()
    {
        return 0;
    }
    
    public int compareTo(Employee o2)
    {
        if (this.averageReviews > o2.averageReviews)
            return 1;
        else if (averageReviews < o2.averageReviews)
            return -1;
        else return 0;
    }
    
}
