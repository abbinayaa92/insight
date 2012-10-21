package com.example.insight;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FriendViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friend_view, menu);
        return true;
    }
}
