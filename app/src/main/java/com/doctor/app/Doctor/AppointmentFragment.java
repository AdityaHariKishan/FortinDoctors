package com.doctor.app.Doctor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doctor.app.R;

public class AppointmentFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    View view;
    Context context;
    String TAG="AppointmentFragment";
    private static final String[] pageTitle = {"Current", "Pending","History"};

    public AppointmentFragment() {

    }

    public static AppointmentFragment newInstance() {
        AppointmentFragment fragment = new AppointmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_appointment, container, false);
        context=getActivity();

        tabLayout = (TabLayout) view.findViewById(R.id.tab);
        viewPager = (ViewPager) view.findViewById(R.id.vp);
        final ViewPagerAdapter pagerAdapter=new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new WizardPageChangeListener());
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        return view;
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private int WIZARD_PAGES_COUNT = 3;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
                return new CurrentFragment();
            else if(position==1)
                return new UpcomingFragment();
            else
                return new HistoryFragment();
        }

        @Override
        public int getCount() {
            return WIZARD_PAGES_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitle[position];
        }

    }

    private class WizardPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int position) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

        }
    }

}
