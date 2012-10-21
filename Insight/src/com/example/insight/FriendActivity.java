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

import com.example.insight.EventActivity.GetEventTask;
import com.example.insight.datamodel.Event;
import com.example.insight.datamodel.Friend;
import com.example.insight.datamodel.InsightGlobalState;
import com.example.insight.datamodel.FriendList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
        contactNameList=new ArrayList<String>();
        for(int i=0;i< contactList.size();i++)
        {
        	contactNameList.add(contactList.get(i).getName());
        }
        
        
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
					if (textLength2 <= contactList.get(i).getName().length()) {
						if (friendSearch.getText().toString().equalsIgnoreCase((String) contactList.get(i).getName().subSequence(0, textLength2))) {
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
				  selectedFriend.getEmail() + " " + selectedFriend.getPhone() + " " +selectedFriend.getFriend_id() + " " +position + " " ,
				  Toast.LENGTH_LONG).show();
				 

				int eventId = selectedFriend.getFriend_id();
//				Log.d("event title",Integer.toString(eventId));
//				String url = "http://137.132.82.133/pg2/friends_read_ind.php?id=" + eventId;
//				ProgressDialog dialog = new ProgressDialog(context);
//				dialog.setMessage("Getting Event Info...");
//				dialog.setCancelable(false);
//				dialog.setCanceledOnTouchOutside(false);
//				dialog.show();
//				GetEventTask getProjectTask = new GetEventTask(context, callingActivity, dialog);
//				getProjectTask.execute(url);
			}
		});

            
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friend, menu);
        return true;
    }
    
    


}
