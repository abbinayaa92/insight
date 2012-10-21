package com.example.insight;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.insight.EventForm.addTest;
import com.example.insight.datamodel.Friend;
import com.example.insight.datamodel.FriendList;
import com.example.insight.datamodel.InsightGlobalState;
import com.google.android.maps.GeoPoint;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class HomeActivity extends Activity {

	LocalActivityManager mLocalActivityManager;
	private SharedPreferences prefs;
	List<String> contactList;
	Activity currentActivity;
	private Context context;
	private Friend contact;
	private InsightGlobalState globalState;
	private ArrayList<Friend> contactlist;
	private FriendList friendlist;
	private int cont_ref=1;
	JSONParser jsonParser = new JSONParser();
	private String url = "http://137.132.82.133/pg2/users_add.php";
	private static final String TAG_SUCCESS = "success";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentActivity=this;
        context=this;
        globalState = (InsightGlobalState) getApplication();
        globalState.setCoorx(0);
        globalState.setCoory(0);
        contactlist = new ArrayList<Friend>();
        friendlist = new FriendList();
        TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
        mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(mLocalActivityManager);
        // Tab for Photos
        TabSpec eventspec = tabHost.newTabSpec("Events");
        // setting Title and Icon for the Tab
        eventspec.setIndicator("Events");
        //, getResources().getDrawable(R.drawable.icon_photos_tab));
        Intent eventIntent = new Intent(this, EventActivity.class);
        eventspec.setContent(eventIntent);
 
        // Tab for Songs
        TabSpec friendspec = tabHost.newTabSpec("Friends");
        friendspec.setIndicator("Friends");
        //, getResources().getDrawable(R.drawable.icon_songs_tab));
        Intent friendIntent = new Intent(this, FriendActivity.class);
        friendspec.setContent(friendIntent);
 
      
        // Adding all TabSpec to TabHost
        tabHost.addTab(eventspec); // Adding photos tab
        tabHost.addTab(friendspec); // Adding songs tab
        
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
    
    private class GetContacts extends AsyncTask<Context, String, ArrayList<Friend>> {
    	private Context context;
    	private Activity callingActivity;
    	private ProgressDialog dialog;

    	public GetContacts(Activity callingActivity, ProgressDialog dialog) {
    		this.callingActivity = callingActivity;
    		this.dialog = dialog;
    	}

    	@Override
    	protected void onPreExecute() {
    		if (!this.dialog.isShowing()) {
    			this.dialog.setMessage("Syncing contacts from google...");
    			this.dialog.show();
    			this.dialog.setCanceledOnTouchOutside(false);
    			this.dialog.setCancelable(false);
    		}
    	}
    	@Override
    	protected ArrayList<Friend> doInBackground(Context... params) {
    		 this.context = params[0];
    		String jsonOutput = "";
    		
            try {
            	jsonOutput = makeSecuredReq(C.GET_CONTACTS_FROM_GOOGLE_REQUEST,getConsumer(prefs));
             	JSONObject jsonResponse = new JSONObject(jsonOutput);
            	JSONObject m = (JSONObject)jsonResponse.get("feed");
            	JSONArray entries =(JSONArray)m.getJSONArray("entry");
            	Log.d("JSON entries",entries.toString());
            	for (int i=0 ; i<entries.length() ; i++) {
            		  contact= new Friend();
            		JSONObject entry = entries.getJSONObject(i);
            		JSONObject title = entry.getJSONObject("title");
            		JSONArray email=null;
            		JSONObject phone=null;
            		try
            		{
            		 email= entry.getJSONArray("gd$email");
            		 phone=entry.getJSONObject("gd$phoneNumber");
            		}
            		catch(Exception e)
            		{
            			Log.d("error","no phone or email");
            		}
            		
            		Log.d("contact info",entry.toString());
            		if(email!=null)
            		{
            			JSONObject email1 = email.getJSONObject(0);
            			contact.setEmail(email1.getString("address"));
            			contact.setName(title.getString("$t"));
            			if(phone!=null)
            				contact.setPhone(phone.getString("$t"));
            			
                		contactlist.add(contact);
            		}
            		
            		
            	}
            	
            	return contactlist;
    		} catch (Exception e) {
    			Log.e(C.TAG, "Error executing request",e);
    			Log.d("Error retrieving contacts : ", jsonOutput);
    			return null;
    		}
    		
    	}

    	@Override
    	protected void onPostExecute(ArrayList<Friend> result) {
    		if (dialog != null) {
    			if (this.dialog.isShowing()) {
    				this.dialog.dismiss();
    			}
    		}
    		
    		if(result!=null){
    		
    		friendlist.setFriendlist(result);
        	globalState.setFriendlist(friendlist);
    		}
    		else
    		Log.d("error contact list size 0","");
    		 addUser newevent = new addUser(context, currentActivity);
    			newevent.execute();
    	}
    }  
    
    public class addUser extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;

		public addUser(Context context, Activity callingActivity) {
			this.context = context;
			this.callingActivity = callingActivity;
		}

		
		 protected String doInBackground(String... args) {
	          
	 
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            //put the appropriate textbox content instead of the actual strings entered here
	            params.add(new BasicNameValuePair("email", "asdf@asdf.asdf")); //put the text of the title textbox here instead of "teting testing events"
	            params.add(new BasicNameValuePair("event_id", "0"));
	            params.add(new BasicNameValuePair("time", "00:00"));
	            FriendList Flist = globalState.getFriendlist();
	            List<Friend> listfriend = Flist.getFriendlist();
	            for(int i=0;i<listfriend.size();i++)
	            	params.add(new BasicNameValuePair("friends["+i+"]", listfriend.get(i).getEmail()));
	            //params.add(new BasicNameValuePair("friends[1]", "helo@abc.com"));
	            //for coorx and coory need to call the location server to find coordinates of venue and add it here instead of the values entered
	            params.add(new BasicNameValuePair("coorx", Integer.toString(globalState.getCoorx())));
	            params.add(new BasicNameValuePair("coory", Integer.toString(globalState.getCoory())));
	            
	            // getting JSON Object
	            // Note that create product url accepts POST method
	            JSONObject json = jsonParser.makeHttpRequest(url,"POST", params);
	 
	            // check log cat fro response
	          //  Log.d("Create Response", json.toString());
	            int success;
				try {
					Log.d("friend result",json.toString());
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
    

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.Sync:
			ProgressDialog dialog = new ProgressDialog(context);
			dialog.setMessage("Syncing contacts from google...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
    		new GetContacts(currentActivity,dialog).execute(context);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
        protected void onResume() {
    		// TODO Auto-generated method stub
    		super.onResume();
    		if (isOAuthSuccessful() && cont_ref==1) {
        		// OAuth successful, try getting the contacts
    			cont_ref=0;
    			ProgressDialog dialog = new ProgressDialog(context);
    			dialog.setMessage("Syncing contacts from google...");
    			dialog.setCancelable(false);
    			dialog.setCanceledOnTouchOutside(false);
    			dialog.show();
        		new GetContacts(currentActivity,dialog).execute(context);
        		
        	}
        	else {
        		
        	}
    	}
        
      
        private boolean isOAuthSuccessful() {
        	String token = prefs.getString(OAuth.OAUTH_TOKEN, null);
    		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, null);
    		if (token != null && secret != null)
    		{
    			Log.d("aoth token",token);
    			return true;
    		}
    		else 
    			return false;
        }
        
    	private OAuthConsumer getConsumer(SharedPreferences prefs) {
    		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
    		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
    		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(C.CONSUMER_KEY, C.CONSUMER_SECRET);
    		consumer.setTokenWithSecret(token, secret);
    		return consumer;
    	}
    	
    	private String makeSecuredReq(String url,OAuthConsumer consumer) throws Exception {
    		DefaultHttpClient httpclient = new DefaultHttpClient();
        	HttpGet request = new HttpGet(url);
        	Log.i(C.TAG,"Requesting URL : " + url);
        	consumer.sign(request);
        	HttpResponse response = httpclient.execute(request);
        	Log.i(C.TAG,"Statusline : " + response.getStatusLine());
        	InputStream data = response.getEntity().getContent();
        	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
            String responeLine;
            StringBuilder responseBuilder = new StringBuilder();
            while ((responeLine = bufferedReader.readLine()) != null) {
            	responseBuilder.append(responeLine);
            }
            Log.i(C.TAG,"Response : " + responseBuilder.toString());
            return responseBuilder.toString();
    	}	
}
