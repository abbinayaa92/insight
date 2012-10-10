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

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class FriendActivity extends Activity {

	Button FriendButton;
	ListView FriendList;
	private SharedPreferences prefs;
	List<String> contactList;
	Activity currentActivity;
	private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentActivity=this;
        context=this;
        FriendButton= (Button)findViewById(R.id.ImportButton);
        FriendList =(ListView)findViewById(R.id.FriendList);
        FriendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             startActivity(new Intent().setClass(v.getContext(), RequestTokenActivity.class));
            }
        });
            
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friend, menu);
        return true;
    }
    

private class GetContacts extends AsyncTask<Context, String, List<String>> {
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
	protected List<String> doInBackground(Context... params) {
		 this.context = params[0];
		String jsonOutput = "";
		contactList=new ArrayList<String>();
        try {
        	jsonOutput = makeSecuredReq(C.GET_CONTACTS_FROM_GOOGLE_REQUEST,getConsumer(prefs));
         	JSONObject jsonResponse = new JSONObject(jsonOutput);
        	JSONObject m = (JSONObject)jsonResponse.get("feed");
        	JSONArray entries =(JSONArray)m.getJSONArray("entry");
        	for (int i=0 ; i<entries.length() ; i++) {
        		JSONObject entry = entries.getJSONObject(i);
        		JSONObject title = entry.getJSONObject("title");
        		//Log.d("JSON title",title.getString("$t"));
        		//contactList.add("A");
        		//if (title.getString("$t")!=null && title.getString("$t").length()>0) {
        			contactList.add(title.getString("$t"));
        		//}
        		//Log.d("list name", contactList.get(i));
        	}
        	return contactList;
		} catch (Exception e) {
			Log.e(C.TAG, "Error executing request",e);
			Log.d("Error retrieving contacts : ", jsonOutput);
			return null;
		}
		
	}

	@Override
	protected void onPostExecute(List<String> result) {
		if (dialog != null) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
		}
		
		if(result!=null){
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
					  android.R.layout.simple_list_item_1, android.R.id.text1, contactList);

					// Assign adapter to ListView
			FriendList.setAdapter(adapter); 
			
		for(int i=0;i<result.size();i++)
        	Log.d("Contact",result.get(i));
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
