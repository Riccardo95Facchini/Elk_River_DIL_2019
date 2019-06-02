package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Fragment_Customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Activity_Customer.Activity_Customer_SelectedSearch;
import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Adapter_Customer.Adapter_Customer_SearchEmployeeCard;
import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Adapter_Customer.Adapter_Customer_SearchSpotCard;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;
import facchini.riccardo.Elk_River_DIL_2019.Fishing_Spot.Fishing_Spot;
import facchini.riccardo.Elk_River_DIL_2019.OnItemClickListener;
import facchini.riccardo.Elk_River_DIL_2019.R;

public class Fragment_Customer_Search extends Fragment implements OnItemClickListener
{
    
    private ImageButton buttonInstructor, buttonRental, buttonSpot;
    private RecyclerView recyclerView;
    private LinearLayout selectionLayout;
    private ProgressBar progressBar;
    
    private ArrayList<Employee> foundEmployees = new ArrayList<>();
    private ArrayList<Fishing_Spot> foundSpots = new ArrayList<>();
    
    //Firestore
    private FirebaseFirestore db;
    private CollectionReference employeesCollection, spotsCollection;
    
    private String type;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        getActivity().setTitle(R.string.search);
        
        return inflater.inflate(R.layout.fragment_customer_search, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        recyclerView = view.findViewById(R.id.foundEmployeesView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        selectionLayout = view.findViewById(R.id.selectionLayout);
        
        buttonInstructor = view.findViewById(R.id.buttonInstructor);
        buttonInstructor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                type = getString(R.string.CONST_EXPERT_INSTRUCTOR);
                searchTag(getString(R.string.CONST_EXPERT_INSTRUCTOR));
            }
        });
        
        buttonRental = view.findViewById(R.id.buttonRental);
        buttonRental.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                type = getString(R.string.CONST_RENTAL);
                searchTag(getString(R.string.CONST_RENTAL));
            }
        });
        
        buttonSpot = view.findViewById(R.id.buttonSpot);
        buttonSpot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                type = getString(R.string.CONST_SPOT);
                searchTag(getString(R.string.CONST_SPOT));
            }
        });
        
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        
        db = FirebaseFirestore.getInstance();
        employeesCollection = db.collection("employees");
        spotsCollection = db.collection("spots");
        
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK && selectionLayout.getVisibility() == View.GONE
                        && progressBar.getVisibility() == View.GONE && recyclerView.getVisibility() == View.VISIBLE)
                {
                    selectionLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    foundEmployees.clear();
                    foundSpots.clear();
                    return true;
                }
                return false;
            }
        });
    }
    
    @Override
    public void onItemClick(int position)
    {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        
        if (!foundEmployees.isEmpty())
        {
            Employee selected = foundEmployees.get(position);
            b.putParcelable("Selected", selected);
        } else if (!foundSpots.isEmpty())
        {
            Fishing_Spot selected = foundSpots.get(position);
            b.putParcelable("Selected", selected);
        }
        
        intent.putExtra("type", type);
        intent.putExtras(b);
        
        intent.setClass(getContext(), Activity_Customer_SelectedSearch.class);
        startActivity(intent);
    }
    
    
    /**
     * Searches if employees or fishing spots with the given tag exist and displays them to the user
     *
     * @param text Tag to search
     */
    private void searchTag(String text)
    {
        progressBar.setVisibility(View.VISIBLE);
        selectionLayout.setVisibility(View.GONE);
        foundEmployees.clear();
        foundSpots.clear();
        
        if (!text.equals(getString(R.string.CONST_SPOT)))
        {
            employeesCollection.whereArrayContains("tags", text).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
            {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                        foundEmployees.add(new Employee(doc.getData()));
                    
                    progressBar.setVisibility(View.GONE);
                    
                    if (foundEmployees.isEmpty())
                    {
                        Toast.makeText(getContext(), getString(R.string.noEmployeeFound), Toast.LENGTH_SHORT).show();
                        type = "";
                        selectionLayout.setVisibility(View.VISIBLE);
                    } else
                        setAdapter(foundEmployees, false);
                }
            });
        } else
        {
            spotsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
            {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                        foundSpots.add(new Fishing_Spot(doc.getData()));
                    
                    progressBar.setVisibility(View.GONE);
                    
                    if (foundSpots.isEmpty())
                    {
                        Toast.makeText(getContext(), getString(R.string.no_fishing_spots), Toast.LENGTH_SHORT).show();
                        type = "";
                        selectionLayout.setVisibility(View.VISIBLE);
                    } else
                        setAdapter(foundSpots, true);
                }
            });
        }
    }
    
    private void setAdapter(Object result, boolean isSpot)
    {
        recyclerView.setVisibility(View.VISIBLE);
        
        if (!isSpot)
        {
            Collections.sort((List<Employee>) result, searchEmployeeComparator);
            Adapter_Customer_SearchEmployeeCard adapter = new Adapter_Customer_SearchEmployeeCard(getContext(), foundEmployees);
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);
        } else
        {
            Collections.sort((List<Fishing_Spot>) result, searchFishingSpotComparator);
            Adapter_Customer_SearchSpotCard adapter = new Adapter_Customer_SearchSpotCard(getContext(), foundSpots);
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);
        }
    }
    
    public Comparator<Employee> searchEmployeeComparator = new Comparator<Employee>()
    {
        @Override
        public int compare(Employee o1, Employee o2)
        {
            return o1.compareTo(o2);
        }
    };
    public Comparator<Fishing_Spot> searchFishingSpotComparator = new Comparator<Fishing_Spot>()
    {
        @Override
        public int compare(Fishing_Spot o1, Fishing_Spot o2)
        {
            return o1.compareTo(o2);
        }
    };
    
    
}
