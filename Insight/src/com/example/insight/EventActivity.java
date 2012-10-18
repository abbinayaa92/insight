
package com.example.insight;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.insight.datamodel.Event;
import com.example.insight.datamodel.Eventlist;
import com.example.insight.datamodel.InsightGlobalState;
import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class EventActivity extends Activity {

	private Context context;
	private Button Create;
	private ListView eventListView;
	private InsightGlobalState globalState;
	private ArrayList<Event> events = new ArrayList<Event>();
	private Activity callingActivity;
	private eventListBaseAdapter eventlistba;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        
        context = this; 
        callingActivity = this;
        globalState = (InsightGlobalState) getApplication();
        Create=(Button)findViewById(R.id.create_event);
        eventListView=(ListView)findViewById(R.id.EventList);
        
        registerReceiver(broadcastReceiver, new IntentFilter("FingerPrint_LOCATION_UPDATE"));
        LocationManager myManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location myLocation = myManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		GeoPoint myPosition;
		if (myLocation == null)
			myPosition = new GeoPoint(1294949, 103773838); // default COM1
															// location
		else
			myPosition = new GeoPoint((int) (myLocation.getLatitude() * 1E6), (int) (myLocation.getLongitude() * 1E6));
		
        Create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(context,EventForm.class));
            } 
        });
       
        
        
        String url = "http://137.132.82.133/pg2/events_read.php";
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		Test projectListTask = new Test(context, callingActivity);
		projectListTask.execute(url);
		
		eventListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object o = eventListView.getItemAtPosition(position);
				Event selectedEvent = (Event) o;
				/*
				 * Toast.makeText( cont, "You have chosen: " + " " +
				 * selectedProject.getProject_name() + " " +
				 * selectedProject.getProject_id() + " " + position + " " +
				 * globalState
				 * .getProjectList().getProjects().get(position).getLeader_id(),
				 * Toast.LENGTH_LONG).show();
				 */

				int eventId = selectedEvent.getId();
				Log.d("event title",Integer.toString(eventId));
				String url = "http://137.132.82.133/pg2/events_read_ind.php?id=" + eventId;
				ProgressDialog dialog = new ProgressDialog(context);
				dialog.setMessage("Getting Event Info...");
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
				GetEventTask getProjectTask = new GetEventTask(context, callingActivity, dialog);
				getProjectTask.execute(url);
			}
		});

	      
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_event, menu);
        return true;
    }

    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.setting:
			startActivity(new Intent(context, FingerPrintActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(broadcastReceiver, new IntentFilter("FingerPrint_LOCATION_UPDATE"));
    }
    
    public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onDestroy() {
		stopService(new Intent(this.getApplicationContext(), FingerPrintService.class));
		SharedPreferences config = getSharedPreferences("FingerPrint", MODE_PRIVATE);
		SharedPreferences.Editor editor = config.edit();
		editor.putBoolean("started", false);
		editor.commit();
		super.onDestroy();
	}
    
 // listen for user location change
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra("location");
			Toast.makeText(context, result, Toast.LENGTH_LONG).show();
			Log.d("result from server", result);

			// WRITE CODES HERE
			// Hint 1: You need to populate the attributes of the floor object
			// with data received from broadcast
			

			// Hint 2: You need to populate the indoorPosition with data
			// received from broadcast
			int coorx = intent.getIntExtra("coorX", 0);
			int coory = intent.getIntExtra("coorY", 0);
			Log.d("positions x", Integer.toString(coorx));
			Log.d("positions y", Integer.toString(coory));

		}
	};
	
    public class Test extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;

		public Test(Context context, Activity callingActivity) {
			this.context = context;
			this.callingActivity = callingActivity;
		}


		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				JSONObject resultJson = new JSONObject(result);
				Log.d("Test", resultJson.toString());
				Gson gson = new Gson();
				Eventlist eventsContainer = gson.fromJson(result, Eventlist.class);
				globalState.setEventlist(eventsContainer);
				events = eventsContainer.getEvents();
				eventlistba = new eventListBaseAdapter(context, events);
				eventListView.setAdapter(eventlistba);
				
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
	}
    
    
    public class GetEventTask extends AsyncTask<String, Void, String> {

    	private final Context context;
    	private final Activity callingActivity;
    	private final ProgressDialog dialog;

    	public GetEventTask(Context context, Activity callingActivity, ProgressDialog dialog) {
    		this.context = context;
    		this.callingActivity = callingActivity;
    		this.dialog = dialog;
    	}

    	@Override
    	protected void onPreExecute() {
    		if (dialog != null) {
    			if (!this.dialog.isShowing()) {
    				this.dialog.setMessage("Getting Event Info...");
    				this.dialog.setCancelable(false);
    				this.dialog.setCanceledOnTouchOutside(false);
    				this.dialog.show();
    			}
    		}
    	}

    	@Override
    	protected String doInBackground(String... urls) {
    		String response = "";
    		for (String url : urls) {
    			DefaultHttpClient client = new DefaultHttpClient();
    			HttpGet httpGet = new HttpGet(url);
    			try {
    				HttpResponse execute = client.execute(httpGet);
    				InputStream content = execute.getEntity().getContent();

    				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
    				String s = "";
    				while ((s = buffer.readLine()) != null) {
    					response += s;
    				}

    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    		return response;
    	}

    	@Override
    	protected void onPostExecute(String result) {
    		try {
    			if (dialog != null && this.dialog.isShowing()) {
    				this.dialog.dismiss();
    			}
    		} catch (Exception e) {
    		}
    		try {
    			Log.d("get event result", result.toString());
    			JSONObject resultJson = new JSONObject(result);
    			if (resultJson.getInt("success") ==1 ) {
    				JSONArray eventJson = new JSONArray(resultJson.getString("events"));
    				Gson gson = new Gson();
    				Event event = gson.fromJson(eventJson.getJSONObject(0).toString(), Event.class);
    				InsightGlobalState globalState = (InsightGlobalState) callingActivity.getApplication();
    				globalState.setEvents(event);

    					Intent eventViewIntent = new Intent(context, EventViewActivity.class);
    					callingActivity.startActivity(eventViewIntent);
    					//callingActivity.finish();

    			} else {
    			}
    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
    	}
    }

    
	
}
