package facchini.riccardo.Elk_River_DIL_2019;

public class Review
{
    private int reviewScore;
    private String serviceUid;
    private String customerUid;
    private String type;
    
    public Review(int reviewScore, String serviceUid, String customerUid, String type)
    {
        this.reviewScore = reviewScore;
        this.serviceUid = serviceUid;
        this.customerUid = customerUid;
        this.type = type;
    }
    
    public String getType() {return type;}
    
    public int getReviewScore() {return reviewScore;}
    
    public String getServiceUid() {return serviceUid;}
    
    public String getCustomerUid() {return customerUid;}
}
