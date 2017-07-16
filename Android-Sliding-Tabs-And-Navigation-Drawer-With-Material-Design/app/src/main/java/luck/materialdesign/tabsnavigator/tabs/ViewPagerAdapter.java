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

    String tab1_text;
    String tab2_text;
    String tab3_text;
    String tab4_text;
    String tab5_text;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

        this.tab1_text = "";
        this.tab2_text = "";
        this.tab3_text = "";
        this.tab4_text = "";
        this.tab5_text = "";
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Tab1 tab1 = new Tab1();
                Bundle bundle1 = new Bundle();
                bundle1.putString("text", tab1_text);
                tab1.setArguments(bundle1);
                return tab1;

            case 1:
                Tab2 tab2 = new Tab2();
                Bundle bundle2 = new Bundle();
                bundle2.putString("text", tab2_text);
                tab2.setArguments(bundle2);
                return tab2;

            case 2:
                Tab3 tab3 = new Tab3();
                Bundle bundle3 = new Bundle();
                bundle3.putString("text", tab3_text);
                tab3.setArguments(bundle3);
                return tab3;

            case 3:
                Tab4 tab4 = new Tab4();
                Bundle bundle4 = new Bundle();
                bundle4.putString("text", tab4_text);
                tab4.setArguments(bundle4);
                return tab4;

            case 4:
                Tab5 tab5 = new Tab5();
                Bundle bundle5 = new Bundle();
                bundle5.putString("text", tab5_text);
                tab5.setArguments(bundle5);
                return tab5;

        }
        return new Tab1();
    }

    public void append_text(String text) {
        tab1_text = text+"1";
        tab2_text = text+"2";
        tab3_text = text+"3";
        tab4_text = text+"4";
        tab5_text = text+"5";
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