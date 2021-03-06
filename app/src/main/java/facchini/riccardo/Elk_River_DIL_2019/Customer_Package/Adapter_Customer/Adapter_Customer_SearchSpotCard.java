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

import com.bumptech.glide.Glide;

import java.util.List;

import facchini.riccardo.Elk_River_DIL_2019.Fishing_Spot_Package.Fishing_Spot;
import facchini.riccardo.Elk_River_DIL_2019.OnItemClickListener;
import facchini.riccardo.Elk_River_DIL_2019.R;

public class Adapter_Customer_SearchSpotCard extends RecyclerView.Adapter<Adapter_Customer_SearchSpotCard.Spot_ViewHolder>
{
    private Context context;
    private List<Fishing_Spot> fishingSpotList;
    private OnItemClickListener itemListener;
    
    
    public Adapter_Customer_SearchSpotCard(Context context, List<Fishing_Spot> fishingSpotList)
    {
        this.context = context;
        this.fishingSpotList = fishingSpotList;
    }
    
    public void setOnItemClickListener(OnItemClickListener itemListener)
    {
        this.itemListener = itemListener;
    }
    
    @NonNull
    @Override
    public Spot_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_customer_search, null);
        return new Spot_ViewHolder(view, itemListener);
    }
    
    @Override
    public void onBindViewHolder(@NonNull Spot_ViewHolder holder, int pos)
    {
        Fishing_Spot fishingSpot = fishingSpotList.get(pos);
        Glide.with(context).load(R.drawable.fishing_rod_color).fitCenter().into(holder.profilePic);
        holder.textViewName.setText(fishingSpot.getName());
        holder.textViewAddress.setText(fishingSpot.displayCoordinates());
        holder.ratingBar.setRating((float) fishingSpot.getAverageReviews());
    }
    
    @Override
    public int getItemCount()
    {
        return fishingSpotList.size();
    }
    
    class Spot_ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewName, textViewAddress;
        ImageView profilePic;
        RatingBar ratingBar;
        
        Spot_ViewHolder(@NonNull View itemView, final OnItemClickListener itemClickListener)
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
