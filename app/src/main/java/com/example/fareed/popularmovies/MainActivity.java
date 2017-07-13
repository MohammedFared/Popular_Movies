package com.example.fareed.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {
    String TAG = "MAINACTIVITYLOG";
    boolean masterDetail = false;

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfig);
        Log.d(TAG, "onCreate: ");
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (!isOnline()) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.main_activty, new NoInternetFragment())
//                    .commit();
//        } else
            if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: savedInstanceState == null");
            if (findViewById(R.id.tablet_FramesContainer) != null) { //tablet mode
                masterDetail = true;
                MoviesFragment moviesFragment = new MoviesFragment();
                Bundle args = new Bundle();
                args.putBoolean("masterDetail", masterDetail);
                moviesFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.leftFrame, moviesFragment)
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.rightFrame, new MovieDetailsFragment())
                        .commit();
            } else {
                masterDetail = false;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.flContent, new MoviesFragment())
                        .commit();
            }
        } else {
            Log.d(TAG, "onCreate: savedInstanceState != null");
        }
    }


    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

}
