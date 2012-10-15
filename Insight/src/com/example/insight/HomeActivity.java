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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.insight.datamodel.Friend;
import com.example.insight.datamodel.FriendList;
import com.example.insight.datamodel.InsightGlobalState;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.TabHost;
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentActivity=this;
        context=this;
      
        globalState = (InsightGlobalState) getApplication();
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
    	}
    }    
        protected void onResume() {
    		// TODO Auto-generated method stub
    		super.onResume();
    		if (isOAuthSuccessful()) {
        		// OAuth successful, try getting the contacts
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