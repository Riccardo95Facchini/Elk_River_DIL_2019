package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Activity_Customer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageTask;

import facchini.riccardo.Elk_River_DIL_2019.Activity_Login;
import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Customer;
import facchini.riccardo.Elk_River_DIL_2019.ImageUploader;
import facchini.riccardo.Elk_River_DIL_2019.R;

public class Activity_Customer_Create extends AppCompatActivity
{
    private String uid;
    private String mail;
    private String phone;
    private String storageUrl = null;
    private Uri image = null;
    private StorageTask taskUpload;
    private ImageUploader imageUploader;
    
    private ImageButton imageButton;
    //Text view
    private EditText firstNameText;
    private EditText surnameText;
    private EditText phoneText;
    private EditText mailText;
    //ProgressBar
    private ProgressBar uploadBar;
    
    private static final int IMAGE_REQUEST = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_create);
        
        //Get intent and extra
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        mail = intent.getStringExtra("mail");
        phone = intent.getStringExtra("phone");
        
        Button sendButton = findViewById(R.id.sendButton);
        imageButton = findViewById(R.id.imageButton);
        firstNameText = findViewById(R.id.employeeNameText);
        surnameText = findViewById(R.id.address1Text);
        phoneText = findViewById(R.id.phoneText);
        mailText = findViewById(R.id.mailText);
        uploadBar = findViewById(R.id.uploadBar);
        
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openFilePicker();
            }
        });
        
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                if (checkImage() && checkText())
                    sendData();
            }
        });
        
        handleTextsOnCreate();
    }
    
    private void openFilePicker()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null)
        {
            image = data.getData();
            imageButton.setImageURI(image);
            uploadImage();
        }
    }
    
    /**
     * If mail or phone number already given, cache them and hide the EditText.
     * Adds listeners to EditText
     */
    private void handleTextsOnCreate()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (!mail.isEmpty())
                        mailText.setText(mail);
                } catch (Exception e)
                {
                    mail = "";
                }
                
                try
                {
                    if (!phone.isEmpty())
                        phoneText.setText(phone);
                } catch (Exception e)
                {
                    phone = "";
                }
                
            }
        });
        
    }
    
    private void uploadImage()
    {
        imageUploader = new ImageUploader(this, imageButton, uploadBar, uid, image);
        taskUpload = imageUploader.upload();
    }
    
    private String getFileExtension()
    {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(image));
    }
    
    /**
     * Check if an image has been uploaded or is being uploaded
     *
     * @return True if image uploaded or never selected (default avatar), false if it's still uploading
     */
    private boolean checkImage()
    {
        if (taskUpload != null)
        {
            if (taskUpload.isInProgress() || imageUploader.getStorageUrl() == null)
            {
                Toast.makeText(this, getString(R.string.wait_for_image_upload), Toast.LENGTH_SHORT).show();
                return false;
            } else
                storageUrl = imageUploader.getStorageUrl();
        }
        return true;
    }
    
    /**
     * Checks if the mandatory fields contain text and if the mail is correct, sets errors when false
     *
     * @return True if all mandatory fields contain text adn email is correct, false otherwise
     */
    private boolean checkText()
    {
        boolean isCheckOk = true;
        
        if (firstNameText.getText().toString().trim().isEmpty())
        {
            firstNameText.setError(getString(R.string.cant_be_empty));
            isCheckOk = false;
        }
        if (surnameText.getText().toString().trim().isEmpty())
        {
            surnameText.setError(getString(R.string.cant_be_empty));
            isCheckOk = false;
        }
        mail = mailText.getText().toString().trim();
        
        if (mail.isEmpty())
        {
            mailText.setError(getString(R.string.cant_be_empty));
            isCheckOk = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches())
        {
            mailText.setError(getString(R.string.enter_valid_mail_address));
            isCheckOk = false;
        }
        
        return isCheckOk;
    }
    
    private void sendData()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference customers = db.collection("customers");
        phone = phone.isEmpty() ? phoneText.getText().toString() : phone;
        Customer newCustomer = new Customer(uid, firstNameText.getText().toString(), surnameText.getText().toString(), phone, mail, storageUrl);
        customers.document(uid).set(newCustomer);
        startActivity(new Intent(this, Activity_Login.class));
    }
}
