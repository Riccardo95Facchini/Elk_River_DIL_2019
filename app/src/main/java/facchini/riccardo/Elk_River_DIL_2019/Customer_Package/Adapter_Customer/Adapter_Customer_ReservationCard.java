package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Adapter_Customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Activity_Customer.Activity_Customer_SelectedInfo;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;
import facchini.riccardo.Elk_River_DIL_2019.Fishing_Spot.Fishing_Spot;
import facchini.riccardo.Elk_River_DIL_2019.OnItemClickListener;
import facchini.riccardo.Elk_River_DIL_2019.R;
import facchini.riccardo.Elk_River_DIL_2019.Reservation_Package.Reservation_Customer_Home;

public class Adapter_Customer_ReservationCard extends RecyclerView.Adapter<Adapter_Customer_ReservationCard.Reservation_Customer_ViewHolder>
{
    
    private Context context;
    private List<Reservation_Customer_Home> reservationCustomerHomeList;
    private OnItemClickListener itemListener;
    
    
    public Adapter_Customer_ReservationCard(Context context, List<Reservation_Customer_Home> reservationCustomerHomeList)
    {
        this.context = context;
        this.reservationCustomerHomeList = reservationCustomerHomeList;
    }
    
    public void setOnItemClickListener(OnItemClickListener itemListener)
    {
        this.itemListener = itemListener;
    }
    
    @NonNull
    @Override
    public Reservation_Customer_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_customer_home, null);
        return new Reservation_Customer_ViewHolder(view, itemListener);
    }
    
    @Override
    public void onBindViewHolder(@NonNull Reservation_Customer_ViewHolder holder, int pos)
    {
        Reservation_Customer_Home res = reservationCustomerHomeList.get(pos);
        
        Employee employee = res.getEmployee();
        if (employee != null)
        {
            holder.textViewEmployee.setText(employee.getName());
            holder.textViewAddress.setText(employee.displayFullAddress());
            holder.textViewWhen.setText(res.getDateFormatted());
        } else
        {
            Fishing_Spot fishingSpot = res.getFishingSpot();
            holder.textViewEmployee.setText(fishingSpot.getName());
            holder.textViewAddress.setText(fishingSpot.displayCoordinates());
            holder.textViewWhen.setText(res.getDateFormatted());
            holder.infoButton.setVisibility(View.GONE);
        }
    }
    
    @Override
    public int getItemCount()
    {
        return reservationCustomerHomeList.size();
    }
    
    class Reservation_Customer_ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewEmployee, textViewAddress, textViewWhen;
        ImageButton infoButton;
        
        public Reservation_Customer_ViewHolder(@NonNull View itemView, final OnItemClickListener itemClickListener)
        {
            super(itemView);
            
            textViewEmployee = itemView.findViewById(R.id.textViewEmployeeName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewWhen = itemView.findViewById(R.id.textViewWhen);
            infoButton = itemView.findViewById(R.id.infoButton);
            
            infoButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Employee employee = reservationCustomerHomeList.get(getAdapterPosition()).getEmployee();
                    startEmployeeInfoActivity(employee);
                }
            });
            
            itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if (itemClickListener != null)
                    {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            itemListener.onItemClick(position);
                    }
                    return true;
                }
            });
        }
        
        /**
         * Puts employee into a bundle in the intent and launches it
         *
         * @param employee The employee for which the info are requested
         */
        private void startEmployeeInfoActivity(Employee employee)
        {
            Intent intent = new Intent(context, Activity_Customer_SelectedInfo.class);
            Bundle b = new Bundle();
            b.putParcelable("Selected", employee);
            intent.putExtras(b);
            context.startActivity(intent);
        }
    }
}