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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import facchini.riccardo.Elk_River_DIL_2019.Fishing_Spot_Package.Fishing_Spot;
import facchini.riccardo.Elk_River_DIL_2019.R;

public class Fragment_Employee_FishingSpot extends Fragment implements OnMapReadyCallback
{
    private GoogleMap googleMap;
    
    private EditText editName;
    private EditText editLat;
    private EditText editLng;
    private Button buttonSend;
    private Button buttonCheck;
    private Marker marker;
    private LatLng camera;
    
    private ArrayList<String> usedNames;
    
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
        
        MapView map = view.findViewById(R.id.map);
        
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
                    checkName(editName.getText().toString().trim().toLowerCase(), false);
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
                
                if (checkFieldsValues())
                    if (!isNameOk)
                        checkName(editName.getText().toString().trim().toLowerCase(), true);
                    else
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
                    isNameOk = false;
                    editName.getText().clear();
                    Toast.makeText(getContext(), getString(R.string.fishing_spot_added), Toast.LENGTH_SHORT).show();
                    googleMap.addMarker(new MarkerOptions().title(newSpot.getName()).position(new LatLng(newSpot.getLatitude(), newSpot.getLongitude()))).showInfoWindow();
                }
            });
        }
    }
    
    /**
     * Checkf if the spot name is already used and displays a warning if so
     *
     * @param nameLowercase Name given to the spot set to lowercase
     */
    private void checkName(String nameLowercase, final boolean forSend)
    {
        spots.whereEqualTo("nameLowercase", nameLowercase).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if (queryDocumentSnapshots.isEmpty())
                {
                    isNameOk = true;
                    marker.setTitle(editName.getText().toString().trim());
                    marker.showInfoWindow();
                    if (forSend)
                        sendData();
                } else
                {
                    isNameOk = false;
                    editName.setError(getString(R.string.name_already_used));
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
        if (lat < -90 || lat > 90)
        {
            editLat.setError(getString(R.string.wrong_coordinates));
            return false;
        } else if (lng < -180 || lng > 180)
        {
            editLng.setError(getString(R.string.wrong_coordinates));
            return false;
        }
        
        try
        {
            LatLng latLng = new LatLng(lat, lng);
            if (marker == null)
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
            else
            {
                marker.setPosition(latLng);
                marker.setTitle(title);
            }
            marker.showInfoWindow();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(latLng).zoom(15f).build()));
            return true;
        } catch (Exception e)
        {
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
        try
        {
            View currentFocus = getActivity().getCurrentFocus();
            currentFocus.clearFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onMapReady(GoogleMap map)
    {
        MapsInitializer.initialize(getContext());
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        loadMarkers();
        
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener()
        {
            @Override
            public void onCameraMove()
            {
                LatLng newCamera = googleMap.getCameraPosition().target;
                
                if (camera != null && Math.abs(camera.latitude - newCamera.latitude) < 0.00001 && Math.abs(camera.longitude - newCamera.longitude) < 0.00001)
                    return;
                
                camera = newCamera;
                editLat.setText(String.valueOf(camera.latitude));
                editLng.setText(String.valueOf(camera.longitude));
                
                marker.setPosition(camera);
                String title = editName.getText().toString().isEmpty() ? "Marker" : editName.getText().toString();
                marker.setTitle(title);
                marker.showInfoWindow();
            }
        });
        
        setMap(38.5281913, -81.7226506, "Elk River");
        editLat.setText(String.valueOf(38.5281913));
        editLng.setText(String.valueOf(-81.7226506));
    }
    
    private void loadMarkers()
    {
        spots.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                MarkerOptions options = new MarkerOptions().draggable(false);
                usedNames = new ArrayList<>();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                    googleMap.addMarker(options.title((String) doc.get("name")).position(new LatLng(doc.getDouble("latitude"), doc.getDouble("longitude")))).showInfoWindow();
            }
        });
    }
}
