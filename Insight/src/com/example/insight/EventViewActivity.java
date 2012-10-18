package com.example.insight;

import com.example.insight.datamodel.Event;
import com.example.insight.datamodel.InsightGlobalState;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class EventViewActivity extends Activity {

	private InsightGlobalState globalState;
	private Event event;
	private TextView title, desc, venue,date, time;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        
        title= (TextView) findViewById(R.id.EventNameinfo);
        desc= (TextView) findViewById(R.id.Descinfo);
        venue= (TextView) findViewById(R.id.Venueinfo);
        date= (TextView) findViewById(R.id.Dateinfo);
        time= (TextView) findViewById(R.id.Timeinfo);
        
        globalState = (InsightGlobalState) getApplication();

		event = globalState.getEvents();
		title.setText(event.getTitle());
		venue.setText(event.getVenue());
		date.setText(event.getDate());
		desc.setText(event.getDescription());
		time.setText(event.getTime());
		
		Log.d("event info",event.getTitle()+" "+event.getDescription() + " "+event.getDate());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_event_view, menu);
        return true;
    }
}
