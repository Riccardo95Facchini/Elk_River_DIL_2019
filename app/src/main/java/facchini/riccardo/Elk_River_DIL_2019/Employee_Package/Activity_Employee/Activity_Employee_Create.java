package facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Activity_Employee;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.util.Locale;

import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;
import facchini.riccardo.Elk_River_DIL_2019.ImageLoader;
import facchini.riccardo.Elk_River_DIL_2019.ImageUploader;
import facchini.riccardo.Elk_River_DIL_2019.R;

public class Activity_Employee_Create extends AppCompatActivity
{
    private static final int IMAGE_REQUEST = 1;
    
    private String uid = "";
    private String mail = "";
    private String profilePic = "";
    private Address address;
    
    private String storageUrl = "";
    private StorageTask taskUpload;
    private ImageUploader imageUploader;
    private ImageView imageView;
    
    private boolean editing = false;
    private Employee currentEmployee = null;
    
    //UI
    //Buttons
    private Button continueButton;
    //Text
    private TextView textTop;
    private EditText employeeNameText, address1Text, address2Text, cityText, zipText, phoneText, mailText;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_create);
        
        //Get intent and extra
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        currentEmployee = b.getParcelable("CurrentEmployee");
        
        
        if (currentEmployee != null)
        {
            setTitle(R.string.edit);
            editing = true;
            uid = currentEmployee.getUid();
            storageUrl = currentEmployee.getProfilePicUrl();
        } else
        {
            uid = intent.getStringExtra("uid");
            mail = intent.getStringExtra("mail");
        }
        
        setUI();
        handleTextsOnCreate();
        
        ProgressBar uploadBar = findViewById(R.id.uploadBar);
        imageUploader = new ImageUploader(this, imageView, uploadBar, uid, storageUrl, editing, false);
        
        if (editing)
            continueButton.setEnabled(true);
        else
            imageUploader.uploadDefaultAvatar();
        
    }
    
    /**
     * Sets the UI elements of this activity
     */
    private void setUI()
    {
        //Initialize UI elements
        textTop = findViewById(R.id.textTop);
        employeeNameText = findViewById(R.id.employeeNameText);
        address1Text = findViewById(R.id.address1Text);
        address2Text = findViewById(R.id.address2Text);
        cityText = findViewById(R.id.cityText);
        zipText = findViewById(R.id.zipText);
        phoneText = findViewById(R.id.phoneText);
        mailText = findViewById(R.id.mailText);
        imageView = findViewById(R.id.imageView);
        
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openFilePicker();
            }
        });
        
        if (!mail.isEmpty())
            mailText.setText(mail);
        
        if (editing)
        {
            ImageLoader.loadImage(this, storageUrl, imageView);
            textTop.setText(getString(R.string.changeFieldsEditEmployee));
            employeeNameText.setText(currentEmployee.getName());
            address1Text.setText(currentEmployee.getAddress1());
            address2Text.setText(currentEmployee.getAddress2());
            cityText.setText(currentEmployee.getCity());
            zipText.setText(currentEmployee.getZip());
            phoneText.setText(currentEmployee.getPhone());
            mailText.setText(currentEmployee.getMail());
        }
        
        continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkAddress() && checkImage())
                    continueToTags();
            }
        });
        
        continueButton.setEnabled(false);
    }
    
    /**
     * Adds listeners to EditText
     */
    private void handleTextsOnCreate()
    {
        if (!editing)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (!mail.isEmpty())
                        mailText.setText(mail);
                }
            });
        }
        
        employeeNameText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                    continueButton.setEnabled(false);
                else if (isFormFull())
                    continueButton.setEnabled(true);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) { }
        });
        
        address1Text.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                    continueButton.setEnabled(false);
                else if (isFormFull())
                    continueButton.setEnabled(true);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) { }
        });
        address2Text.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                    continueButton.setEnabled(false);
                else if (isFormFull())
                    continueButton.setEnabled(true);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) { }
        });
        cityText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                    continueButton.setEnabled(false);
                else if (isFormFull())
                    continueButton.setEnabled(true);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) { }
        });
        zipText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                    continueButton.setEnabled(false);
                else if (isFormFull())
                    continueButton.setEnabled(true);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) { }
        });
        phoneText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                    continueButton.setEnabled(false);
                else if (isFormFull())
                    continueButton.setEnabled(true);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) { }
        });
        mailText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                    continueButton.setEnabled(false);
                else if (isFormFull())
                    continueButton.setEnabled(true);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    
    /**
     * Checks if the entire form has been filled
     *
     * @return true if all fields have at least one char, false otherwise
     */
    private boolean isFormFull()
    {
        return employeeNameText.getText().toString().length() > 0 &&
                address1Text.getText().toString().length() > 0 &&
                address2Text.getText().toString().length() > 0 &&
                cityText.getText().toString().length() > 0 &&
                zipText.getText().toString().length() > 0 &&
                phoneText.getText().toString().length() > 0 &&
                mailText.getText().toString().length() > 0;
    }
    
    private void openFilePicker()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }
    
    private void continueToTags()
    {
        Intent intent = new Intent(this, Activity_Employee_TagHours.class);
        
        String name = employeeNameText.getText().toString().trim();
        String mail = mailText.getText().toString().trim();
        String address1 = address1Text.getText().toString().trim();
        String address2 = address2Text.getText().toString().trim();
        String city = cityText.getText().toString().trim();
        String zip = zipText.getText().toString().trim();
        String phone = phoneText.getText().toString().trim();
        
        profilePic = imageUploader.getStorageUrl();
        
        if (!editing)
        {
            intent.putExtra("uid", uid)
                    .putExtra("name", name)
                    .putExtra("address1", address1)
                    .putExtra("address2", address2)
                    .putExtra("city", city)
                    .putExtra("zip", zip)
                    .putExtra("phone", phone)
                    .putExtra("mail", mail)
                    .putExtra("profilePic", profilePic);
            startActivity(intent);
        } else
        {
            Bundle b = new Bundle();
            Employee employee = new Employee(uid, name, mail, address1, address2, city, zip, phone,
                    currentEmployee.getAverageReviews(), currentEmployee.getNumReviews(), currentEmployee.getTags(),
                    currentEmployee.getHours(), null); //TODO: ADD PIC
            b.putParcelable("CurrentEmployee", employee);
            intent.putExtras(b);
            startActivityForResult(intent, 0);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 0 && resultCode == Activity.RESULT_OK)
        {
            setResult(Activity.RESULT_OK);
            finish();
        } else if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null)
        {
            taskUpload = imageUploader.upload(data.getData());
        }
    }
    
    private boolean checkImage()
    {
        if (taskUpload != null)
        {
            if (taskUpload.isInProgress() || imageUploader.getStorageUrl().isEmpty())
            {
                Toast.makeText(this, getString(R.string.wait_for_image_upload), Toast.LENGTH_SHORT).show();
                return false;
            } else
                storageUrl = imageUploader.getStorageUrl();
        }
        return true;
    }
    
    /**
     * Checks if the inserted address is valid, makes toast to warn the user to fix it if wrong
     *
     * @return true if valid, false otherwise
     */
    private boolean checkAddress()
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String fullAddress = String.format("%s %s %s %s",
                address1Text.getText().toString().trim(),
                address2Text.getText().toString().trim(),
                cityText.getText().toString().trim(),
                zipText.getText().toString().trim());
        
        address = null;
        
        try
        {
            address = geocoder.getFromLocationName(fullAddress, 1).get(0);
        } catch (IOException e)
        {
            Toast.makeText(this, getString(R.string.wrongAddress), Toast.LENGTH_SHORT).show();
            return false;
        } finally
        {
            if (address == null)
            {
                Toast.makeText(this, getString(R.string.wrongAddress), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
