package ro.spykids.clientapp.ui.activity.common;

import static ro.spykids.clientapp.util.Constants.CAMERA_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.pojo.User;
import ro.spykids.clientapp.ui.activity.child.ChildMainActivity;
import ro.spykids.clientapp.ui.activity.parent.ParentMainActivity;
import ro.spykids.clientapp.ui.fragment.child.GenerateQRFragment;
import ro.spykids.clientapp.ui.fragment.parent.NewChildFragment;
import ro.spykids.clientapp.util.TokenManager;

public class BondActivity extends AppCompatActivity {

    private FrameLayout mFrameLayout;
    private User user;
    private String token;
    private Button btnHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_bond);
        mFrameLayout = this.findViewById(R.id.frame_layout);
        btnHome = this.findViewById(R.id.button_home);

        user = (User) getIntent().getSerializableExtra("User");
        token = getIntent().getStringExtra("token");

        Bundle bundle = new Bundle();
        bundle.putString("email", user.getEmail());
        bundle.putString("token", token);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            return;
        }

        if (user.getType().equals("PARENT")) {
            NewChildFragment fragment = new NewChildFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(mFrameLayout.getId(), fragment).commit();
        } else {
            GenerateQRFragment fragment = new GenerateQRFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(mFrameLayout.getId(), fragment).commit();
        }

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(">>>>> USER TYPE : " + user.getType());
                if (user.getType().equals("PARENT")) {
                    Intent intent = new Intent(BondActivity.this, ParentMainActivity.class);
                    intent.putExtra("email", user.getEmail());
                    intent.putExtra("token", token);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(BondActivity.this, ChildMainActivity.class);
                    intent.putExtra("email", user.getEmail());
                    intent.putExtra("token", token);
                    startActivity(intent);
                }
            }
        });

    }

}
