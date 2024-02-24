package ro.spykids.clientapp.ui.fragment.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.clientapi.AuthAPI;
import ro.spykids.clientapp.clientapi.UserAPI;
import ro.spykids.clientapp.pojo.User;
import ro.spykids.clientapp.ui.activity.common.AuthActivity;
import ro.spykids.clientapp.util.TokenManager;

public class ProfileFragment extends Fragment {
    private Button btnLogout, btnEditProfile, btnSaveProfile;
    private EditText eTxtEmail, eTxtFirstName, eTxtLastName, eTxtPhone, eTxtAge;
    private TextView txtViewType;
    private String email, token;
    private UserAPI userAPI = new UserAPI();
    private AuthAPI authAPI = new AuthAPI();
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //remove the status bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
            token = args.getString("token");
        }

        eTxtEmail = view.findViewById(R.id.eTxtProfileEmail);
        eTxtFirstName = view.findViewById(R.id.eTxtProfileFirstName);
        eTxtLastName = view.findViewById(R.id.eTxtProfileLastName);
        eTxtPhone = view.findViewById(R.id.eTxtProfilePhone);
        eTxtAge = view.findViewById(R.id.eTxtProfileAge);
        btnLogout = view.findViewById(R.id.btnLogout);
//        btnEditProfile = view.findViewById(R.id.btnEditProfile);
//        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        txtViewType = view.findViewById(R.id.txtViewProfileType);


        getUserData();



        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //revoked token
                authAPI.logout(getContext(), email);

                TokenManager tokenManager = new TokenManager(getContext());
                tokenManager.clearToken();
                Intent intent = new Intent(getContext(), AuthActivity.class);
                startActivity(intent);
            }
        });

//        btnEditProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                eTxtEmail.setEnabled(true);
//                eTxtEmail.setFocusableInTouchMode(true);
//                eTxtEmail.requestFocus();
//
//                eTxtFirstName.setEnabled(true);
//                eTxtFirstName.setFocusableInTouchMode(true);
//                eTxtFirstName.requestFocus();
//
//                eTxtLastName.setEnabled(true);
//                eTxtLastName.setFocusableInTouchMode(true);
//                eTxtLastName.requestFocus();
//
//                eTxtPhone.setEnabled(true);
//                eTxtPhone.setFocusableInTouchMode(true);
//                eTxtPhone.requestFocus();
//
//                eTxtAge.setEnabled(true);
//                eTxtAge.setFocusableInTouchMode(true);
//                eTxtAge.requestFocus();
//            }
//        });

//        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //TODO: trimite datele catre server
//            }
//        });

        return view;
    }

    private void getUserData(){
        userAPI.getUserByEmail(getContext(), email, token, new UserAPI.UserResponseListener() {
            @Override
            public void onSuccess(User userResponse) {
                txtViewType.setText(userResponse.getType());
                eTxtEmail.setText(userResponse.getEmail());
                eTxtFirstName.setText(userResponse.getFirstName());
                eTxtLastName.setText(userResponse.getLastName());
                eTxtPhone.setText(userResponse.getPhone());
                eTxtAge.setText(userResponse.getAge().toString());
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}