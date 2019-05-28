package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Fragment_Customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;
import facchini.riccardo.Elk_River_DIL_2019.OnItemClickListener;
import facchini.riccardo.Elk_River_DIL_2019.R;
import facchini.riccardo.Elk_River_DIL_2019.Spot_Fishing;

public class Fragment_Customer_Search extends Fragment implements OnItemClickListener
{
    
    public static String SPOT_FISHING = "spot";
    
    private ImageButton expertButton, rentalButton, spotButton;
    private RecyclerView foundEmployeesView;
    private ProgressBar progressBar;
    
    private ArrayList<Employee> foundEmployees = new ArrayList<>();
    private ArrayList<Spot_Fishing> foundSpots = new ArrayList<>();
    private Adapter_Customer_SearchEmployeeCard adapter;
    
    private SharedPreferences sharedPreferences;
    
    //Firestore
    FirebaseFirestore db;
    CollectionReference employeesCollection, spotsCollection;
    
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
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.elk_river_preferences), Context.MODE_PRIVATE);
        
        foundEmployeesView = view.findViewById(R.id.foundEmployeesView);
        foundEmployeesView.setHasFixedSize(true);
        foundEmployeesView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        
        db = FirebaseFirestore.getInstance();
        employeesCollection = db.collection("employees");
        spotsCollection = db.collection("spots");
    }
    
    @Override
    public void onItemClick(int position)
    {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        
        try
        {
            Employee selected = foundEmployees.get(position);
            b.putParcelable("Selected", selected);
        } catch (Exception e)
        {
            
            Spot_Fishing selected = foundSpots.get(position);
            b.putParcelable("Selected", selected);
        }
        intent.putExtras(b);
        
        intent.setClass(getContext(), Activity_Customer_SelectedSearch.class);
        startActivity(intent);
    }
    
    
    /**
     * Searches if employees with the given tag exist and displays them to the user
     *
     * @param text Tag to search
     */
    private void searchTag(String text)
    {
        progressBar.setVisibility(View.VISIBLE);
        foundEmployees.clear();
        foundSpots.clear();
        
        if (text.equals(SPOT_FISHING))
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
                        Toast.makeText(getContext(), getString(R.string.noEmployeeFound), Toast.LENGTH_SHORT).show();
                    else
                        setAdapter(foundEmployees);
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
                        foundSpots.add(new Spot_Fishing(doc.getData()));
                    
                    progressBar.setVisibility(View.GONE);
                    
                    if (foundSpots.isEmpty())
                        Toast.makeText(getContext(), getString(R.string.no_fishing_spots), Toast.LENGTH_SHORT).show();
                    else
                        setAdapter(foundSpots);
                }
            });
        }
    }
    
    private void setAdapter(Object result)
    {
        if (result.getClass() == foundEmployees.getClass())
            Collections.sort((List<Employee>) result, searchEmployeeComparator);
        else
            Collections.sort(foundEmployees, searchEmployeeComparator);
        
        adapter = new Adapter_Customer_SearchEmployeeCard(getContext(), foundEmployees);
        adapter.setOnItemClickListener(this);
        foundEmployeesView.setAdapter(adapter);
    }
    
    /**
     * Defined comparator for reservations to order them
     */
    public Comparator<Employee> searchEmployeeComparator = new Comparator<Employee>()
    {
        @Override
        public int compare(Employee o1, Employee o2)
        {
            return o1.compareTo(o2);
        }
    };
}
