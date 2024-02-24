package ro.spykids.clientapp.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ro.spykids.clientapp.ui.model.AllAreasModel;

import java.util.ArrayList;
import java.util.List;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.clientapi.AreaAPI;
import ro.spykids.clientapp.ui.activity.parent.ViewAreaOnMapActivity;

public class RecyclerViewAreasAdapter extends RecyclerView.Adapter<RecyclerViewAreasAdapter.MyViewHolder>{
    Context context;    //for inflating out layout
    ArrayList<AllAreasModel> allAreasModels;
    private AreaAPI areaAPI = new AreaAPI();

    private String parentEmail, token;

    public RecyclerViewAreasAdapter(Context context, ArrayList<AllAreasModel> allAreasModels){
        this.context = context;
        this.allAreasModels = allAreasModels;
    }


    @NonNull
    @Override
    public RecyclerViewAreasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the layout (giving a look to out rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row_areas, parent, false);

        return new RecyclerViewAreasAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAreasAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //assigning values to the views we created in the recycler_view_row layout file
        //based on the position of the recycler view
        String name = allAreasModels.get(position).getName();
        Boolean safe = Boolean.valueOf(allAreasModels.get(position).getSafe());
        Boolean enable = Boolean.valueOf(allAreasModels.get(position).getEnable());
        String type = allAreasModels.get(position).getAreaType();
        String data = allAreasModels.get(position).getData();

        holder.txtViewName.setText(name);
        holder.txtViewSafe.setText(safe.toString());
        holder.txtViewEnable.setText(enable.toString());
        holder.txtViewAreaType.setText(type);


        holder.btnViewAreaOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewAreaOnMapActivity.class);
                Log.d("RecyclerViewAreasAdapter", "DATA AICI ESTE : " + data);
                intent.putExtra("data", data);
                intent.putExtra("type", type);
                context.startActivity(intent);
            }
        });

        holder.btnSwicthSafety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areaAPI.modifyAreaSafety(view.getContext(), parentEmail, token, name, !safe);

                Boolean newsafe = !safe;

                // Actualizează lista de modele și notifică adapterul
                List<AllAreasModel> updatedModels = new ArrayList<>(allAreasModels);
                updatedModels.get(position).setSafe(newsafe.toString());
                updateData(updatedModels);
            }
        });

        holder.btnSwitchEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areaAPI.modifyAreaEnable(view.getContext(), parentEmail, token, name, !enable);

                Boolean newenable = !enable;

                // Actualizează lista de modele și notifică adapterul
                List<AllAreasModel> updatedModels = new ArrayList<>(allAreasModels);
                updatedModels.get(position).setEnable(newenable.toString());
                updateData(updatedModels);
            }
        });
    }

    @Override
    public int getItemCount() {
        //the recycler view just wants to know the number of items you want displayed
        return allAreasModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //grabbing the views from our recycler_view_row layout file

        ImageView imageView;
        TextView txtViewName, txtViewSafe, txtViewEnable, txtViewAreaType;
        Button btnViewAreaOnMap, btnSwicthSafety, btnSwitchEnable;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewAreas);
            txtViewName = itemView.findViewById(R.id.txtViewName);
            txtViewSafe = itemView.findViewById(R.id.txtViewSafeValue);
            txtViewEnable = itemView.findViewById(R.id.txtViewEnableValue);
            txtViewAreaType = itemView.findViewById(R.id.txtViewAreaTypeValue);
            btnViewAreaOnMap = itemView.findViewById(R.id.btnViewAreaOnMap);
            btnSwicthSafety = itemView.findViewById(R.id.btnSwicthSafety);
            btnSwitchEnable = itemView.findViewById(R.id.btnSwitchEnable);
        }
    }

    public void updateData(List<AllAreasModel> newModels) {
        allAreasModels.clear();
        allAreasModels.addAll(newModels);
        notifyDataSetChanged();
    }


    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}