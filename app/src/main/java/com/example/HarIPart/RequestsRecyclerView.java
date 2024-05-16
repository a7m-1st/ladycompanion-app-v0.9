package com.example.HarIPart;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestsRecyclerView extends RecyclerView.Adapter<RequestsRecyclerView.ThisViewHolder> {

    private static Context context;
    public static ArrayList<unsafeRequestsFromDB> unsafeRequests;

    private static final String LATLNGFILE = "latlng_info";

    public RequestsRecyclerView(Context context, ArrayList<unsafeRequestsFromDB> requestInfo) {
        this.context = context;
        this.unsafeRequests = requestInfo;
    }

    @NonNull
    @Override
    public ThisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.unsafe_zone_addition_requests, parent, false);
        return new ThisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThisViewHolder holder, int position) {
        unsafeRequestsFromDB request = unsafeRequests.get(position);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> address;
        try {
            address = geocoder.getFromLocation(request.getX(), request.getY(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String locationinString = address.get(0).getAddressLine(address.get(0).getMaxAddressLineIndex());
        String value = "Location: "+locationinString;
        holder.Location.setText(value);
        holder.DBID = request.getReqID();
    }

    @Override
    public int getItemCount() { return unsafeRequests.size(); }

    public class ThisViewHolder extends RecyclerView.ViewHolder {
        TextView Location;
        ImageButton viewOnMapButton, rejectButton;

        int DBID;

        public ThisViewHolder(@NonNull View itemView) {
            super(itemView);

            //Initialize the view
            viewOnMapButton = itemView.findViewById(R.id.viewOnMapButton);
            rejectButton = itemView.findViewById(R.id.RejectButton);
            Location = itemView.findViewById(R.id.LocationName);

            //get the position data ready

            viewOnMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    unsafeRequestsFromDB itemChosen = unsafeRequests.get(position);
                    String valueToSend = Integer.toString(itemChosen.getReqID());
                    File latlngFile = new File(context.getFilesDir(), LATLNGFILE);
                    if (latlngFile.exists()) {
                        latlngFile.delete();
                        try (FileOutputStream fos = context.openFileOutput(LATLNGFILE, Context.MODE_PRIVATE)) {
                            fos.write(valueToSend.getBytes());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try (FileOutputStream fos = context.openFileOutput(LATLNGFILE, Context.MODE_PRIVATE)) {
                            fos.write(valueToSend.getBytes());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Navigation.findNavController(itemView).navigate(R.id.action_agencyReviewUnsafeZoneRequests_to_acceptOrRejectMapRequest);
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    removeAt(position);
                    try {
                        sqlConnector connect = new sqlConnector();
                        connect.deleteUnsafeZoneRequest(DBID);
                        connect.closeConnection();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Request Removed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void removeAt(int position) {
        unsafeRequests.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, unsafeRequests.size());
    }
}

//References used:
//https://stackoverflow.com/questions/26076965/android-recyclerview-addition-removal-of-items
//https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
