
package com.example.insight;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

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
import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
