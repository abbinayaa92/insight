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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class FriendActivity extends Activity {

	Button FriendButton;
	ListView FriendList;
	Context context;
	InsightGlobalState globalState;
	FriendList friendlist;
	ArrayList<Friend> contactList;
	ArrayList<String> contactNameList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_friend);
        FriendList =(ListView)findViewById(R.id.FriendList);
        globalState=(InsightGlobalState)getApplication();
        friendlist=globalState.getFriendlist();
        contactList = friendlist.getFriendlist();
        contactNameList=new ArrayList<String>();
        for(int i=0;i< contactList.size();i++)
        {
        	contactNameList.add(contactList.get(i).getName());
        }
        
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,  android.R.layout.simple_list_item_1, android.R.id.text1, contactNameList);

				// Assign adapter to ListView
	    FriendList.setAdapter(adapter); 
            
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friend, menu);
        return true;
    }
    


}
