package facchini.riccardo.Elk_River_DIL_2019;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class ImageUploader
{
    private Context context;
    private ImageView imageView;
    private ProgressBar uploadBar;
    private StorageReference profilePics;
    private String uid;
    private String storageUrl;
    private Uri image;
    private boolean editing;
    
    public ImageUploader(Context context, ImageView imageView, ProgressBar uploadBar, String uid, Uri image, boolean editing)
    {
        this.context = context;
        this.imageView = imageView;
        this.uploadBar = uploadBar;
        this.profilePics = null;
        this.uid = uid;
        this.image = image;
        this.storageUrl = "";
        this.editing = editing;
    }
    
    public StorageTask upload()
    {
        imageView.setClickable(false);
        imageView.setColorFilter(Color.argb(128, 255, 255, 255));
        uploadBar.setProgress(0);
        uploadBar.setVisibility(View.VISIBLE);
        storageUrl = "";
        
        if (profilePics != null)
            profilePics.delete();
        
        profilePics = FirebaseStorage.getInstance().getReference("profile_pics").child(uid + "_" + System.currentTimeMillis());
        
        return profilePics.putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        profilePics.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                        {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                storageUrl = uri.toString();
                                
                                if (editing)
                                    FirebaseFirestore.getInstance().collection("customers").document(uid).update("profilePicUrl", storageUrl);
                                
                                ImageLoader.loadImage(context, storageUrl, imageView);
                                uploadBar.setVisibility(View.GONE);
                                imageView.setClickable(true);
                                imageView.setColorFilter(Color.argb(0, 0, 0, 0));
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
                        imageView.setClickable(true);
                        imageView.setImageResource(R.drawable.default_avatar);
                        imageView.setColorFilter(Color.argb(0, 0, 0, 0));
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
