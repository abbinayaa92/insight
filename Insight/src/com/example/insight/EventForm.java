package com.example.insight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class EventForm extends Activity {

	Button addevent;
	Context context;
	Activity callingActivity;
	JSONParser jsonParser = new JSONParser();
	private String url = "http://137.132.82.133/pg2/events_add.php";
	private static final String TAG_SUCCESS = "success";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_form);
        addevent=(Button)findViewById(R.id.AddEvent);
       
        addevent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	addTest newevent = new addTest(context, callingActivity);
        		newevent.execute();
            } 
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_form, menu);
        return true;
    }
    
    public class addTest extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;

		public addTest(Context context, Activity callingActivity) {
			this.context = context;
			this.callingActivity = callingActivity;
		}

		
		 protected String doInBackground(String... args) {
	          
	 
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            //put the appropriate textbox content instead of the actual strings entered here
	            params.add(new BasicNameValuePair("title", "testing testing events")); //put the text of the title textbox here instead of "teting testing events"
	            params.add(new BasicNameValuePair("description", "checking and testing"));
	            params.add(new BasicNameValuePair("date", "2013-02-11"));
	            params.add(new BasicNameValuePair("time", "05:00:00"));
	            params.add(new BasicNameValuePair("venue", "com1"));
	            //for coorx and coory need to call the location server to find coordinates of venue and add it here instead of the values entered
	            params.add(new BasicNameValuePair("coorx", "22.5"));
	            params.add(new BasicNameValuePair("coory", "46.8"));
	 
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
                  Log.d("result","success");
                    // closing this screen
                    
                } else {
                    // failed to create product
                	Log.d("result","failure");
                }
		}
	}
}
