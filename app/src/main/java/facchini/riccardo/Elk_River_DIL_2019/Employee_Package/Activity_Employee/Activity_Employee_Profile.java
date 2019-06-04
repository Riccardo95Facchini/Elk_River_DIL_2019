package facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Activity_Employee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import facchini.riccardo.Elk_River_DIL_2019.ImageLoader;
import facchini.riccardo.Elk_River_DIL_2019.R;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;

public class Activity_Employee_Profile extends AppCompatActivity
{
    private Button buttonEdit;
    
    private TextView textEmployeeName;
    private TextView textHours;
    private TextView textReviews;
    private TextView textPhoneMail;
    private TextView textAddress;
    private RatingBar ratingAvg;
    private ImageView imageView;
    
    Context context;
    private Employee employee;
    private String employeeUid;
    private SharedPreferences pref;
    
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);
        
        pref = getSharedPreferences(getString(R.string.elk_river_preferences), Context.MODE_PRIVATE);
        employeeUid = pref.getString(getString(R.string.current_user_uid_key), "");
        
        FirebaseFirestore.getInstance().collection("employees").document(employeeUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if (documentSnapshot.exists())
                {
                    employee = new Employee(documentSnapshot.getData());
                    fillProfile();
                }
            }
        });
        
        textEmployeeName = findViewById(R.id.textName);
        textHours = findViewById(R.id.textHours);
        textReviews = findViewById(R.id.textReviews);
        textPhoneMail = findViewById(R.id.textPhoneMail);
        textAddress = findViewById(R.id.textAddress);
        buttonEdit = findViewById(R.id.buttonEdit);
        ratingAvg = findViewById(R.id.ratingAvg);
        imageView = findViewById(R.id.imageView);
        
        context = this;
        buttonEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putParcelable("CurrentEmployee", employee);
                intent.putExtras(b);
                intent.setClass(context, Activity_Employee_Create.class);
                startActivityForResult(intent, 0);
            }
        });
    }
    
    private void fillProfile()
    {
        ImageLoader.loadImage(this, employee.getProfilePicUrl(), imageView);
        textEmployeeName.setText(employee.getName());
        textAddress.setText(String.format("%s %s %s %s", employee.getAddress1(), employee.getAddress2(),
                employee.getCity(), employee.getZip()));
        textReviews.setText(String.format("(%.2f/5) %d %s", employee.getAverageReviews(), employee.getNumReviews(), getString(R.string.reviews)));
        textPhoneMail.setText(String.format("Phone: %s\nMail: %s", employee.getPhone(), employee.getMail()));
        textHours.setText(employee.displayHoursFormat());
        ratingAvg.setRating((float) employee.getAverageReviews());
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == 0 && resultCode == Activity.RESULT_OK)
        {
            recreate();
        }
    }
}
