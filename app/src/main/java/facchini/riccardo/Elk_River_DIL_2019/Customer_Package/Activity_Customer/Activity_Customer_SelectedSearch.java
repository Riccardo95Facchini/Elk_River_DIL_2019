package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Activity_Customer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import facchini.riccardo.Elk_River_DIL_2019.Chat.Activity_Chat;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;
import facchini.riccardo.Elk_River_DIL_2019.Fishing_Spot.Fishing_Spot;
import facchini.riccardo.Elk_River_DIL_2019.Fragment_DatePicker;
import facchini.riccardo.Elk_River_DIL_2019.R;
import facchini.riccardo.Elk_River_DIL_2019.Reservation_Package.ReservationDatabase;

public class Activity_Customer_SelectedSearch extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{
    //Firestore
    FirebaseFirestore db;
    CollectionReference reservationsCollection;
    
    private Employee selectedEmployee;
    private Fishing_Spot selectedSpot;
    private ArrayAdapter<String> adapter;
    private Calendar selectedDate, now;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    private TextView nameText, employeeInfoText, employeeHoursText;
    private Button selectDateButton;
    private ImageButton startChatButton;
    
    private boolean isSpot = false;
    
    SharedPreferences sharedPref;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_selected_employee);
        
        sharedPref = getSharedPreferences(getString(R.string.elk_river_preferences), Context.MODE_PRIVATE);
        
        now = Calendar.getInstance();
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        
        db = FirebaseFirestore.getInstance();
        reservationsCollection = db.collection("reservations");
        
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        
        employeeInfoText = findViewById(R.id.employeeInfoText);
        employeeHoursText = findViewById(R.id.employeeHoursText);
        startChatButton = findViewById(R.id.startChatButton);
        
        String name = "";
        
        if (b != null)
        {
            if (b.getParcelable("Selected").getClass() == Employee.class)
            {
                selectedEmployee = b.getParcelable("Selected");
                employeeInfoText.setText(String.format("City: %s \tAddress: %s %s", selectedEmployee.getCity(),
                        selectedEmployee.getAddress1(), selectedEmployee.getAddress2()));
                employeeHoursText.setText(selectedEmployee.displayHoursFormat());
                name = selectedEmployee.getName();
                isSpot = false;
                startChatButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startChat();
                    }
                });
                
                
            } else
            {
                selectedSpot = b.getParcelable("Selected");
                name = selectedSpot.getName();
                employeeInfoText.setVisibility(View.GONE);
                employeeHoursText.setVisibility(View.GONE);
                startChatButton.setVisibility(View.GONE);
                isSpot = true;
            }
        }
        
        nameText = findViewById(R.id.employeeNameText);
        selectDateButton = findViewById(R.id.selectDateButton);
        
        nameText.setText(name);
        
        
        selectDateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogFragment datePicker = new Fragment_DatePicker();
                datePicker.show(getSupportFragmentManager(), "DatePicker");
            }
        });
    }
    
    private void startChat()
    {
        Intent chatIntent = new Intent(Activity_Customer_SelectedSearch.this, Activity_Chat.class);
        chatIntent.putExtra("thisUsername", sharedPref.getString(getString(R.string.current_user_username_key), ""));
        chatIntent.putExtra("otherUid", selectedEmployee.getUid());
        chatIntent.putExtra("otherUsername", selectedEmployee.getName());
        startActivity(chatIntent);
    }
    
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, year);
        selectedDate.set(Calendar.MONTH, month);
        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        selectedDate.set(Calendar.AM_PM, 0);
        selectedDate.set(Calendar.HOUR, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);
        
        checkHoursDate();
    }
    
    /**
     * Takes the selected day and does a query to check if there are already reservations on that day
     */
    private void checkHoursDate()
    {
        Calendar plusDay = Calendar.getInstance();
        plusDay.setTime(selectedDate.getTime());
        plusDay.add(Calendar.HOUR, 24);
        
        if (!isSpot)
        {
            
            reservationsCollection.whereEqualTo("employeeUid", selectedEmployee.getUid())
                    .whereGreaterThan("time", selectedDate.getTime())
                    .whereLessThan("time", plusDay.getTime()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            createSpinnerAdapter(queryDocumentSnapshots);
                        }
                    }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    //Catch here if index isn't created on firestore
                    e.printStackTrace();
                }
            });
        } else
        {
            reservationsCollection.whereEqualTo("spotUid", selectedSpot.getUid())
                    .whereGreaterThan("time", selectedDate.getTime())
                    .whereLessThan("time", plusDay.getTime()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            createSpinnerAdapter(queryDocumentSnapshots);
                        }
                    }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    //Catch here if index isn't created on firestore
                    e.printStackTrace();
                }
            });
        }
    }
    
    /**
     * Creates the spinner adapter to select the reservation time based on opening hours and already reserved hours,
     * if no slots are available displays an alert to the user asking to select a different date.
     *
     * @param snap result of the query, contains all reservations for the selected day
     */
    private void createSpinnerAdapter(QuerySnapshot snap)
    {
        List<String> reservedHours = new ArrayList<>();
        
        for (QueryDocumentSnapshot doc : snap)
            reservedHours.add(timeFormat.format(((Timestamp) doc.get("time")).toDate()));
        
        ArrayList<String> spinnerText = new ArrayList<>();
        String dayOfTheWeek = getDayString();
        List<String> hoursSelectedDay;
        
        if (!isSpot)
        {
            try
            {
                    /*Needed for testing, some employees don't have hours registered for a closed day, resulting in a NPE.
                     If a employee is registered with the system "closed" tags are generated automatically. */
                hoursSelectedDay = new ArrayList<>(selectedEmployee.getHours().get(dayOfTheWeek));
                
                String h1 = hoursSelectedDay.get(0),
                        h2 = hoursSelectedDay.get(1),
                        h3 = hoursSelectedDay.get(2),
                        h4 = hoursSelectedDay.get(3);
                
                if (!h1.equalsIgnoreCase(getString(R.string.closedLowercase)))
                    buildSpinnerArray(h1, h2, spinnerText);
                if (!h3.equalsIgnoreCase(getString(R.string.closedLowercase)))
                    buildSpinnerArray(h3, h4, spinnerText);
                
            } catch (NullPointerException npe)
            {
                //Nothing needs to be done since spinnerText will still be empty
                npe.printStackTrace();
            }
        } else
        {
            buildSpinnerArray("00:00", "23:00", spinnerText);
            spinnerText.add("23:00");
        }
        
        if (!reservedHours.isEmpty() && !spinnerText.isEmpty())
            spinnerText.removeAll(reservedHours); //Removes taken hours from the spinner list
        
        if (spinnerText.isEmpty())
        {
            new AlertDialog.Builder(this).setTitle(getString(R.string.sorry)).setCancelable(false)
                    .setMessage(getString(R.string.noFreeSlots))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //Do nothing
                        }
                    }).show();
        } else
        {
            adapter = new ArrayAdapter<>(Activity_Customer_SelectedSearch.this, android.R.layout.simple_spinner_item, spinnerText);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            showSpinner(dayOfTheWeek);
        }
    }
    
    /**
     * Builds the spinner text with all the possible hours between start and finish, finish is not added
     *
     * @param start       Opening hour
     * @param finish      Closing hour
     * @param spinnerText ArrayList containing strings to be placed in the spinner
     */
    private void buildSpinnerArray(String start, String finish, ArrayList<String> spinnerText)
    {
        Calendar calStart = Calendar.getInstance();
        
        try
        {
            if (selectedDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) && selectedDate.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                    && selectedDate.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) && selectedDate.get(Calendar.DAY_OF_WEEK) == now.get(Calendar.DAY_OF_WEEK))
            {
                Calendar plusOne = Calendar.getInstance();
                plusOne.setTime(now.getTime());
                plusOne.add(Calendar.MINUTE, 60);
                start = timeFormat.format(plusOne.getTime());
            }
            
            calStart.setTime(timeFormat.parse(start));
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        
        while (!(timeFormat.format(calStart.getTime()).equals(finish)))
        {
            spinnerText.add(timeFormat.format(calStart.getTime()));
            calStart.add(Calendar.MINUTE, 60);
        }
    }
    
    /**
     * Converts day of the week int into string used by the system
     *
     * @return Sunday to Saturday as string given the selected day
     */
    private String getDayString()
    {
        switch (selectedDate.get(Calendar.DAY_OF_WEEK))
        {
            case Calendar.SUNDAY:
                return getString(R.string.sundayText);
            case Calendar.MONDAY:
                return getString(R.string.mondayText);
            case Calendar.TUESDAY:
                return getString(R.string.tuesdayText);
            case Calendar.WEDNESDAY:
                return getString(R.string.wednesdayText);
            case Calendar.THURSDAY:
                return getString(R.string.thursdayText);
            case Calendar.FRIDAY:
                return getString(R.string.fridayText);
            case Calendar.SATURDAY:
                return getString(R.string.saturdayText);
            
        }
        return null;
    }
    
    /**
     * Shows the spinner to select the reservation time
     *
     * @param day chosen day
     */
    private void showSpinner(final String day)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Customer_SelectedSearch.this);
        View view = getLayoutInflater().inflate(R.layout.alert_select_slot, null);
        builder.setTitle(day + " " + getString(R.string.availableHours));
        final Spinner spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        
        builder.setPositiveButton(getString(R.string.set), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                setDialogResult(spinner.getSelectedItem().toString());
                dialog.dismiss();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                Toast.makeText(Activity_Customer_SelectedSearch.this, "No reservation selected", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(view);
        builder.show();
    }
    
    /**
     * Called by the spinner dialog on positive button press, calls the update of the array in the reservation collection
     *
     * @param result Selected element of the spinner
     */
    private void setDialogResult(String result)
    {
        Date fullDate = new Date();
        String date = dateFormat.format(selectedDate.getTime()).concat(" ".concat(result));
        try
        {
            fullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
        } catch (ParseException e)
        {
            //TODO: handle exception
            e.printStackTrace();
        }
        
        String customerName = sharedPref.getString(getString(R.string.current_user_username_key), "");
        
        String thisUid = getSharedPreferences(getString(R.string.elk_river_preferences), Context.MODE_PRIVATE)
                .getString(getString(R.string.current_user_uid_key), "");
        
        String reservedUid;
        
        if (isSpot)
            reservedUid = selectedSpot.getUid();
        else
            reservedUid = selectedEmployee.getUid();
        
        
        ReservationDatabase reservationDatabase = new ReservationDatabase(reservedUid, thisUid, customerName, fullDate, isSpot);
        db.collection("reservations").add(reservationDatabase);
        
        Toast.makeText(this, getString(R.string.reservationCompleted), Toast.LENGTH_LONG).show();
    }
}
