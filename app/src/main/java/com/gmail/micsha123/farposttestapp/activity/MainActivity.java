package com.gmail.micsha123.farposttestapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.gmail.micsha123.farposttestapp.fragment.MainFragment;
import com.gmail.micsha123.farposttestapp.R;
/** Host activity for MainFragment */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /** Toolbar for implementing material design*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /** Progressbar from task */
        progress = (ProgressBar) findViewById(R.id.progress_bar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }
    /** Method show/hide spinning throbber */
    public void showProgressBar(boolean show){
        if(show){
            progress.setVisibility(ProgressBar.VISIBLE);
        } else{
            progress.setVisibility(ProgressBar.INVISIBLE);
        }
    }
    /** Method returns true, if throbber shown on the screen */
    public boolean isProgressbarVisible(){
        return progress.isShown();
    }
}
