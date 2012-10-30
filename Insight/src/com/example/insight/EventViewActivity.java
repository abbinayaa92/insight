package com.example.insight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.insight.EventForm.addTest;
import com.example.insight.datamodel.Event;
import com.example.insight.datamodel.Eventlist;
import com.example.insight.datamodel.InsightGlobalState;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventViewActivity extends Activity {

	private InsightGlobalState globalState;
	private Event event;
	private Eventlist signedup;
	private TextView title, desc, venue,date, time;
	private Button attendevent;
	Context context;
	JSONParser jsonParser = new JSONParser();
	private String url = "http://137.132.82.133/pg2/events_pop_add.php";
	private static final String TAG_SUCCESS = "success";
	Activity callingActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        context=this;
        callingActivity=this;
        title= (TextView) findViewById(R.id.EventNameinfo);
        desc= (TextView) findViewById(R.id.Descinfo);
        venue= (TextView) findViewById(R.id.Venueinfo);
        date= (TextView) findViewById(R.id.Dateinfo);
        time= (TextView) findViewById(R.id.Timeinfo);
        attendevent =(Button) findViewById(R.id.AttendEvent);
        globalState = (InsightGlobalState) getApplication();

		event = globalState.getEvents();
		signedup= globalState.getSignedup();
		title.setText(event.getTitle());
		venue.setText(event.getVenue());
		date.setText(event.getDate());
		desc.setText(event.getDescription());
		time.setText(event.getTime());
		
		for(int i=0;i<signedup.size();i++)
		{
			Log.d("singed up id",""+signedup.get(i).getId());
			if(signedup.get(i).getId() == event.getId())
			{
				Log.d("id matched","inside event");
				attendevent.setVisibility(View.GONE);
				break;
			}
			else
				attendevent.setVisibility(View.VISIBLE);
		}
		Log.d("event info",event.getTitle()+" "+event.getDescription() + " "+event.getDate()+ "" + event.getId());
		Log.d("user_id",globalState.getId());
		 attendevent.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	attend signupevent = new attend(context, callingActivity);
	        		signupevent.execute();
	            } 
	        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_event_view, menu);
        return true;
    }
    
    public class attend extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;

		public attend(Context context, Activity callingActivity) {
			this.context = context;
			this.callingActivity = callingActivity;
		}

		
		 protected String doInBackground(String... args) {
	          
	 
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            //put the appropriate textbox content instead of the actual strings entered here
	            params.add(new BasicNameValuePair("event_id", Integer.toString(event.getId()))); //put the text of the title textbox here instead of "teting testing events"
	            params.add(new BasicNameValuePair("user_id", globalState.getId()));
	            // getting JSON Object
	            // Note that create product url accepts POST method
	            JSONObject json = jsonParser.makeHttpRequest(url,
	                    "POST", params);
	 
	            // check log cat fro response
	          //  Log.d("Create Response", json.toString());
	            int success;
				try {
					success = json.getInt(TAG_SUCCESS);
					return Integer.toString(success);
					 
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "";
				}
	           
	 
	           
		}

		@Override
		protected void onPostExecute(String result) {
			
                if (result.equals("1")) {
                    // successfully created product
                  Log.d("result event signup","success");
                  Eventlist signupevents=globalState.getSignedup();
                  signupevents.addEvent(event);
                  globalState.setSignedup(signupevents);
                    // closing this screen
                    
                } else {
                    // failed to create product
                	Log.d("result event signup","failure");
                }
		}
	}
}
