package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Fragment_Customer;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import facchini.riccardo.Elk_River_DIL_2019.R;
import facchini.riccardo.Elk_River_DIL_2019.SharedViewModel;

public class Fragment_Customer_Profile extends Fragment
{
    
    private SharedViewModel viewModel;
    
    private TextView profileInfoText;
    private ImageView imageView;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        getActivity().setTitle(R.string.profile);
        return inflater.inflate(R.layout.fragment_customer_profile, container, false);
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        
        Picasso.get().load(Uri.parse(viewModel.getCurrentCustomer().getProfilePicUrl())).fit().centerCrop().into(imageView);
        
        profileInfoText.setText(viewModel.getCurrentCustomer().displayProfile());
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        profileInfoText = view.findViewById(R.id.profileInfoText);
        imageView = view.findViewById(R.id.imageView);
    }
}
