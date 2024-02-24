package ro.spykids.clientapp.ui.fragment.common;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.clientapi.AuthAPI;

public class RegisterTabFragment extends Fragment {
    private EditText email, password, firstName, lastName, phone, age;
    private RadioGroup radioGroup;
    private String radioType = "PARENT";
    private Button registerBtn;


    private int duration = 800;
    private int translationX = 800;
    private float alpha = 0;

    private AuthAPI authAPI = new AuthAPI();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_register_tab, container,false);

        //remove the status bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final MediaPlayer errorSound = MediaPlayer.create(getContext(), R.raw.error);

        email = root.findViewById(R.id.etxtRegisterEmail);
        password = root.findViewById(R.id.etxtRegisterPassword);
        firstName = root.findViewById(R.id.etxtRegisterFirstName);
        lastName = root.findViewById(R.id.etxtRegisterLastName);
        phone = root.findViewById(R.id.etxtRegisterPhone);
        age = root.findViewById(R.id.etxtRegisterAge);
        radioGroup = root.findViewById(R.id.radioGroupUserType);
        registerBtn = root.findViewById(R.id.btnRegister);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.rbtnCheckChild:
                        radioType = "CHILD";
                        break;
                    case R.id.rbtnCheckParent:
                        radioType = "PARENT";
                        break;
                    default:
                        radioType = "";
                        break;
                }
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateFirstName() || !validateLastName() || !validateEmail() || !validatePassword()  || !validatePhone() || !validateAge()  ) {
                    errorSound.start();
                    return;
                }
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                String firstNameString = firstName.getText().toString();
                String lastNameString = lastName.getText().toString();
                String phoneString = phone.getText().toString();
                Integer ageInt = Integer.parseInt(age.getText().toString());

                authAPI.register(getContext(), emailString, passwordString, firstNameString, lastNameString,
                        phoneString, ageInt, radioType);
            }
        });


        return root;
    }

    //VALIDATE PARAMETERS

    public boolean validateFirstName(){
        String checkName = "[a-zA-Z0-9-]+";
        String fn = firstName.getText().toString();
        if(fn.isEmpty()){
            firstName.setError("First name connot be empty!");
            return false;
        }
        else if(fn.matches(checkName)==false || containDigit(fn)==true){
            firstName.setError("First name cannot contain white spaces, numbers or special characters (only - is allowed)!");
            return false;
        }
        else if(fn.length()<3){
            firstName.setError("First name must contain at least 3 characters!");
            return false;
        }
        else if(fn.length()>50){
            firstName.setError("First name must contain at most 50 characters!");
            return false;
        }
        else{
            firstName.setError(null);
            return true;
        }
    }

    public boolean containDigit(String inputString){  //verify if the name contain digits
        char[] chars = inputString.toCharArray();
        for(char character : chars){
            if(Character.isDigit(character)){
                return true;
            }
        }
        return false;
    }

    public boolean validateLastName(){
        String checkName = "[a-zA-Z0-9-]+";
        String ln = lastName.getText().toString();
        if(ln.isEmpty()){
            lastName.setError("Last name connot be empty!");
            return false;
        }
        else if(ln.matches(checkName)==false || containDigit(ln)==true){
            lastName.setError("Last name cannot contain white spaces, numbers or special characters (only - is allowed)!");
            return false;
        }
        else if(ln.length()<3){
            lastName.setError("Last name must contain at least 3 characters!");
            return false;
        }
        else if(ln.length()>50){
            lastName.setError("Last name must contain at most 50 characters!");
            return false;
        }
        else{
            lastName.setError(null);
            return true;
        }
    }

    public boolean validateEmail(){
        String eml = email.getText().toString();
        String checkEmail  = "[a-zA-Z0-9._-]+@(gmail|yahoo|hotmail|outlook|icloud)\\.(com|net|org|edu|info|me|co|gov)";
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
        else if(passwd.length()>50){
            password.setError("Password must contain at most 50 characters!");
            return false;
        }
        else if(!passwd.matches(checkPassword)){
            password.setError("Invalid password. It must contain: " +
                    "\n - at least 8 characters long, " +
                    "\n - at least one digit, " +
                    "\n - at least one upper letter, " +
                    "\n - at least one lower letter, " +
                    "\n - at least one special character, " +
                    "\n - not contain any whitespace)!");
            return false;
        }
        else{
            password.setError(null);
            return true;
        }
    }

    public boolean validateAge(){
        String a = age.getText().toString();
        if(a.isEmpty()){
            age.setError("Age connot be empty!");
            return false;
        }
        else if(a.length()<1){
            age.setError("Age must contain at least 1 characters!");
            return false;
        }
        else if(a.length()>3){
            age.setError("Age must contain at most 3 characters!");
            return false;
        }
        else if(containsOnlyDigits(a)==false){
            age.setError("Phone must contain only digits!");
            return false;
        }
        else{
            age.setError(null);
            return true;
        }
    }

    public boolean validatePhone(){
        String phn = phone.getText().toString();

        if(phn.isEmpty()){
            phone.setError("Phone connot be empty!");
            return false;
        }
        else if(phn.length()<10){
            phone.setError("Phone must contain at least 10 characters!");
            return false;
        }
        else if(phn.length()>13){
            phone.setError("Phone must contain at most 13 characters!");
            return false;
        }
        else if(phn.startsWith("0") == false){
            phone.setError("Phone must start with digit 0!");
            return false;
        }
        else if (containsOnlyDigits(phn) == false) {
            phone.setError("Phone must contain only digits!");
            return false;
        }
        else{
            phone.setError(null);
            return android.util.Patterns.PHONE.matcher(phn).matches();
        }
    }

    public boolean containsOnlyDigits(String inputString) {
        return inputString.matches("[0-9]+");
    }
    //END OF VALIDATE PARAMETERS
}
