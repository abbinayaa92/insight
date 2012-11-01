package com.example.insight;
import com.example.insight.datamodel.InsightGlobalState;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;

public class UploadVideo extends Activity {
	private static final int SELECT_VIDEO = 2;
    String selectedPath = "";
    public String urlString = "http://137.132.82.133/pg2/videos_upload.php";
    Context context;
    Activity callingActivity;
    InsightGlobalState obj ;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	System.out.println("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadvideo);
        context=this;
        callingActivity=this;
        obj = (InsightGlobalState)getApplication();
        Log.d("obj",obj.toString());
        openGalleryVideo();
        
    }
 
    public void openGalleryVideo(){
    	System.out.println("Open Gallery");
    	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    	intent.setType("*/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(Intent.createChooser(intent,"Select Video "), SELECT_VIDEO);
   }
 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
 
        if (resultCode == RESULT_OK) {
 
            if (requestCode == SELECT_VIDEO)
            {
                System.out.println("SELECT_VIDEO");
                Uri selectedVideoUri = data.getData();
                selectedPath = getPath(selectedVideoUri);
                System.out.println("SELECTED PATH : " + selectedPath.toString());
                System.out.println("SELECTED VIDEO URI : " + selectedVideoUri.toString());
                System.out.println("SELECT_VIDEO Path : " + selectedPath);
                String temp=selectedPath.toString();
                Log.d("temp",temp);
                
                
                //UploadVid doUpload = new UploadVid(context, callingActivity);
                //doUpload.execute();
                
            }
 
        }
    }
 
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    
    public class UploadVid extends AsyncTask<String, Void, String> {
    	private final Context context;
    	private final Activity callingActivity;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;
        String responseFromServer = "";
        public String urlString = "http://137.132.82.133/pg2/mult_upload.php";

    	public UploadVid(Context context, Activity callingActivity) {
    		this.context = context;
    		this.callingActivity = callingActivity;
    	}

    	
    	 protected String doInBackground(String... args) {
    		 String str="";
     
    		 try
    	        {
    	         //------------------ CLIENT REQUEST
    	        FileInputStream fileInputStream = new FileInputStream(new File(selectedPath) );
    	         // open a URL connection to the Servlet
    	         URL url = new URL(urlString);
    	         // Open a HTTP connection to the URL
    	         conn = (HttpURLConnection) url.openConnection();
    	         // Allow Inputs
    	         conn.setDoInput(true);
    	         // Allow Outputs
    	         conn.setDoOutput(true);
    	         // Don't use a cached copy.
    	         conn.setUseCaches(false);
    	         // Use a post method.
    	         conn.setRequestMethod("POST");
    	         conn.setRequestProperty("Connection", "Keep-Alive");
    	         conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
    	         dos = new DataOutputStream( conn.getOutputStream() );
    	         dos.writeBytes(twoHyphens + boundary + lineEnd);
    	         System.out.println("uploadedfile" +"\t"+ selectedPath + "\t" + lineEnd);
    	         dos.writeBytes("Content-Disposition: form-data; name=uploadedfile;filename="+ selectedPath + lineEnd);
    	         dos.writeBytes(lineEnd);
    	         // create a buffer of maximum size
    	         bytesAvailable = fileInputStream.available();
    	         bufferSize = Math.min(bytesAvailable, maxBufferSize);
    	         buffer = new byte[bufferSize];
    	         // read file and write it into form...
    	         bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    	         while (bytesRead > 0)
    	         {
    	          dos.write(buffer, 0, bufferSize);
    	          bytesAvailable = fileInputStream.available();
    	          bufferSize = Math.min(bytesAvailable, maxBufferSize);
    	          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    	         }
    	         // send multipart form data necesssary after file data...
    	         dos.writeBytes(lineEnd);
    	         dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    	         // close streams
    	         Log.e("Debug","File is written");
    	         fileInputStream.close();
    	         dos.flush();
    	         dos.close();
    	        }
    	        catch (MalformedURLException ex)
    	        {
    	             Log.d("Debug", "error: " + ex.getMessage(), ex);
    	        }
    	        catch (IOException ioe)
    	        {
    	             Log.d("Debug", "error: " + ioe.getMessage(), ioe);
    	        }

  		   try {
  	              inStream = new DataInputStream ( conn.getInputStream() );
  	             
  	 
  	              while (( str = inStream.readLine()) != null)
  	              {
  	                   Log.e("Debug","Server Response "+str);
  	              }
  	              inStream.close();
  	              
  	 
  	        }
  	        catch (IOException ioex){
  	             Log.e("Debug", "error: " + ioex.getMessage(), ioex);
  	        }
  		 return str;
    	}

    	@Override
    	protected void onPostExecute(String result) {
    		
    }
    }
}

   

 /*   private void doFileUpload(){
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "rn";
        String twoHyphens = "--";
        String boundary =  "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;
        String responseFromServer = "";
        String urlString = "http://137.132.82.133/pg2/videos_upload.php";
        try
        {
         //------------------ CLIENT REQUEST
        FileInputStream fileInputStream = new FileInputStream(new File(selectedPath) );
         // open a URL connection to the Servlet
         URL url = new URL(urlString);
         // Open a HTTP connection to the URL
         conn = (HttpURLConnection) url.openConnection();
         // Allow Inputs
         conn.setDoInput(true);
         // Allow Outputs
         conn.setDoOutput(true);
         // Don't use a cached copy.
         conn.setUseCaches(false);
         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
         dos = new DataOutputStream( conn.getOutputStream() );
         dos.writeBytes(twoHyphens + boundary + lineEnd);
         dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"\" + selectedPath + \"\"" + lineEnd);
         dos.writeBytes(lineEnd);
         // create a buffer of maximum size
         bytesAvailable = fileInputStream.available();
         bufferSize = Math.min(bytesAvailable, maxBufferSize);
         buffer = new byte[bufferSize];
         // read file and write it into form...
         bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         while (bytesRead > 0)
         {
          dos.write(buffer, 0, bufferSize);
          bytesAvailable = fileInputStream.available();
          bufferSize = Math.min(bytesAvailable, maxBufferSize);
          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         }
         // send multipart form data necesssary after file data...
         dos.writeBytes(lineEnd);
         dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
         // close streams
         Log.e("Debug","File is written");
         fileInputStream.close();
         dos.flush();
         dos.close();
        }
        catch (MalformedURLException ex)
        {
             Log.e("Debug", "error: " + ex.getMessage(), ex);
        }
        catch (IOException ioe)
        {
             Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }
        //------------------ read the SERVER RESPONSE
        try {
              inStream = new DataInputStream ( conn.getInputStream() );
              String str;
 
              while (( str = inStream.readLine()) != null)
              {
                   Log.e("Debug","Server Response "+str);
              }
              inStream.close();
 
        }
        catch (IOException ioex){
             Log.e("Debug", "error: " + ioex.getMessage(), ioex);
        }
      */