package com.example.insight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.insight.EventForm.addTest;
import com.example.insight.datamodel.Event;
import com.example.insight.datamodel.Eventlist;
import com.example.insight.datamodel.InsightGlobalState;
import com.example.insight.datamodel.Event.EventDateCompare;
import com.example.insight.datamodel.Event.EventLocCompare;
import com.example.insight.datamodel.Event.EventPopCompare;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class EventViewActivity extends Activity {

	private InsightGlobalState globalState;
	private Event event;
	private Eventlist signedup;
	private TextView title, desc, venue,date, time;
	private Button attendevent;
	private AlertDialog alert;
	private ImageView vid;
	ArrayList<String> multimedia=new ArrayList<String>();
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
        vid =(ImageView)findViewById(R.id.thumbnail);
		event = globalState.getEvents();
		signedup= globalState.getSignedup();
		title.setText(event.getTitle());
		venue.setText(event.getVenue());
		date.setText(event.getDate());
		desc.setText(event.getDescription());
		time.setText(event.getTime());
		vid.setVisibility(View.GONE);
		
		for(int i=0;i<event.getMult().size();i++)
		{
			vid.setVisibility(View.VISIBLE);
			Log.d("inside mul loop","video url"+event.getMult().get(i).getMult());
			multimedia.add(event.getMult().get(i).getMult());
		}

		final CharSequence[] items = multimedia.toArray(new CharSequence[multimedia.size()]);
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Choose File:");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch (item) {
					default:
						String urlpath=multimedia.get(item);
						Log.d("url path",urlpath);
//						 Intent i= new Intent (Intent.ACTION_VIEW);
//						 i.setData(Uri.parse(urlpath));
//						 startActivity(i);
						 Intent eventViewIntent = new Intent(context, Playvid.class);
						 eventViewIntent.putExtra("url", urlpath);
	    				 startActivity(eventViewIntent);
						 break;
					}
					
				}
			});
			alert = builder.create();
			
			 vid.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
							alert.show();
					}
				});
			
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
                  Toast.makeText(context, "You have successfully signed up for the event", Toast.LENGTH_LONG).show();
                    // closing this screen
                    
                } else {
                    // failed to create product
                	Log.d("result event signup","failure");
                }
		}
	}
}
