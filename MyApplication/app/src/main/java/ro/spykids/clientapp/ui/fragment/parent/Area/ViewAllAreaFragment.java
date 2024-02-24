package ro.spykids.clientapp.ui.fragment.parent.Area;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ro.spykids.clientapp.ui.model.AllAreasModel;

import java.util.ArrayList;
import java.util.List;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.clientapi.AreaAPI;
import ro.spykids.clientapp.pojo.Area;
import ro.spykids.clientapp.ui.adapter.RecyclerViewAreasAdapter;

public class ViewAllAreaFragment extends Fragment {
    private String email, token;
    private AreaAPI areaAPI = new AreaAPI();

    private RecyclerViewAreasAdapter adapter;

    ArrayList<AllAreasModel> allAreasModels = new ArrayList<>();

    public ViewAllAreaFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_all_areas, container, false);

        //remove the status bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
            token = args.getString("token");
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAllAreas);

        allAreasModels.clear();

        //get all the areas for the parent
        getAreas(new AreaAPI.AreaListCallback() {
            @Override
            public void onSuccess(List<Area> areaList) {
                for(int i = 0; i<areaList.size();i++) {


                    allAreasModels.add(new AllAreasModel(
                            areaList.get(i).getName(),
                            areaList.get(i).getAreaType().toString(),
                            areaList.get(i).getSafe().toString(),
                            areaList.get(i).getEnable().toString(),
                            areaList.get(i).getData()
                    ));
                }

                adapter.notifyDataSetChanged();
            }


            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        adapter = new RecyclerViewAreasAdapter(getContext(), allAreasModels);
        adapter.setParentEmail(email);
        adapter.setToken(token);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    public void getAreas(final AreaAPI.AreaListCallback callback) {
        areaAPI.getParentAreas(getContext(), token, email, new AreaAPI.AreaListCallback() {
            @Override
            public void onSuccess(List<Area> areaList) {
                callback.onSuccess(areaList);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }


}
