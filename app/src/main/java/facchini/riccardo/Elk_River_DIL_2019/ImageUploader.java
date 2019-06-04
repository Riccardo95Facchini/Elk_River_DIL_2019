package facchini.riccardo.Elk_River_DIL_2019;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class ImageUploader
{
    private Context context;
    private ImageButton imageButton;
    private ProgressBar uploadBar;
    private StorageReference profilePics;
    private String uid;
    private String storageUrl;
    private Uri image;
    
    public ImageUploader(Context context, ImageButton imageButton, ProgressBar uploadBar, String uid, Uri image)
    {
        this.context = context;
        this.imageButton = imageButton;
        this.uploadBar = uploadBar;
        this.profilePics = FirebaseStorage.getInstance().getReference("profile_pics");
        this.uid = uid;
        this.image = image;
        this.storageUrl = null;
    }
    
    public StorageTask upload()
    {
        imageButton.setClickable(false);
        imageButton.setColorFilter(Color.argb(128, 255, 255, 255));
        uploadBar.setProgress(0);
        uploadBar.setVisibility(View.VISIBLE);
        this.storageUrl = null;
        
        return profilePics.child(uid).putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        profilePics.child(uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                        {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                imageButton.setColorFilter(Color.argb(0, 0, 0, 0));
                                uploadBar.setVisibility(View.GONE);
                                imageButton.setClickable(true);
                                storageUrl = uri.toString();
                                Toast.makeText(context, context.getString(R.string.image_uploaded), Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        image = null;
                        imageButton.setClickable(true);
                        imageButton.setImageResource(R.drawable.default_avatar);
                        imageButton.setColorFilter(Color.argb(0, 0, 0, 0));
                        uploadBar.setVisibility(View.GONE);
                        Toast.makeText(context, context.getString(R.string.upload_error), Toast.LENGTH_SHORT).show();
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
    
    public String getStorageUrl() {return storageUrl;}
}
