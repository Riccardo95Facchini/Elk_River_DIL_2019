package facchini.riccardo.Elk_River_DIL_2019;

public class Review
{
    private int reviewScore;
    private String serviceUid;
    private String userUid;
    private boolean isSpot;
    
    public Review(int reviewScore, String serviceUid, String userUid, boolean isSpot)
    {
        this.reviewScore = reviewScore;
        this.serviceUid = serviceUid;
        this.userUid = userUid;
        this.isSpot = isSpot;
    }
    
    public boolean isSpot() {return isSpot;}
    
    public int getReviewScore() {return reviewScore;}
    
    public String getServiceUid() {return serviceUid;}
    
    public String getUserUid() {return userUid;}
}
