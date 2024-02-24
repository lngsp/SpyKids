package ro.spykids.clientapp.ui.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ro.spykids.clientapp.ui.fragment.common.LoginTabFragment;
import ro.spykids.clientapp.ui.fragment.common.RegisterTabFragment;

public class AuthAdapter extends FragmentPagerAdapter {
    private Context context;
    int totalTabs;

    public AuthAdapter(FragmentManager fragmentManager, Context context, int totalTabs){
        super(fragmentManager);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    public Fragment getItem(int position){
        switch (position){
            case 0:
                LoginTabFragment loginTabFragment = new LoginTabFragment();
                return loginTabFragment;
            case 1:
                RegisterTabFragment registerTabFragment = new RegisterTabFragment();
                return registerTabFragment;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return totalTabs;
    }
}
