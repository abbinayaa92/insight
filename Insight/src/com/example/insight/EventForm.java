package com.example.insight;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.insight.datepicker.DateSlider;
import com.example.insight.datepicker.DefaultDateSlider;

public class EventForm extends Activity {

	Button addevent;
	EditText title, desc, venue,date;
	TimePicker time;
	Context context;
	Activity callingActivity;
	JSONParser jsonParser = new JSONParser();
	private String url = "http://137.132.82.133/pg2/events_add.php";
	private static final String TAG_SUCCESS = "success";
	static final int DEFAULTDATESELECTOR_ID = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context =this;
        callingActivity =this;
        setContentView(R.layout.event_form);
        addevent=(Button)findViewById(R.id.AddEvent);
        title= (EditText) findViewById(R.id.newEventTitle);
        desc= (EditText) findViewById(R.id.newEventDesc);
        venue= (EditText) findViewById(R.id.newEventVenue);
        date= (EditText) findViewById(R.id.newEventDate);
        time= (TimePicker) findViewById(R.id.newEventTime);
       
        addevent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	addTest newevent = new addTest(context, callingActivity);
        		newevent.execute();
            } 
        });
        
        date.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DEFAULTDATESELECTOR_ID);
			}

		});

    }
    
    private final DateSlider.OnDateSetListener mDateSetListener = new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			date.setText(selectedDate.get(Calendar.YEAR) + "-" + (selectedDate.get(Calendar.MONTH) + 1) + "-" + selectedDate.get(Calendar.DATE));
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DEFAULTDATESELECTOR_ID:
			final Calendar c = Calendar.getInstance();
			return new DefaultDateSlider(this, mDateSetListener, c);
		}
		return null;
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
	            params.add(new BasicNameValuePair("title", title.getText().toString())); //put the text of the title textbox here instead of "teting testing events"
	            params.add(new BasicNameValuePair("description", desc.getText().toString()));
	            params.add(new BasicNameValuePair("date", date.getText().toString()));
	            params.add(new BasicNameValuePair("time", "00:00"));
	            params.add(new BasicNameValuePair("venue", venue.getText().toString()));
	            //for coorx and coory need to call the location server to find coordinates of venue and add it here instead of the values entered
	            params.add(new BasicNameValuePair("coorx", "0"));
	            params.add(new BasicNameValuePair("coory", "0"));
	            params.add(new BasicNameValuePair("floor_id", "COM1_L2.jpg")); // set the actual floor id instead
	 
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
                Intent eventsViewIntent = new Intent(context, HomeActivity.class);
				callingActivity.startActivity(eventsViewIntent);
				callingActivity.finish();
		}
	}
}
