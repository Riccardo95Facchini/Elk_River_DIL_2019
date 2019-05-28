package facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Adapter_Employee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import facchini.riccardo.Elk_River_DIL_2019.Chat.Activity_Chat;
import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Customer;
import facchini.riccardo.Elk_River_DIL_2019.R;
import facchini.riccardo.Elk_River_DIL_2019.Reservation_Package.Reservation_Employee_Home;

public class Adapter_Employee_Home extends RecyclerView.Adapter<Adapter_Employee_Home.Reservation_Employee_ViewHolder>
{
    
    private Context context;
    //private String customerUid;
    private List<Reservation_Employee_Home> reservationCustomerHomeList;
    
    public Adapter_Employee_Home(Context context, List<Reservation_Employee_Home> reservationCustomerHomeList)
    {
        this.context = context;
        this.reservationCustomerHomeList = reservationCustomerHomeList;
    }
    
    @NonNull
    @Override
    public Reservation_Employee_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_employee_home, null);
        return new Reservation_Employee_ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull Reservation_Employee_ViewHolder holder, int pos)
    {
        Reservation_Employee_Home res = reservationCustomerHomeList.get(pos);
        Customer customer = res.getCustomer();
        
        //customerUid = customer.getUid();
        
        holder.textViewCustomer.setText(customer.getName().concat(" ").concat(customer.getSurname()));
        holder.textViewWhen.setText(res.getDateFormatted());
        holder.customerUid = customer.getUid();
    }
    
    @Override
    public int getItemCount()
    {
        return reservationCustomerHomeList.size();
    }
    
    class Reservation_Employee_ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewCustomer, textViewWhen;
        ImageButton startChatButton;
        String customerUid;
        
        public Reservation_Employee_ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            
            textViewCustomer = itemView.findViewById(R.id.textViewEmployeeName);
            textViewWhen = itemView.findViewById(R.id.textViewWhen);
            startChatButton = itemView.findViewById(R.id.startChatButton);
            
            startChatButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    startChat(v);
                }
            });
        }
        
        /**
         * Starts the chat with the selected user
         *
         * @param v Current view
         */
        void startChat(View v)
        {
            Intent chatIntent = new Intent(v.getContext(), Activity_Chat.class);
            SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.elk_river_preferences), Context.MODE_PRIVATE);
            String thisUsername = pref.getString(context.getString(R.string.current_user_username_key), "");
            chatIntent.putExtra("thisUsername", thisUsername);
            chatIntent.putExtra("otherUid", customerUid);
            chatIntent.putExtra("otherUsername", textViewCustomer.getText());
            v.getContext().startActivity(chatIntent);
        }
    }
}
