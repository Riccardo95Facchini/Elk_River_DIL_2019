package facchini.riccardo.Elk_River_DIL_2019.Chat_Package;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ChatData implements Comparable<ChatData>
{
    private String thisName, otherName, otherUid, lastText, otherProfilePic;
    private boolean isRead;
    private Date lastMsgDate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM HH:mm");
    
    public ChatData(String thisName, String otherName, String otherUid, String lastText, String otherProfilePic, Date lastMsgDate)
    {
        this.thisName = thisName;
        this.otherName = otherName;
        this.otherUid = otherUid;
        this.lastText = lastText;
        this.otherProfilePic = otherProfilePic;
        this.isRead = false;
        this.lastMsgDate = lastMsgDate;
    }
    
    public ChatData(HashMap<String, Object> map)
    {
        this.thisName = (String) map.get("thisName");
        this.otherName = (String) map.get("otherName");
        this.otherUid = (String) map.get("otherUid");
        this.lastText = (String) map.get("lastText");
        this.otherProfilePic = (String) map.get("otherProfilePic");
        
        try
        {
            this.isRead = (boolean) map.get("isRead");
        } catch (Exception e)
        {
            this.isRead = (boolean) map.get("read");
        }
        try
        {
            this.lastMsgDate = (Date) map.get("lastMsgDate");
        } catch (Exception e)
        {
            this.lastMsgDate = ((Timestamp) map.get("lastMsgDate")).toDate();
        }
    }
    
    public String getThisName() {return thisName;}
    
    public String getOtherUid() {return otherUid;}
    
    public String getOtherName() {return otherName;}
    
    public String getLastText() {return lastText;}
    
    public String getOtherProfilePic() {return otherProfilePic;}
    
    public boolean isRead() {return isRead;}
    
    public Date getlastMsgDate() {return lastMsgDate;}
    
    public void setLastText(String lastText)
    {
        this.lastText = lastText;
    }
    
    public void setRead(boolean read)
    {
        isRead = read;
    }
    
    public void setlastMsgDate(Date lastMsgDate)
    {
        this.lastMsgDate = lastMsgDate;
    }
    
    
    
    public String getDateFormatted() { return dateFormat.format(lastMsgDate); }
    
    @Override
    public int compareTo(ChatData o)
    {
        return this.lastMsgDate.compareTo(o.lastMsgDate);
    }
}
