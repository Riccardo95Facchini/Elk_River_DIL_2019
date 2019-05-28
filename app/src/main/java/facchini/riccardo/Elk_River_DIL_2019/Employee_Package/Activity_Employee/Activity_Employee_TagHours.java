package facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Activity_Employee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import facchini.riccardo.Elk_River_DIL_2019.Activity_Login;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;
import facchini.riccardo.Elk_River_DIL_2019.R;

public class Activity_Employee_TagHours extends AppCompatActivity
{
    public static String EXPERT_INSTRUCTOR = "expert_instructor", RENTAL = "rental";
    
    
    //Firestore
    private FirebaseFirestore db;
    private CollectionReference employeesReference;
    
    //UI
    //Buttons
    private Button sendButton, mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton;
    //TextViews
    private Map<String, TextView> hoursTexts;
    //Checkboxes
    private CheckBox checkExpert, checkRental;
    
    private ArrayList<String> tags;
    private Map<String, List<String>> hours;
    
    private Employee currentEmployee = null;
    private boolean editing = false;
    private String uid, mail, phone, name, address1, address2, city, zip;
    
    private ArrayAdapter<String> adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_tag_hours);
        
        getIntentAndExtras(getIntent());
        
        if (!editing)
        {
            hours = new HashMap<>();
            hoursInit();
        } else
        {
            hours = currentEmployee.getHours();
        }
        
        getUI();
        
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!tags.isEmpty())
                    sendData();
                else
                    Toast.makeText(Activity_Employee_TagHours.this, getString(R.string.noTagWarning), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Sets UI elements
     */
    private void getUI()
    {
        sendButton = findViewById(R.id.sendButton);
        
        if (hours.isEmpty())
            sendButton.setEnabled(false);
        
        mondayButton = findViewById(R.id.mondayButton);
        tuesdayButton = findViewById(R.id.tuesdayButton);
        thursdayButton = findViewById(R.id.thursdayButton);
        wednesdayButton = findViewById(R.id.wednesdayButton);
        fridayButton = findViewById(R.id.fridayButton);
        saturdayButton = findViewById(R.id.saturdayButton);
        sundayButton = findViewById(R.id.sundayButton);
        checkExpert = findViewById(R.id.checkExpert);
        checkRental = findViewById(R.id.checkRental);
        
        
        hoursTexts = new HashMap<>();
        
        hoursTexts.put("Monday", (TextView) findViewById(R.id.textMon));
        hoursTexts.put("Tuesday", (TextView) findViewById(R.id.textTue));
        hoursTexts.put("Wednesday", (TextView) findViewById(R.id.textWed));
        hoursTexts.put("Thursday", (TextView) findViewById(R.id.textThu));
        hoursTexts.put("Friday", (TextView) findViewById(R.id.textFri));
        hoursTexts.put("Saturday", (TextView) findViewById(R.id.textSat));
        hoursTexts.put("Sunday", (TextView) findViewById(R.id.textSun));
        
        if (editing)
        {
            setHoursTexts();
            
            tags = currentEmployee.getTags();
            
            for (String t : tags)
            {
                if (t.equals(EXPERT_INSTRUCTOR))
                    checkExpert.setChecked(true);
                else if (t.equals(RENTAL))
                    checkRental.setChecked(true);
            }
        }
        
        checkExpert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    sendButton.setEnabled(true);
                    tags.add(EXPERT_INSTRUCTOR);
                } else
                {
                    for (String t : tags)
                        if (t.equals(EXPERT_INSTRUCTOR))
                            tags.remove(t);
                    
                    sendButton.setEnabled(!tags.isEmpty());
                }
            }
        });
        
        checkRental.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    sendButton.setEnabled(true);
                    tags.add(RENTAL);
                } else
                {
                    for (String t : tags)
                        if (t.equals(RENTAL))
                            tags.remove(t);
                    
                    sendButton.setEnabled(!tags.isEmpty());
                }
            }
        });
        
        createSpinnerAdapter();
        
        mondayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dayButtonListenerSet("Monday");
            }
        });
        tuesdayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { dayButtonListenerSet("Tuesday"); }
        });
        thursdayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {dayButtonListenerSet("Thursday");}
        });
        wednesdayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { dayButtonListenerSet("Wednesday"); }
        });
        fridayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dayButtonListenerSet("Friday");
            }
        });
        saturdayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) { dayButtonListenerSet("Saturday"); }
        });
        sundayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dayButtonListenerSet("Sunday");
            }
        });
    }
    
    private void setHoursTexts()
    {
        hoursTexts.get("Monday").setText(currentEmployee.displayHoursDay("Monday"));
        hoursTexts.get("Tuesday").setText(currentEmployee.displayHoursDay("Tuesday"));
        hoursTexts.get("Thursday").setText(currentEmployee.displayHoursDay("Thursday"));
        hoursTexts.get("Wednesday").setText(currentEmployee.displayHoursDay("Wednesday"));
        hoursTexts.get("Friday").setText(currentEmployee.displayHoursDay("Friday"));
        hoursTexts.get("Saturday").setText(currentEmployee.displayHoursDay("Saturday"));
        hoursTexts.get("Sunday").setText(currentEmployee.displayHoursDay("Sunday"));
    }
    
    private void dayButtonListenerSet(final String day)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Employee_TagHours.this);
        View view = getLayoutInflater().inflate(R.layout.alert_opening_hours, null);
        builder.setTitle(day + " hours").setCancelable(false);
        final Spinner timeSpinner1 = view.findViewById(R.id.spinner);
        final Spinner timeSpinner2 = view.findViewById(R.id.timeSpinner2);
        final Spinner timeSpinner3 = view.findViewById(R.id.timeSpinner3);
        final Spinner timeSpinner4 = view.findViewById(R.id.timeSpinner4);
        timeSpinner1.setAdapter(adapter);
        timeSpinner2.setAdapter(adapter);
        timeSpinner3.setAdapter(adapter);
        timeSpinner4.setAdapter(adapter);
        
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                int t1 = timeSpinner1.getSelectedItemPosition();
                int t2 = timeSpinner2.getSelectedItemPosition();
                int t3 = timeSpinner3.getSelectedItemPosition();
                int t4 = timeSpinner4.getSelectedItemPosition();
                
                if (!checkHours(t1, t2, t3, t4))
                    Toast.makeText(Activity_Employee_TagHours.this, "Wrong times selected, try again", Toast.LENGTH_LONG).show();
                else
                {
                    ArrayList ret = new ArrayList<String>();
                    ret.add(timeSpinner1.getSelectedItem().toString());
                    ret.add(timeSpinner2.getSelectedItem().toString());
                    ret.add(timeSpinner3.getSelectedItem().toString());
                    ret.add(timeSpinner4.getSelectedItem().toString());
                    
                    hours.put(day, ret);
                    hoursTexts.get(day).setText(String.format("%s-%s \t %s-%s", ret.get(0), ret.get(1), ret.get(2), ret.get(3)));
                }
                
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        builder.show();
    }
    
    /**
     * Creates all values from 00:00 to 23:00 to be placed in the spinners at 1 hour steps
     */
    private void createSpinnerAdapter()
    {
        final String start = "00:00", finish = "23:00";
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        
        try
        {
            calendar.setTime(timeFormat.parse(start));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        
        ArrayList<String> spinnerText = new ArrayList<>();
        spinnerText.add("Closed");
        
        while (!(timeFormat.format(calendar.getTime()).equals(finish)))
        {
            spinnerText.add(timeFormat.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 60);
        }
        
        adapter = new ArrayAdapter<>(Activity_Employee_TagHours.this, android.R.layout.simple_spinner_item, spinnerText);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
    }
    
    private boolean checkHours(int t1, int t2, int t3, int t4)
    {
        if (t1 > t2 || t3 > t4)
            return false;
        if ((t1 != 0 && t2 == 0) || (t2 != 0 && t1 == 0) || (t3 != 0 && t4 == 0) || (t4 != 0 && t3 == 0))
            return false;
        if ((t1 >= t3 && t3 != 0) || (t2 >= t3 && t3 != 0))
            return false;
        
        return true;
    }
    
    private void getIntentAndExtras(Intent intent)
    {
        Bundle b = intent.getExtras();
        currentEmployee = b.getParcelable("CurrentEmployee");
        if (currentEmployee != null)
        {
            setTitle(R.string.edit);
            editing = true;
        } else
        {
            uid = intent.getStringExtra("uid");
            mail = intent.getStringExtra("mail");
            phone = intent.getStringExtra("phone");
            name = intent.getStringExtra("name");
            address1 = intent.getStringExtra("address1");
            address2 = intent.getStringExtra("address2");
            city = intent.getStringExtra("city");
            zip = intent.getStringExtra("zip");
        }
    }
    
    /**
     * Sends the new employee and its tag to the database
     */
    private void sendData()
    {
        db = FirebaseFirestore.getInstance();
        employeesReference = db.collection("employees");
        final Employee employee;
        if (!editing)
        {
            employee = new Employee(uid, name, mail, address1, address2, city, zip, phone, 0, 0, tags, hours);
            employeesReference.document(uid).set(employee);
            startActivity(new Intent(this, Activity_Login.class));
        } else
        {
            uid = currentEmployee.getUid();
            employee = new Employee(uid, currentEmployee.getName(), currentEmployee.getMail(), currentEmployee.getAddress1(),
                    currentEmployee.getAddress2(), currentEmployee.getCity(), currentEmployee.getZip(), currentEmployee.getPhone(),
                    currentEmployee.getAverageReviews(), currentEmployee.getNumReviews(), tags, hours);
            
            employeesReference.document(uid).set(employee);
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
    
    /**
     * Inits all days as closed
     */
    private void hoursInit()
    {
        ArrayList<String> closed = new ArrayList<String>(Arrays.asList(new String[]{"Closed", "Closed", "Closed", "Closed"}));
        
        hours.put(getString(R.string.mondayText), closed);
        hours.put(getString(R.string.tuesdayText), closed);
        hours.put(getString(R.string.wednesdayText), closed);
        hours.put(getString(R.string.thursdayText), closed);
        hours.put(getString(R.string.fridayText), closed);
        hours.put(getString(R.string.saturdayText), closed);
        hours.put(getString(R.string.sundayText), closed);
    }
    
}
