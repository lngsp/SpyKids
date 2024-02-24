package ro.spykids.clientapp.ui.fragment.common;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.clientapi.AuthAPI;

public class LoginTabFragment extends Fragment {
    private EditText email, password;
    private Button loginBtn;
    private int duration = 800;
    private int translationX = 800;
    private float alpha = 0;
    private AuthAPI authAPI = new AuthAPI();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login_tab, container,false);

        //remove the status bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        email = root.findViewById(R.id.etxtLoginEmail);
        password = root.findViewById(R.id.etxtLoginPassword);
        loginBtn = root.findViewById(R.id.btnLogin);

        final MediaPlayer errorSound = MediaPlayer.create(getContext(), R.raw.error);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail() || !validatePassword()) {
                    errorSound.start();
                    return;
                }
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                authAPI.login(getActivity(), emailString, passwordString);
            }
        });

        return root;
    }

    //VALIDATE PARAMETERS

    public boolean validateEmail(){
        String eml = email.getText().toString();
        String checkEmail  = "[a-zA-Z0-9._-]+@(gmail|yahoo|hotmail|outlook|icloud|aol|zoho|gmx)\\.(com|net|org|edu|info|biz|me|co|gov)";
        if(eml.isEmpty()){
            email.setError("Email connot be empty!");
            return false;
        }else if(eml.length()<5){
            email.setError("Email must contain at least 5 characters!");
            return false;
        }
        else if(eml.length()>50){
            email.setError("Email must contain at most 50 characters!");
            return false;
        }
        else if(!eml.matches(checkEmail )){
            email.setError("Please enter a valid email!");
            return false;
        }
        else{
            email.setError(null);
            return true;
        }
    }

    public boolean validatePassword(){
        String passwd = password.getText().toString();
        String checkPassword = "^" +
                "(?=.*[0-9])" +         // cel puțin 1 cifră
                "(?=.*[a-z])" +         // cel puțin 1 literă mică
                "(?=.*[A-Z])" +         // cel puțin 1 literă mare
                "(?=.*[a-zA-Z])" +      // orice literă
                "(?=.*[@#$%^.&+=])" +   // cel puțin 1 caracter special
                "(?=\\S+$)" +           // fără spații albe
                ".{8,}" +               // cel puțin 5 caractere
                "$";

        if(passwd.isEmpty()){
            password.setError("Password connot be empty!");
            return false;
        }
        else if(passwd.length()<5){
            password.setError("Password must contain at least 5 characters!");
            return false;
        }
        else if(passwd.length()>50){
            password.setError("Password must contain at most 50 characters!");
            return false;
        }
        else if(!passwd.matches(checkPassword)){
            password.setError("Invalid password. It must contain: " +
                    "\n - at least 8 characters long, " +
                    "\n - at least one digit, " +
                    "\n - one upper letter, " +
                    "\n - one lower letter, " +
                    "\n - one special character, " +
                    "\n - not contain any whitespace)!");
            return false;
        }
        else{
            password.setError(null);
            return true;
        }
    }
}
