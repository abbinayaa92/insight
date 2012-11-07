package com.example.insight;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.insight.EventForm.AttachmentBaseAdapter;
import com.example.insight.EventForm.addTest;
import com.example.insight.EventForm.AttachmentBaseAdapter.ViewHolder;
import com.example.insight.FriendActivity.getf;
import com.example.insight.datamodel.Event;
import com.example.insight.datamodel.Eventlist;
import com.example.insight.datamodel.Friend;
import com.example.insight.datamodel.FriendList;
import com.example.insight.datamodel.InsightGlobalState;
import com.example.insight.datamodel.Event.EventDateCompare;
import com.example.insight.datamodel.Event.EventLocCompare;
import com.example.insight.datamodel.Event.EventPopCompare;
import com.example.insight.datamodel.SignedFriends;
import com.google.gson.Gson;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
	private ListView signlist;
	private ImageView vid;
	ArrayList<String> multimedia=new ArrayList<String>();
	ArrayList<SignedFriends> friendlist= new ArrayList<SignedFriends>();
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
        signlist=(ListView)findViewById(R.id.attendees);
        globalState = (InsightGlobalState) getApplication();
        vid =(ImageView)findViewById(R.id.thumbnail);
		event = globalState.getEvents();
		signedup= globalState.getSignedup();
		title.setText(event.getTitle());
		venue.setText(event.getVenue());
		date.setText(event.getDate());
		desc.setText(event.getDescription());
		time.setText(event.getTime());
		friendlist= event.getFriend_id();
		Log.d("signed list", "size:"+friendlist.size());
		FriendAttachmentBaseAdapter adap= new FriendAttachmentBaseAdapter(context);
		signlist.setAdapter(adap);
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

    public class FriendAttachmentBaseAdapter extends BaseAdapter {

  	  private final LayoutInflater mInflater;

  	  public FriendAttachmentBaseAdapter(Context context) {
  	   mInflater = LayoutInflater.from(context);
  	  }

  	  
  	  public int getCount() {
  	   return friendlist.size();
  	  }

  	  
  	  public Object getItem(int position) {
  	   return friendlist.get(position);
  	  }

  	  
  	  public long getItemId(int position) {
  	   return position;
  	  }

  	  public View getView(final int position, View convertView, ViewGroup parent) {
  	   final ViewHolder holder;
  	  
  	   if (convertView == null) {
  	    convertView = mInflater.inflate(R.layout.singedlistadapter, null);
  	    holder = new ViewHolder();
  	    holder.friendname = (TextView) convertView.findViewById(R.id.signFriendName);
  	  
  	    convertView.setTag(holder);
  	   } else {
  	    holder = (ViewHolder) convertView.getTag();
  	   }
  	   holder.friendname.setText(friendlist.get(position).getFriend_email());

  	 holder.friendname.setOnClickListener(new View.OnClickListener() {
 	    
 	    public void onClick(View v) {
 	    	String url = "http://137.132.82.133/pg2/users_read_ind.php?id=" + friendlist.get(position).getFriend_id();
			ProgressDialog dialog = new ProgressDialog(context);
			dialog.setMessage("Getting Friend Info...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			getf getProjectTask = new getf(context, callingActivity, dialog);
			getProjectTask.execute(url);
 	    }
 	   });
 	   return convertView;
 	  }

  	  class ViewHolder {
  	   TextView friendname;
  	  }

  	 }
    
    public class getf extends AsyncTask<String, Void, String> {

    	private final Context context;
    	private final Activity callingActivity;
    	private final ProgressDialog dialog;

    	public getf(Context context, Activity callingActivity, ProgressDialog dialog) {
    		this.context = context;
    		this.callingActivity = callingActivity;
    		this.dialog = dialog;
    	}

    	@Override
    	protected void onPreExecute() {
    		if (dialog != null) {
    			if (!this.dialog.isShowing()) {
    				this.dialog.setMessage("Getting Friend Info...");
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
    			Log.d("get friend detail result", result.toString());
    			JSONObject resultJson = new JSONObject(result);
    			if (resultJson.getInt("success") ==1 ) {
    				JSONArray friendJson = new JSONArray(resultJson.getString("users"));
    				Gson gson = new Gson();
    				Friend friend1 = gson.fromJson(friendJson.getJSONObject(0).toString(), Friend.class);
    				InsightGlobalState globalState = (InsightGlobalState) callingActivity.getApplication();
    				FriendList list= globalState.getFriendlist();
    				for(int i=0;i<list.size();i++)
    				{
    					if(list.get(i).getId()==friend1.getId())
    					{
    						Log.d("in match",friend1.getEmail());
    						friend1.setName(list.get(i).getName());
    						friend1.setPhone(list.get(i).getPhone());
    					}
    				}
    				globalState.setFriends(friend1);

    					Intent eventViewIntent = new Intent(context, FriendViewActivity.class);
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


