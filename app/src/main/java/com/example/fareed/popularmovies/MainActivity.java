package com.example.fareed.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    boolean masterDetail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfig);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        if (!isOnline()){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activty, new NoInternetFragment())
                    .commit();
        }
        if (savedInstanceState == null)
        {
            if (isOnline()) {
                if (findViewById(R.id.tablet_FramesContainer)!= null) {
                    masterDetail = true;
                    MoviesFragment moviesFragment = new MoviesFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("masterDetail", masterDetail);
                    moviesFragment.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.leftFrame, moviesFragment)
                            .commit();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.rightFrame, new MovieDetailsFragment())
                            .commit();
                }
                else {
                    masterDetail = false;
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.main_activty, new MoviesFragment())
                            .commit();
                }
            }
            else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activty, new NoInternetFragment())
                        .commit();
            }
        }
    }


    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

}
