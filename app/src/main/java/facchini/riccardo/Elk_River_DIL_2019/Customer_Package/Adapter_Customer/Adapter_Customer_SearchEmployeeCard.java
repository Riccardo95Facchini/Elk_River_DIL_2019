package facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Adapter_Customer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;
import facchini.riccardo.Elk_River_DIL_2019.ImageLoader;
import facchini.riccardo.Elk_River_DIL_2019.OnItemClickListener;
import facchini.riccardo.Elk_River_DIL_2019.R;

public class Adapter_Customer_SearchEmployeeCard extends RecyclerView.Adapter<Adapter_Customer_SearchEmployeeCard.Employees_ViewHolder>
{
    private Context context;
    private List<Employee> employeesList;
    private OnItemClickListener itemListener;
    
    
    public Adapter_Customer_SearchEmployeeCard(Context context, List<Employee> employeesList)
    {
        this.context = context;
        this.employeesList = employeesList;
    }
    
    public void setOnItemClickListener(OnItemClickListener itemListener)
    {
        this.itemListener = itemListener;
    }
    
    @NonNull
    @Override
    public Employees_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_customer_search, null);
        return new Employees_ViewHolder(view, itemListener);
    }
    
    @Override
    public void onBindViewHolder(@NonNull Employees_ViewHolder holder, int pos)
    {
        Employee employee = employeesList.get(pos);
        
        holder.textViewName.setText(employee.getName());
        holder.textViewAddress.setText(employee.displayFullAddress());
        holder.ratingBar.setRating((float) employee.getAverageReviews());
        ImageLoader.loadImage(context, employee.getProfilePicUrl(), holder.profilePic);
    }
    
    @Override
    public int getItemCount()
    {
        return employeesList.size();
    }
    
    class Employees_ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewName, textViewAddress;
        RatingBar ratingBar;
        ImageView profilePic;
        
        Employees_ViewHolder(@NonNull View itemView, final OnItemClickListener itemClickListener)
        {
            super(itemView);
            
            textViewName = itemView.findViewById(R.id.textViewEmployeeName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            profilePic = itemView.findViewById(R.id.profilePic);
            
            
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (itemClickListener != null)
                    {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            itemListener.onItemClick(position);
                    }
                }
            });
        }
    }
}
