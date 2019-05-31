package facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Fragment_Employee;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Customer;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Adapter_Employee.Adapter_Employee_Home;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;
import facchini.riccardo.Elk_River_DIL_2019.R;
import facchini.riccardo.Elk_River_DIL_2019.Reservation_Package.Reservation_Employee_Home;
import facchini.riccardo.Elk_River_DIL_2019.SharedViewModel;

public class Fragment_Employee_Home extends Fragment
{
    //Firestore
    private FirebaseFirestore db;
    private CollectionReference employeesCollection, reservationsCollection;
    
    private Calendar now;
    private String employeeUid;
    private SharedViewModel viewModel;
    private List<Reservation_Employee_Home> resList;
    
    private RecyclerView recyclerView;
    private Adapter_Employee_Home adapterEmployeeHome;
    
    private TextView noReservationsText;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        getActivity().setTitle(R.string.reservations);
        return inflater.inflate(R.layout.fragment_employee_home, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        db = FirebaseFirestore.getInstance();
        SharedPreferences pref = getContext().getSharedPreferences(getString(R.string.elk_river_preferences), Context.MODE_PRIVATE);
        employeeUid = pref.getString(getString(R.string.current_user_uid_key), "");
        employeesCollection = db.collection("employees");
        reservationsCollection = db.collection("reservations");
        
        now = Calendar.getInstance();
        
        recyclerView = view.findViewById(R.id.futureReservations);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setVisibility(View.VISIBLE);
        
        noReservationsText = view.findViewById(R.id.noReservations);
        noReservationsText.setVisibility(View.GONE);
        
        resList = new ArrayList<>();
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        
        
        employeesCollection.document(employeeUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if (documentSnapshot.exists())
                    viewModel.setCurrentEmployee(new Employee(documentSnapshot.getData()));
            }
        });
        
        reservationsCollection.whereEqualTo("employeeUid", employeeUid).whereGreaterThan("time", now.getTime())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                extractNextReservations(queryDocumentSnapshots);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Extracts and checks the future reservations of the user
     *
     * @param snap
     */
    private void extractNextReservations(final QuerySnapshot snap)
    {
        if (snap.isEmpty())
        {
            recyclerView.setVisibility(View.GONE);
            noReservationsText.setVisibility(View.VISIBLE);
            return;
        }
        
        
        for (final QueryDocumentSnapshot doc : snap)
        {
            String name = doc.get("customerName").toString().substring(0, doc.get("customerName").toString().indexOf(' '));
            String surname = doc.get("customerName").toString().substring(doc.get("customerName").toString().indexOf(' ') + 1);
            Customer c = new Customer(doc.get("customerUid").toString(), name, surname);
            resList.add(new Reservation_Employee_Home(((Timestamp) doc.get("time")).toDate(), (String) doc.get("type"), c));
            
            if (resList.size() == snap.size())
                orderList();
        }
    }
    
    /**
     * Orders list to be displayed and adds elements to the adapter
     */
    private void orderList()
    {
        Collections.sort(resList, reservationComparator);
        adapterEmployeeHome = new Adapter_Employee_Home(getContext(), resList);
        recyclerView.setAdapter(adapterEmployeeHome);
    }
    
    /**
     * Defined comparator for reservations to order them
     */
    public Comparator<Reservation_Employee_Home> reservationComparator = new Comparator<Reservation_Employee_Home>()
    {
        @Override
        public int compare(Reservation_Employee_Home o1, Reservation_Employee_Home o2)
        {
            return o1.getDate().compareTo(o2.getDate());
        }
    };
}
