package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Activity_Customer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import facchini.riccardo.Elk_River_DIL_2019.Activity_Login;
import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Customer;
import facchini.riccardo.Elk_River_DIL_2019.R;

public class Activity_Customer_Create extends AppCompatActivity
{
    private String uid;
    private String mail;
    private String phone;
    private String storageUrl = null;
    private Uri image = null;
    private StorageTask taskUpload;
    
    //Firestore
    private FirebaseFirestore db;
    private CollectionReference customers;
    private StorageReference profilePics;
    
    //UI
    //Buttons
    private Button sendButton;
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
        
        //Initialize UI elements
        sendButton = findViewById(R.id.sendButton);
        imageButton = findViewById(R.id.imageButton);
        firstNameText = findViewById(R.id.employeeNameText);
        surnameText = findViewById(R.id.address1Text);
        phoneText = findViewById(R.id.phoneText);
        mailText = findViewById(R.id.mailText);
        uploadBar = findViewById(R.id.uploadBar);
        
        sendButton.setEnabled(false);
        profilePics = FirebaseStorage.getInstance().getReference("profile_pics");
        
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
                        mailText.setVisibility(View.GONE);
                } catch (Exception e)
                {
                    mail = "";
                }
                
                try
                {
                    if (!phone.isEmpty())
                        phoneText.setVisibility(View.GONE);
                } catch (Exception e)
                {
                    phone = "";
                }
                
            }
        });
        
        firstNameText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                    sendButton.setEnabled(false);
                else if (surnameText.getText().toString().length() > 0)
                    sendButton.setEnabled(true);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) { }
        });
        
        surnameText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                    sendButton.setEnabled(false);
                else if (firstNameText.getText().toString().length() > 0)
                    sendButton.setEnabled(true);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    
    private void uploadImage()
    {
        imageButton.setColorFilter(Color.argb(128, 255, 255, 255));
        uploadBar.setProgress(0);
        uploadBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);
        storageUrl = null;
        
        taskUpload = profilePics.child(uid).putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        storageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        imageButton.setColorFilter(Color.argb(0, 0, 0, 0));
                        uploadBar.setVisibility(View.GONE);
                        Toast.makeText(Activity_Customer_Create.this, getString(R.string.image_uploaded), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        image = null;
                        storageUrl = null;
                        imageButton.setImageResource(R.drawable.default_avatar);
                        imageButton.setColorFilter(Color.argb(0, 0, 0, 0));
                        uploadBar.setVisibility(View.GONE);
                        Toast.makeText(Activity_Customer_Create.this, getString(R.string.upload_error), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        uploadBar.setProgress((int) progress);
                    }
                });
    }
    
    private String getFileExtension()
    {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(image));
    }
    
    private void sendData()
    {
        if (taskUpload != null && taskUpload.isInProgress())
        {
            Toast.makeText(this, getString(R.string.wait_for_image_upload), Toast.LENGTH_SHORT).show();
            return;
        }
        
        db = FirebaseFirestore.getInstance();
        customers = db.collection("customers");
        mail = mail.isEmpty() ? mailText.getText().toString() : mail;
        phone = phone.isEmpty() ? phoneText.getText().toString() : phone;
        Customer newCustomer = new Customer(uid, firstNameText.getText().toString(), surnameText.getText().toString(), phone, mail, storageUrl);
        customers.document(uid).set(newCustomer);
        
        startActivity(new Intent(this, Activity_Login.class));
    }
}
