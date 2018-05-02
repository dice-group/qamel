package de.qa.activity;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.qa.fragment.QAFragment;
import de.qa.qa.dataextraction.CalendarExtraction;
import de.qa.qa.dataextraction.ContactsExtraction;

/**
 * Created by paramjot on 2/6/18.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private Context context;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                QAFragment tab1 = new QAFragment();
                return tab1;
            case 1:
                CalendarExtraction tab2 = new CalendarExtraction();
                return tab2;
            case 2:
                ContactsExtraction tab3 = new ContactsExtraction();
                return tab3;
            case 3:
                QAFragment tab4 = new QAFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
