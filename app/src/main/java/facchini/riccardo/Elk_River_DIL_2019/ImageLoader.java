package facchini.riccardo.Elk_River_DIL_2019;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageLoader
{
    public static void loadImage(final Context context, final String url, final ImageView imageView)
    {
        Glide.with(context).load(url).placeholder(R.drawable.default_avatar).fitCenter().centerCrop().into(imageView);
    }
}

