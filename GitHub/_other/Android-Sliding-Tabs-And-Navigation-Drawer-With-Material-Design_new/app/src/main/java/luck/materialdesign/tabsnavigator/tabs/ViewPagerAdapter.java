package luck.materialdesign.tabsnavigator.tabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Edwin on 15/02/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    Tab1 tab1;
    Tab2 tab2;
    Tab3 tab3;
    Tab4 tab4;
    Tab5 tab5;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

        tab1 = new Tab1();
        tab2 = new Tab2();
        tab3 = new Tab3();
        tab4 = new Tab4();
        tab5 = new Tab5();
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return tab1;

            case 1:
                return tab2;

            case 2:
                return tab3;

            case 3:
                return tab4;

            case 4:
                return tab5;

        }
        return new Tab1();
    }

    public void append_text(String text) {
        if(tab1 != null)    tab1.append_text(text);
        if(tab2 != null)    tab2.append_text(text);
        if(tab3 != null)    tab3.append_text(text);
        if(tab4 != null)    tab4.append_text(text);
        if(tab5 != null)    tab5.append_text(text);
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}