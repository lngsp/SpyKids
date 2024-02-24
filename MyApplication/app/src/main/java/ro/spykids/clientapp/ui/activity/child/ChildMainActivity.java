package ro.spykids.clientapp.ui.activity.child;

import static ro.spykids.clientapp.util.Constants.LOCATION_REQUEST;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.databinding.ActivityChildMainBinding;
import ro.spykids.clientapp.ui.fragment.child.GenerateQRFragment;
import ro.spykids.clientapp.ui.fragment.child.HomeChildFragment;
import ro.spykids.clientapp.ui.fragment.common.ProfileFragment;
import ro.spykids.clientapp.util.TokenManager;

public class ChildMainActivity extends AppCompatActivity {
    private String email, token;

    ActivityChildMainBinding binding;

    MediaPlayer buttonPressedSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        buttonPressedSound = MediaPlayer.create(getApplicationContext(), R.raw.button_pressed);

        binding = ActivityChildMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = getIntent().getStringExtra("email");
        token = getIntent().getStringExtra("token");

        replaceFragment(new HomeChildFragment(email, token));

        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.bottom_home_child:
                    buttonPressedSound.start();
                    HomeChildFragment homeChildFragment = new HomeChildFragment();
//                    Bundle argsChildParent = new Bundle();
//                    argsChildParent.putString("email", email);
//                    argsChildParent.putString("token", token);
//                    homeChildFragment.setArguments(argsChildParent);
                    replaceFragment(homeChildFragment);
                    break;
                case R.id.bottom_generate_qr:
                    buttonPressedSound.start();
                    GenerateQRFragment generateQRFragment = new GenerateQRFragment();
//                    Bundle argsGenerateQR = new Bundle();
//                    argsGenerateQR.putString("email", email);
//                    argsGenerateQR.putString("token", token);
//                    generateQRFragment.setArguments(argsGenerateQR);
                    replaceFragment(generateQRFragment);
                    break;
                case R.id.bottom_profile:
                    buttonPressedSound.start();
                    ProfileFragment profileFragment = new ProfileFragment();
//                    Bundle argsProfile = new Bundle();
//                    argsProfile.putString("email", email);
//                    argsProfile.putString("token", token);
//                    profileFragment.setArguments(argsProfile);
                    replaceFragment(profileFragment);
                    break;
            }

            return true;
        });

    }

//    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.commit();
//    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Adăugați emailul și tokenul în Bundle
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("token", token);
        fragment.setArguments(args);

        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}