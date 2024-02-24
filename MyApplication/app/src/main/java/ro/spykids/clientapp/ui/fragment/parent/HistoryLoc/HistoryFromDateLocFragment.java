package ro.spykids.clientapp.ui.fragment.parent.HistoryLoc;

import static java.lang.Double.NaN;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ro.spykids.clientapp.clientapi.LocationAPI;
import ro.spykids.clientapp.R;
import ro.spykids.clientapp.ui.adapter.RecyclerViewHistoryLocAdapter;
import ro.spykids.clientapp.ui.model.HistoryAllLocationsModel;
import ro.spykids.clientapp.pojo.Location;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFromDateLocFragment extends Fragment {
    private LocationAPI locationAPI = new LocationAPI();
    private String email, token;
    private RecyclerViewHistoryLocAdapter adapter;
    private CalendarView calendarView;
    private Date selectedDate;

    ArrayList<HistoryAllLocationsModel> historyLocationModels = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //remove the status bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_history_from_date_loc, container, false);

        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
            token = args.getString("token");
        }

        calendarView = view.findViewById(R.id.calendarFromDate);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHistoryFromDateLoc);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Crearea obiectului Date cu valorile selectate
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                selectedDate = new java.sql.Date(calendar.getTimeInMillis());
                changeDate();
            }
        });


        adapter = new RecyclerViewHistoryLocAdapter(getContext(), historyLocationModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void changeDate(){
        historyLocationModels.clear();

        //get all the location
        getLocations(new LocationAPI.LocationListCallback() {

            @Override
            public void onSuccess(List<Location> locationList) {
                boolean foundValidLocation = false; //pt a verifica ca nu s-a gasit o locatie valida si copilul are gps-ul oprit

                for(int i = 0; i<locationList.size();i++){
                    double latitude = locationList.get(i).getLatitude();
                    double longitude = locationList.get(i).getLongitude();

                    // If the GPS is closed
                    if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
                        continue; // move on
                    }
                    //create the address to show based on latitude and longitude
                    String address = getAddress(latitude, longitude);


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

                    String arrTime = locationList.get(i).getArrivalTime().toString();
                    LocalDateTime arrDateTime = LocalDateTime.parse(arrTime);
                    String formattedArrDate = "Arrival time:" + arrDateTime.format(formatter);

                    String deptTime = locationList.get(i).getDepartureTime().toString();
                    LocalDateTime deptDateTime = LocalDateTime.parse(deptTime);
                    String formattedDeptDate =  "Departure time:" + deptDateTime.format(formatter);

                    historyLocationModels.add(new HistoryAllLocationsModel(
                            address,
                            formattedArrDate,
                            formattedDeptDate,
                            locationList.get(i).getEmail(),
                            latitude,
                            longitude
                    ));

                    foundValidLocation = true;
                }
                adapter.notifyDataSetChanged();
                if (!foundValidLocation) {
                    Toast.makeText(getContext(), "No location found yet!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String message) {
                if(message==null){
                    Toast.makeText(getContext(), "No location found yet!", Toast.LENGTH_LONG).show();
                }else Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getLocations(final LocationAPI.LocationListCallback callback) {
        locationAPI.getLocationsFromDate(getContext(), email, token, selectedDate, new LocationAPI.LocationListCallback() {
            @Override
            public void onSuccess(List<Location> locationList) {
                callback.onSuccess(locationList);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }



    private String getAddress(double latitude, double longitude) {
        //create the address based on latutude and longitude
        String address = "";

        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            if (Double.isNaN(latitude) || Double.isNaN(longitude)){
                //ignore values
            }
            else{
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && addresses.size() > 0) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                        sb.append(returnedAddress.getAddressLine(i)).append("\n");
                    }

                    address = sb.toString();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }


}