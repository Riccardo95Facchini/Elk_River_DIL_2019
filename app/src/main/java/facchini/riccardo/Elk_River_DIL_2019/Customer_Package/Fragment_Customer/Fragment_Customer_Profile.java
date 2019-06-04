package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Fragment_Customer;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Activity_Customer.Activity_Customer_Create;
import facchini.riccardo.Elk_River_DIL_2019.ImageLoader;
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
        ImageLoader.loadImage(getContext(), viewModel.getCurrentCustomer().getProfilePicUrl(), imageView);
        profileInfoText.setText(viewModel.getCurrentCustomer().displayProfile());
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        profileInfoText = view.findViewById(R.id.profileInfoText);
        imageView = view.findViewById(R.id.imageView);
        
        Button buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), Activity_Customer_Create.class);
                intent.putExtra("uid", viewModel.getCurrentCustomer().getUid());
                intent.putExtra("nameSurname", viewModel.getCurrentCustomer().getName() + " " + viewModel.getCurrentCustomer().getSurname());
                intent.putExtra("mail", viewModel.getCurrentCustomer().getMail());
                intent.putExtra("phone", viewModel.getCurrentCustomer().getPhone());
                intent.putExtra("storageUrl", viewModel.getCurrentCustomer().getProfilePicUrl());
                intent.putExtra("editing", true);
                startActivity(intent);
            }
        });
    }
}
