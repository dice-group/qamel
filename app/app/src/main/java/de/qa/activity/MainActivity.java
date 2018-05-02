package de.qa.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.qa.R;
import de.qa.fragment.QAFragment;
import de.qa.misc.Utils;
import de.qa.qa.dataextraction.DataExtraction;
import de.qa.qa.triplestore.TripleStore;
import de.qa.synchronizer.OfflineDataManager;

public class MainActivity extends AppCompatActivity implements OfflineDataManager.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TripleStore mTripleStore;
    private DataExtraction dataExtraction;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                dataExtraction=new DataExtraction(context);
            }
        });
        thread.start();

       /* Intent i = new Intent(MainActivity.this, OfflineQuestionAnswerer.class);
        startActivity(i);*/


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("QA"));
        tabLayout.addTab(tabLayout.newTab().setText("Calendar"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.addTab(tabLayout.newTab().setText("Location"));
   //   tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        if (!Utils.isOffline(this)) {
            new OfflineDataManager().update(this, this);
           // new OfflineQuestionAnswerer();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new QAFragment())
                .commit();
    }

    @Override
    public void onUpdateFailed() {

    }

    @Override
    public void onUpdateCompleted(String newRevision) {

    }
}




