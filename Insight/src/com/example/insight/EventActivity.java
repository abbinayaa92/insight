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
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;



public class EventActivity extends Activity {

	private Context context;
	private Activity callingActivity;
	JSONParser jsonParser = new JSONParser();
	private String url = "http://137.132.82.133/pg2/events_update.php";
	private static final String TAG_SUCCESS = "success";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
//        
//        JSONObject json1 = new JSONObject();
//        try {
//			json1.put("index", 1);
//			json1.put("text", "check hello");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        addTest createProjectTask = new addTest(context, callingActivity);
		createProjectTask.execute();
        
        
        String url = "http://137.132.82.133/pg2/events_read.php";
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		Test projectListTask = new Test(context, callingActivity);
		projectListTask.execute(url);
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
				
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
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
	            params.add(new BasicNameValuePair("title", "test event update"));
	            params.add(new BasicNameValuePair("description", "testing"));
	            params.add(new BasicNameValuePair("date", "2013-02-11"));
	            params.add(new BasicNameValuePair("time", "05:00:00"));
	            params.add(new BasicNameValuePair("venue", "com1"));
	            params.add(new BasicNameValuePair("coorx", "22.5"));
	            params.add(new BasicNameValuePair("coory", "46.8"));
	            params.add(new BasicNameValuePair("id", "3"));
	 
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
