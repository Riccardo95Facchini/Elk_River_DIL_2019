package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Activity_Customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import facchini.riccardo.Elk_River_DIL_2019.Fishing_Spot.Fishing_Spot;
import facchini.riccardo.Elk_River_DIL_2019.R;
import facchini.riccardo.Elk_River_DIL_2019.Review;

public class Activity_Customer_SelectedSpotInfo extends AppCompatActivity
{
    private RatingBar ratingReview;
    private Button buttonSend;
    private CollectionReference reviewsRef;
    
    private Fishing_Spot fishingSpot;
    private String userUid;
    private String reviewId;
    private long pastRating;
    private int currentRating;
    private SharedPreferences pref;
    
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_employee_info);
        
        reviewsRef = FirebaseFirestore.getInstance().collection("reviews");
        
        pref = getSharedPreferences(getString(R.string.elk_river_preferences), Context.MODE_PRIVATE);
        userUid = pref.getString(getString(R.string.current_user_uid_key), "");
        
        
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null)
            fishingSpot = b.getParcelable("Selected");
        
        checkReviewExists();
        
        findViewById(R.id.textHours).setVisibility(View.GONE);
        findViewById(R.id.textPhoneMail).setVisibility(View.GONE);
        findViewById(R.id.buttonChat).setVisibility(View.GONE);
        
        TextView textName = findViewById(R.id.textName);
        TextView textReviews = findViewById(R.id.textReviews);
        TextView textAddress = findViewById(R.id.textAddress);
        RatingBar ratingAvg = findViewById(R.id.ratingAvg);
        
        buttonSend = findViewById(R.id.buttonSend);
        ratingReview = findViewById(R.id.ratingReview);
        
        textName.setText(fishingSpot.getName());
        textAddress.setText(fishingSpot.displayCoordinates());
        textReviews.setText(String.format("(%.2f/5) %d %s", fishingSpot.getAverageReviews(), fishingSpot.getNumReviews(), getString(R.string.reviews)));
        ratingAvg.setRating((float) fishingSpot.getAverageReviews());
        
        ratingReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                if (rating <= 0 || rating == pastRating)
                    buttonSend.setEnabled(false);
                else
                {
                    buttonSend.setEnabled(true);
                    currentRating = (int) rating;
                }
            }
        });
        
        buttonSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendReview();
            }
        });
    }
    
    /**
     * Checks if this user has already made a review for this fishingSpot,
     * if so it stores the value and the id of the document before setting the stars as already filled
     */
    private void checkReviewExists()
    {
        reviewsRef.whereEqualTo("serviceUid", fishingSpot.getUid())
                .whereEqualTo("userUid", userUid).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        if (queryDocumentSnapshots.isEmpty())
                        {
                            pastRating = -1;
                            reviewId = "";
                        } else
                        {
                            reviewId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            pastRating = (long) queryDocumentSnapshots.getDocuments().get(0).get("reviewScore");
                            ratingReview.setRating(pastRating);
                        }
                    }
                });
    }
    
    /**
     * Sends the review and sets the boolean to force the update, still it may be faster than the calling of the server side function.
     * There is no actual way to implement a scalable update system since the listeners would constantly launch during a real deploy.
     */
    private void sendReview()
    {
        if (reviewId.isEmpty())
        {
            reviewsRef.document().set(new Review(currentRating, fishingSpot.getUid(), userUid, true));
        } else
            reviewsRef.document(reviewId).update("reviewScore", currentRating);
        
        pref.edit().putBoolean(getString(R.string.need_update_key), true).commit();
        this.finish();
    }
}
