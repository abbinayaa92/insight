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
import org.json.JSONException;
import org.json.JSONObject;

import com.example.insight.EventActivity.GetEventTask;
import com.example.insight.datamodel.Event;
import com.example.insight.datamodel.Friend;
import com.example.insight.datamodel.InsightGlobalState;
import com.example.insight.datamodel.FriendList;
import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class FriendActivity extends Activity {

	Button FriendButton;
	EditText friendSearch;
	ListView FriendList;
	Context context;
	Activity callingActivity;
	InsightGlobalState globalState;
	FriendList friendlist;
	ArrayList<Friend> contactList;
	ArrayList<String> contactNameList;
	ArrayList<Friend> filteredNameList = new ArrayList<Friend>();
	FriendListBaseAdapter friendadapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        callingActivity=this;
        setContentView(R.layout.activity_friend);
        FriendList =(ListView)findViewById(R.id.FriendList);
        friendSearch =(EditText)findViewById(R.id.FriendSearch);
        globalState=(InsightGlobalState)getApplication();

		  friendlist=globalState.getFriendlist();
	        contactList = friendlist.getFriendlist();
	        
	        friendadapter= new FriendListBaseAdapter(context, contactList);

					// Assign adapter to ListView
		    FriendList.setAdapter(friendadapter); 
		    
	    friendSearch.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int textLength2 = friendSearch.getText().length();
				filteredNameList.clear();
				for (int i = 0; i < contactList.size(); i++) {
					if (contactList.get(i).getName()!=null && (!contactList.get(i).getName().equals("")) && textLength2 <= contactList.get(i).getName().length()) {
						if (friendSearch.getText().toString().equalsIgnoreCase((String) contactList.get(i).getName().subSequence(0, textLength2))) {
							filteredNameList.add(contactList.get(i));
						}
					}
					else if(textLength2 <= contactList.get(i).getEmail().length())
					{
						if (friendSearch.getText().toString().equalsIgnoreCase((String) contactList.get(i).getEmail().subSequence(0, textLength2))) {
							filteredNameList.add(contactList.get(i));
						}
					}
				}
				friendadapter= new FriendListBaseAdapter(context, filteredNameList);
				FriendList.setAdapter(friendadapter); 
			}
		});
	    
	    FriendList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object o = FriendList.getItemAtPosition(position);
				Friend selectedFriend = (Friend) o;
				
				  Toast.makeText( context, "You have chosen: " + " " +
				  selectedFriend.getName() + " " +
				  selectedFriend.getEmail() + " " + selectedFriend.getPhone() + " " +
				 selectedFriend.getId() + " " + " " + selectedFriend.getFloor_id()+ " " + selectedFriend.getLat()+ " " + selectedFriend.getLon()+position + " " ,
				  Toast.LENGTH_LONG).show();
				 

				int friendId = selectedFriend.getId();
				Log.d("friend title",Integer.toString(friendId));
				String url = "http://137.132.82.133/pg2/users_read_ind.php?id=" + friendId;
				ProgressDialog dialog = new ProgressDialog(context);
				dialog.setMessage("Getting Friend Info...");
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
				getf getProjectTask = new getf(context, callingActivity, dialog);
				getProjectTask.execute(url);
			}
		});
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
            
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friend, menu);
        return true;
    }
    
    protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
    }
    


}
