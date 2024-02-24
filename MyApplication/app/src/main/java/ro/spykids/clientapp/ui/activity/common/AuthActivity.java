package ro.spykids.clientapp.ui.activity.common;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import ro.spykids.clientapp.ui.adapter.AuthAdapter;

import ro.spykids.clientapp.R;

public class AuthActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_auth);

        tabLayout = findViewById(R.id.tabLayoutAuth);
        viewPager = findViewById(R.id.viewPagerAuth);

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Register"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final AuthAdapter authAdapter = new AuthAdapter(getSupportFragmentManager(), this, tabLayout.getTabCount());
        viewPager.setAdapter(authAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }
}