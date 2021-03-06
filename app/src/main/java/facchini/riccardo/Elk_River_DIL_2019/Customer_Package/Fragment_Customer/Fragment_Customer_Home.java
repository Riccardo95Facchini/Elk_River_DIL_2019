package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Fragment_Customer;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ProgressBar;
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

import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Adapter_Customer.Adapter_Customer_ReservationCard;
import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Customer;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;
import facchini.riccardo.Elk_River_DIL_2019.Fishing_Spot_Package.Fishing_Spot;
import facchini.riccardo.Elk_River_DIL_2019.OnItemClickListener;
import facchini.riccardo.Elk_River_DIL_2019.R;
import facchini.riccardo.Elk_River_DIL_2019.Reservation_Package.Reservation_Customer_Home;
import facchini.riccardo.Elk_River_DIL_2019.SharedViewModel;

public class Fragment_Customer_Home extends Fragment implements OnItemClickListener
{
    //Firestore
    private FirebaseFirestore db;
    private CollectionReference customersCollection, employeesCollection, spotsCollection, reservationsCollection;
    
    private Calendar now;
    private String customerUid;
    private SharedViewModel viewModel;
    private List<Reservation_Customer_Home> resList;
    private SharedPreferences pref;
    
    private RecyclerView recyclerView;
    private Adapter_Customer_ReservationCard adapterCustomerHome;
    
    private TextView noReservationsText;
    private ProgressBar progressBar;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        getActivity().setTitle(R.string.reservations);
        return inflater.inflate(R.layout.fragment_customer_home, container, false);
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        
        try
        {
            if (pref.getBoolean(getString(R.string.need_update_key), false))
            {
                pref.edit().putBoolean(getString(R.string.need_update_key), false).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Fragment_Customer_Home()).commit();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        db = FirebaseFirestore.getInstance();
        
        pref = getContext().getSharedPreferences(getString(R.string.elk_river_preferences), Context.MODE_PRIVATE);
        customerUid = pref.getString(getString(R.string.current_user_uid_key), "");
        customersCollection = db.collection("customers");
        employeesCollection = db.collection("employees");
        spotsCollection = db.collection("spots");
        reservationsCollection = db.collection("reservations");
        
        now = Calendar.getInstance();
        
        recyclerView = view.findViewById(R.id.futureReservations);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        noReservationsText = view.findViewById(R.id.noReservations);
        noReservationsText.setVisibility(View.GONE);
        
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        
        resList = new ArrayList<>();
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        
        
        customersCollection.document(customerUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if (documentSnapshot.exists())
                    viewModel.setCurrentCustomer(new Customer(documentSnapshot.getData()));
            }
        });
        
        reservationsCollection.whereEqualTo("customerUid", customerUid).whereGreaterThan("time", now.getTime())
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
                //May fail if the index as not been created in Firestore, catch exception here to get the link
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
        progressBar.setVisibility(View.VISIBLE);
        if (snap.isEmpty())
        {
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            noReservationsText.setVisibility(View.VISIBLE);
            return;
        }
        
        for (final QueryDocumentSnapshot doc : snap)
        {
            //TODO: make it more efficient, no need to load each one in its entirety (change reservation structure to include essentials)
            if (doc.get("employeeUid") != null)
            {
                employeesCollection.document((String) doc.get("employeeUid")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        if (documentSnapshot.exists())
                            resList.add(
                                    new Reservation_Customer_Home(((Timestamp) doc.get("time")).toDate(), (String) doc.get("type"),
                                            documentSnapshot.toObject(Employee.class), doc.getId()));
                        
                        if (resList.size() == snap.size())
                            orderList();
                    }
                });
            } else
            {
                spotsCollection.document((String) doc.get("spotUid")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        if (documentSnapshot.exists())
                            resList.add(
                                    new Reservation_Customer_Home(((Timestamp) doc.get("time")).toDate(), (String) doc.get("type"),
                                            documentSnapshot.toObject(Fishing_Spot.class), doc.getId()));
                        
                        if (resList.size() == snap.size())
                            orderList();
                    }
                });
            }
        }
    }
    
    /**
     * Orders list to be displayed and adds elements to the adapter
     */
    private void orderList()
    {
        Collections.sort(resList, reservationComparator);
        
        adapterCustomerHome = new Adapter_Customer_ReservationCard(getContext(), resList);
        recyclerView.setAdapter(adapterCustomerHome);
        adapterCustomerHome.setOnItemClickListener(this);
        
        progressBar.setVisibility(View.GONE);
    }
    
    /**
     * Defined comparator for reservations to order them
     */
    public Comparator<Reservation_Customer_Home> reservationComparator = new Comparator<Reservation_Customer_Home>()
    {
        @Override
        public int compare(Reservation_Customer_Home o1, Reservation_Customer_Home o2)
        {
            return o1.getTime().compareTo(o2.getTime());
        }
    };
    
    @Override
    public void onItemClick(final int position)
    {
        Reservation_Customer_Home res = resList.get(position);
        
        String name;
        
        if (res.getEmployee() != null)
            name = res.getEmployee().getName();
        else
            name = res.getFishingSpot().getName();
        
        new AlertDialog.Builder(getContext()).setCancelable(true)
                .setTitle(getString(R.string.areYouSure))
                .setMessage(getString(R.string.deleteReservationFor).concat(name).concat(getString(R.string.onWithTabs)).concat(res.getDateFormatted()))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        reservationsCollection.document(resList.get(position).getResUid()).delete();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, new Fragment_Customer_Home()).commit();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Do nothing
            }
        }).show();
    }
}
