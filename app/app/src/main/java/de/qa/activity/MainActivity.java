package de.qa.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.qa.fragment.QAFragment;
import de.qa.misc.Utils;
import de.qa.qa.triplestore.TripleStore;
import de.qa.synchronizer.OfflineDataManager;

public class MainActivity extends AppCompatActivity implements OfflineDataManager.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TripleStore mTripleStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.isOffline(this)) {
            new OfflineDataManager().update(this, this);
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
