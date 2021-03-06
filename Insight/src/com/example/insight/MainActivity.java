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

import com.example.insight.datamodel.InsightGlobalState;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends Activity {

	EditText email;
	Button Login;
	private SharedPreferences prefs;
	private InsightGlobalState globalState;
	Activity callingActivity;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        globalState= (InsightGlobalState)getApplication();
        callingActivity=this;
        email= (EditText) findViewById(R.id.LoginEmail);
        Login =(Button) findViewById(R.id.LoginButton);
        
//        if(!prefs.getString("username", "").equals("") && isOAuthSuccessful())
//        {
//        	startActivity(new Intent().setClass(this, HomeActivity.class));
//        	callingActivity.finish();
//        }
        Login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String loginmail=email.getText().toString();
            	if(!(loginmail.equals("")))
            	{
            		globalState.setEmail(loginmail);
            		final Editor edit = prefs.edit();
        			edit.putString("username", loginmail);
        			edit.commit();
            	
            	if(isOAuthSuccessful())
            		startActivity(new Intent().setClass(v.getContext(), HomeActivity.class));
            	else
            		startActivity(new Intent().setClass(v.getContext(), RequestTokenActivity.class));
            	callingActivity.finish();
            	}
            	else
            		Log.d("error","empty login email");
            }
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
   
    
}
