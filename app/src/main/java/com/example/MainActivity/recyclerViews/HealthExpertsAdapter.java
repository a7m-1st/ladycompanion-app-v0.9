package com.example.MainActivity.recyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AmjadPart.fragment_contactexpert;
import com.example.DataBase.data.HealthExpertData;
import com.example.ladyapp.R;

import java.util.ArrayList;

public class HealthExpertsAdapter extends RecyclerView.Adapter<HealthExpertsAdapter.MyViewHolder> {
    private static Context context;
    public static ArrayList<HealthExpertData> healthExperts;

    public HealthExpertsAdapter(Context context, ArrayList<HealthExpertData> healthExperts){
        this.context = context;
        this.healthExperts = healthExperts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.healthexpert_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HealthExpertData expert = healthExperts.get(position);

        holder.doctorname.setText(expert.getExpertName().toString());
        holder.doctorage.setText(Integer.toString(expert.getExpertAge()));
        holder.doctorposition.setText(expert.getExpertPosition().toString());
        holder.doctorbio.setText(expert.getExpertBio().toString());
    }

    @Override
    public int getItemCount() {
        return healthExperts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView doctorname, doctorage, doctorposition, doctorbio;
        Button contact;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            // initialize your views
            doctorname = itemView.findViewById(R.id.doctorname);
            doctorage = itemView.findViewById(R.id.doctorage);
            doctorposition = itemView.findViewById(R.id.doctorposition);
            doctorbio = itemView.findViewById(R.id.doctorbio);

            contact = itemView.findViewById(R.id.contactDoctorBTN);
            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    Toast.makeText(context, "Current Position is " + position, Toast.LENGTH_SHORT).show();
                    if (position != RecyclerView.NO_POSITION) {
                        fragment_contactexpert.experPhoneNo = healthExperts.get(position).getExpertPhoneNo();
                        fragment_contactexpert.expertEmail = healthExperts.get(position).getExpertEmail();
                        Navigation.findNavController(itemView).navigate(R.id.DestContactExpert);
                    }
                }
            });
        }
    }
}
