package facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Fragment_Employee;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import facchini.riccardo.Elk_River_DIL_2019.Fishing_Spot.Fishing_Spot;
import facchini.riccardo.Elk_River_DIL_2019.R;

public class Fragment_Employee_FishingSpot extends Fragment implements OnMapReadyCallback
{
    private GoogleMap googleMap;
    private MapView map;
    
    private EditText editName;
    private EditText editLat;
    private EditText editLng;
    private Button buttonSend;
    private Button buttonCheck;
    
    private boolean isNameOk;
    
    private CollectionReference spots;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_employee_fishing_spot, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        map = view.findViewById(R.id.map);
        
        map.onCreate(null);
        map.onResume();
        map.getMapAsync(this);
        
        editName = view.findViewById(R.id.editName);
        editLat = view.findViewById(R.id.editLat);
        editLng = view.findViewById(R.id.editLng);
        buttonSend = view.findViewById(R.id.buttonSend);
        buttonCheck = view.findViewById(R.id.buttonCheck);
        
        spots = FirebaseFirestore.getInstance().collection("spots");
        isNameOk = false;
        
        editName.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                isNameOk = false;
                
                if (!hasFocus && !editName.getText().toString().trim().isEmpty())
                    checkName(editName.getText().toString().trim().toLowerCase());
            }
        });
        
        buttonCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideKeyboard();
                
                if (checkFieldsValues() && isNameOk)
                    setMap(Double.parseDouble(editLat.getText().toString()), Double.parseDouble(editLng.getText().toString()),
                            editName.getText().toString().trim());
            }
        });
        
        buttonSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideKeyboard();
                
                if (checkFieldsValues() && isNameOk)
                    sendData();
            }
        });
    }
    
    /**
     * Checks each text if it's empty and sets a message if it's so
     *
     * @return False if at least one edittext is empty, True if all are full
     */
    private boolean checkFieldsValues()
    {
        if (editName.getText().toString().trim().isEmpty())
            editName.setError(getString(R.string.enter_name_fishing_spot));
        if (editLat.getText().toString().trim().isEmpty())
            editLat.setError(getString(R.string.enter_valid_lat));
        if (editLng.getText().toString().trim().isEmpty())
            editLng.setError(getString(R.string.enter_valid_lng));
        
        return !editName.getText().toString().trim().isEmpty()
                && !editLat.getText().toString().trim().isEmpty()
                && !editLng.getText().toString().trim().isEmpty();
    }
    
    private void sendData()
    {
        DocumentReference docRef = spots.document();
        final Fishing_Spot newSpot = new Fishing_Spot(docRef.getId(), editName.getText().toString().trim(), Double.parseDouble(editLat.getText().toString()), Double.parseDouble(editLng.getText().toString()));
        
        if (setMap(newSpot.getLatitude(), newSpot.getLongitude(), newSpot.getName()))
        {
            docRef.set(newSpot).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Toast.makeText(getContext(), getString(R.string.fishing_spot_added), Toast.LENGTH_SHORT).show();
                    editName.getText().clear();
                    editLat.getText().clear();
                    editLng.getText().clear();
                    isNameOk = false;
                }
            });
        }
    }
    
    /**
     * Checkf if the spot name is already used and displays a warning if so
     *
     * @param nameLowercase Name given to the spot set to lowercase
     */
    private void checkName(String nameLowercase)
    {
        spots.whereEqualTo("nameLowercase", nameLowercase).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if (queryDocumentSnapshots.isEmpty())
                    isNameOk = true;
                else
                {
                    isNameOk = false;
                    editName.setError(getString(R.string.name_already_used));
                    Fishing_Spot existingSpot = new Fishing_Spot(queryDocumentSnapshots.getDocuments().get(0).getData());
                    setMap(existingSpot.getLatitude(), existingSpot.getLongitude(), existingSpot.getName());
                }
            }
        });
    }
    
    /**
     * Checks if latitude and longitude are correct, then sets a marker on the map and moves the camera to it
     *
     * @param lat   Latitude value
     * @param lng   Longitude value
     * @param title Title to be given to the marker
     * @return True if map was set, false otherwise
     */
    private boolean setMap(double lat, double lng, String title)
    {
        try
        {
            if (lat < -90 || lat > 90 || lng < -180 || lng > 180)
                throw new Exception();
            
            LatLng latLng = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(latLng).zoom(15f).build()));
            return true;
        } catch (Exception e)
        {
            editLat.setError(getString(R.string.wrong_coordinates));
            editLng.setError(getString(R.string.wrong_coordinates));
            return false;
        }
    }
    
    /**
     * Hides keyboard and clears errors
     */
    private void hideKeyboard()
    {
        editLat.setError(null);
        editLng.setError(null);
        View currentFocus = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        MapsInitializer.initialize(getContext());
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}
