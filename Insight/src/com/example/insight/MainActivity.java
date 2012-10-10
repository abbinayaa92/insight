package com.example.insight;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends Activity {

	LocalActivityManager mLocalActivityManager;
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
        mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(mLocalActivityManager);
        // Tab for Photos
        TabSpec eventspec = tabHost.newTabSpec("Events");
        // setting Title and Icon for the Tab
        eventspec.setIndicator("Events");
        //, getResources().getDrawable(R.drawable.icon_photos_tab));
        Intent eventIntent = new Intent(this, EventActivity.class);
        eventspec.setContent(eventIntent);
 
        // Tab for Songs
        TabSpec friendspec = tabHost.newTabSpec("Friends");
        friendspec.setIndicator("Friends");
        //, getResources().getDrawable(R.drawable.icon_songs_tab));
        Intent friendIntent = new Intent(this, FriendActivity.class);
        friendspec.setContent(friendIntent);
 
      
        // Adding all TabSpec to TabHost
        tabHost.addTab(eventspec); // Adding photos tab
        tabHost.addTab(friendspec); // Adding songs tab
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        try {
            mLocalActivityManager.dispatchResume();
        } catch (Exception e) {}
    }
}
