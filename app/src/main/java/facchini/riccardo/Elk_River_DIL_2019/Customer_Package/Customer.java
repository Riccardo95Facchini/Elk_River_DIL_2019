package facchini.riccardo.Elk_River_DIL_2019.Customer_Package;

import java.util.Map;

public class Customer
{
    private String uid;
    private String name;
    private String surname;
    private String phone;
    private String mail;
    private String profilePicUrl;
    
    
    public Customer(String uid, String name, String surname, String phone, String mail, String profilePicUrl)
    {
        this.uid = uid;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.mail = mail;
        this.mail = mail;
        this.profilePicUrl = profilePicUrl;
    }
    
    public Customer(String uid, String name, String surname, String profilePicUrl)
    {
        this.uid = uid;
        this.name = name;
        this.surname = surname;
        this.profilePicUrl = profilePicUrl;
    }
    
    public Customer(Map<String, Object> c)
    {
        this.uid = (String) c.get("uid");
        this.name = (String) c.get("name");
        this.surname = (String) c.get("surname");
        this.phone = (String) c.get("phone");
        this.mail = (String) c.get("mail");
        this.profilePicUrl = (String) c.get("profilePicUrl");
    }
    
    public Customer() {}
    
    public String displayProfile()
    {
        return String.format("%s %s\nPhone: %s\nMail: %s", name, surname, phone, mail);
    }
    
    public String displayInfo()
    {
        return String.format("Name: %s %s\nPhone: %s\nMail: %s", name, surname, phone.isEmpty() ? "N/A" : phone, mail.isEmpty() ? "N/A" : mail);
    }
    
    public String getProfilePicUrl() {return profilePicUrl;}
    
    public String getUid() {return uid;}
    
    public String getName() {return name;}
    
    public String getSurname() {return surname;}
    
    public String getPhone() {return phone;}
    
    public String getMail() {return mail;}
    
}
