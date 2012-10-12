package com.example.insight;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EventForm extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_form);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_form, menu);
        return true;
    }
}
