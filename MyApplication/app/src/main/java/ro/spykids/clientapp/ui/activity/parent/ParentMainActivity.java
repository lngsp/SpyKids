package ro.spykids.clientapp.ui.activity.parent;

import static ro.spykids.clientapp.util.Constants.NOTIFICATION_PERMISSION_REQUEST_CODE;
import static ro.spykids.clientapp.util.Constants.NOTIFICATION_REQUEST;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ro.spykids.clientapp.ui.fragment.parent.Area.AddChildToAreaFragment;
import ro.spykids.clientapp.ui.fragment.parent.Area.CircleAreaFragment;
import ro.spykids.clientapp.ui.fragment.parent.Area.PolygonAreaFragment;
import ro.spykids.clientapp.ui.fragment.parent.Area.ViewAllAreaFragment;
import ro.spykids.clientapp.ui.fragment.parent.HistoryLoc.HistoryAllLocationsFragment;
import ro.spykids.clientapp.ui.fragment.parent.HistoryLoc.HistoryBetweenDateAndNowLocFragment;
import ro.spykids.clientapp.ui.fragment.parent.HistoryLoc.HistoryFromDateLocFragment;
import ro.spykids.clientapp.ui.fragment.parent.NewChildFragment;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.databinding.ActivityParentMainBinding;
import ro.spykids.clientapp.ui.fragment.common.ProfileFragment;
import ro.spykids.clientapp.ui.fragment.parent.HomeParentFragment;
import ro.spykids.clientapp.util.TokenManager;


public class ParentMainActivity extends AppCompatActivity {
    private String email, token;

    ActivityParentMainBinding binding;

    MediaPlayer buttonPressedSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityParentMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonPressedSound = MediaPlayer.create(getApplicationContext(), R.raw.button_pressed);

        email = getIntent().getStringExtra("email");
        token = getIntent().getStringExtra("token");


        replaceFragment(new HomeParentFragment(email, token));

        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.bottom_current_loc:
                    buttonPressedSound.start();
                    HomeParentFragment homeParentFragment = new HomeParentFragment();
//                    Bundle argsHomeParent = new Bundle();
//                    argsHomeParent.putString("email", email);
//                    argsHomeParent.putString("token", token);
//                    homeParentFragment.setArguments(argsHomeParent);
                    replaceFragment(homeParentFragment);
                    break;
                case R.id.bottom_history:
                    buttonPressedSound.start();
                    showBottomDialogHistory();
                    break;
                case R.id.bottom_new_child:
                    buttonPressedSound.start();
                    NewChildFragment newChildFragment = new NewChildFragment();
//                    Bundle argsNewChild = new Bundle();
//                    argsNewChild.putString("email", email);
//                    argsNewChild.putString("token", token);
//                    newChildFragment.setArguments(argsNewChild);
                    replaceFragment(newChildFragment);
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

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPressedSound.start();
                showBottomDialog();
            }
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

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout_areas);

        LinearLayout circleAreaLayout = dialog.findViewById(R.id.layoutCircleArea);
        LinearLayout polygonAreaLayout = dialog.findViewById(R.id.layoutPolygonArea);
        LinearLayout addChildToAreaLayout = dialog.findViewById(R.id.layoutAddChildToArea);
        LinearLayout viewAllAreas = dialog.findViewById(R.id.layoutViewAllArea);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        circleAreaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressedSound.start();
                CircleAreaFragment circleAreaFragment = new CircleAreaFragment();
                Bundle argsCircleArea = new Bundle();
                argsCircleArea.putString("email", email);
                argsCircleArea.putString("token", token);
                circleAreaFragment.setArguments(argsCircleArea);
                replaceFragment(circleAreaFragment);
                dialog.dismiss();
            }
        });

        polygonAreaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressedSound.start();
                PolygonAreaFragment polygonAreaFragment = new PolygonAreaFragment();
                Bundle argsPolygonArea = new Bundle();
                argsPolygonArea.putString("email", email);
                argsPolygonArea.putString("token", token);
                polygonAreaFragment.setArguments(argsPolygonArea);
                replaceFragment(polygonAreaFragment);
                dialog.dismiss();
            }
        });

        addChildToAreaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressedSound.start();
                AddChildToAreaFragment addChildToAreaFragment = new AddChildToAreaFragment();
                Bundle argsAddChildToArea = new Bundle();
                argsAddChildToArea.putString("email", email);
                argsAddChildToArea.putString("token", token);
                addChildToAreaFragment.setArguments(argsAddChildToArea);
                replaceFragment(addChildToAreaFragment);
                dialog.dismiss();

            }
        });

        viewAllAreas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressedSound.start();
                ViewAllAreaFragment viewAllAreaFragment = new ViewAllAreaFragment();
                Bundle argsViewAllArea = new Bundle();
                argsViewAllArea.putString("email", email);
                argsViewAllArea.putString("token", token);
                viewAllAreaFragment.setArguments(argsViewAllArea);
                replaceFragment(viewAllAreaFragment);
                dialog.dismiss();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void showBottomDialogHistory() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout_history);

        LinearLayout allLocLayout = dialog.findViewById(R.id.layoutAllLoc);
        LinearLayout fromDateLocLayout = dialog.findViewById(R.id.layoutFromDate);
        LinearLayout betweenDateLocLayout = dialog.findViewById(R.id.layoutBetweenDateAndNow);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        allLocLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressedSound.start();
                HistoryAllLocationsFragment historyAllLocationsFragment = new HistoryAllLocationsFragment();
                Bundle allLocArgs = new Bundle();
                allLocArgs.putString("email", email);
                allLocArgs.putString("token", token);
                historyAllLocationsFragment.setArguments(allLocArgs);
                replaceFragment(historyAllLocationsFragment);
                dialog.dismiss();
            }
        });

        fromDateLocLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressedSound.start();
                HistoryFromDateLocFragment historyFromDateLocFragment = new HistoryFromDateLocFragment();
                Bundle allLocFromDateArgs = new Bundle();
                allLocFromDateArgs.putString("email", email);
                allLocFromDateArgs.putString("token", token);
                historyFromDateLocFragment.setArguments(allLocFromDateArgs);
                replaceFragment(historyFromDateLocFragment);
                dialog.dismiss();
            }
        });

        betweenDateLocLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressedSound.start();
                HistoryBetweenDateAndNowLocFragment historyBetweenDateAndNowLocFragment = new HistoryBetweenDateAndNowLocFragment();
                Bundle allLocBetweenDateArgs = new Bundle();
                allLocBetweenDateArgs.putString("email", email);
                allLocBetweenDateArgs.putString("token", token);
                historyBetweenDateAndNowLocFragment.setArguments(allLocBetweenDateArgs);
                replaceFragment(historyBetweenDateAndNowLocFragment);
                dialog.dismiss();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

}