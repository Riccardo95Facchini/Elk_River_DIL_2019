package facchini.riccardo.Elk_River_DIL_2019;

public class Review
{
    private int reviewScore;
    private String employeeUid;
    private String userUid;
    
    public Review(int reviewScore, String employeeUid, String userUid)
    {
        this.reviewScore = reviewScore;
        this.employeeUid = employeeUid;
        this.userUid = userUid;
    }
    
    public int getReviewScore() {return reviewScore;}
    
    public String getEmployeeUid() {return employeeUid;}
    
    public String getUserUid() {return userUid;}
}
