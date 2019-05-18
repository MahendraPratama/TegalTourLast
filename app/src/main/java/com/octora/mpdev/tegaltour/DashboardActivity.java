package com.octora.mpdev.tegaltour;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity {

    private ViewPager mPager;
    private SectionPageAdapter mSectPageAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mPager = (ViewPager) findViewById(R.id.tab_pager);
        SampleFragmentPagerAdapter pagerAdapter =
                new SampleFragmentPagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(pagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mPager);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }

    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter{
        private String tabTitles[] = new String[] { "Explore", "Tour Map", "Destination", "Account" };
        private int tabImage[] = new int[] {R.mipmap.ic_explore, R.mipmap.ic_tourmap, R.mipmap.ic_destination, R.mipmap.ic_account};

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position){
            final View v = LayoutInflater.from(getApplication()).inflate(R.layout.tab_header_custom_layout, null);
            TextView tv = (TextView) v.findViewById(R.id.tab_title_customLayout);
            ImageView iv = (ImageView) v.findViewById(R.id.tab_image_customLayout);
            tv.setText(tabTitles[position]);
            iv.setImageResource(tabImage[position]);

            return v;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    ExploreFragment exploreFragment = new ExploreFragment();
                    return exploreFragment;
                case 1 :
                    TourMapFragment tourMapFragment = new TourMapFragment();
                    return tourMapFragment;
                case 2 :
                    DestinationFragment destinationFragment = new DestinationFragment();
                    return destinationFragment;
                case 3 :
                    AccountFragment accountFragment = new AccountFragment();
                    return accountFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }


    class SectionPageAdapter extends FragmentPagerAdapter{
        public SectionPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    ExploreFragment exploreFragment = new ExploreFragment();
                    return exploreFragment;
                case 1 :
                    TourMapFragment tourMapFragment = new TourMapFragment();
                    return tourMapFragment;
                case 2 :
                    DestinationFragment destinationFragment = new DestinationFragment();
                    return destinationFragment;
                case 3 :
                    AccountFragment accountFragment = new AccountFragment();
                    return accountFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        public CharSequence getPageTitle(int position){
            switch (position){
                case 0 :
                    return "Explore";
                case 1 :
                    return "Tour Map";
                case 2 :
                    return "Destination";
                case 3 :
                    return "Account";
                default:
                    return null;
            }
        }
    }
}
