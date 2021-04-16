package myt.music.app_pro;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import myt.music.app_pro.R;


public class Analytics extends Application {



    private Tracker mTracker;




    synchronized public Tracker getDefaultTracker() {

        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // Setting mTracker to Analytics Tracker declared in our xml Folder
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }

        return mTracker;


    }
}