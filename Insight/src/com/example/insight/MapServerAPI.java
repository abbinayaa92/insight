package com.example.insight;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class MapServerAPI extends AsyncTask<String, Void, Bitmap> {
	public static final String SERVER_URL = "http://encaladus.lucan.ddns.comp.nus.edu.sg/GIS_server/index.php";
	
	private Context context;
	private String message;
	private ProgressDialog dialog;
	
	private static final String LOG_TAG = "MapServerAPI";
	
    public MapServerAPI(Context context, String message) {
        this.context = context;
        this.message = message;
    }
    
    protected void onPreExecute() {
    	if (message != null) {
    		dialog = ProgressDialog.show(context,"",message);
    		dialog.setCancelable(true);
    		dialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface arg0) {
					cancel(true);
				}
    		});
    	}
    }
    
    protected Bitmap doInBackground(String... args) {
    	String url = SERVER_URL;
    	Bitmap bitmap = null;
    	try {
	    	if (!isCancelled()) {
	    		HttpClient client = AndroidHttpClient.newInstance("Android");
	    		// Uses POST protocol
	    		HttpPost request = new HttpPost(url);
	    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	    		nameValuePairs.add(new BasicNameValuePair("fp_key", args[0]));
				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                try {
                    HttpResponse response = client.execute(request);
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != HttpStatus.SC_OK) {
                        Log.w(LOG_TAG, "Error " + statusCode + " while retrieving bitmap from " + url);
                        return bitmap;
                    }

                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        InputStream inputStream = null;
                        try {
                        	inputStream = entity.getContent();
                        	FlushedInputStream is = new FlushedInputStream(inputStream);
                        	bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
                            is.close();
                        } 
                        finally {
                            if (inputStream != null) 
                            	inputStream.close();
                            
                            entity.consumeContent();
                        }
                    }
                } 
                catch (IOException e) {
                    request.abort();
                    Log.w(LOG_TAG,"I/O error while retrieving bitmap from " + url,e);
                } 
                catch (IllegalStateException e) {
                    request.abort();
                    Log.w(LOG_TAG,"Incorrect URL: " + url);
                } 
                catch (Exception e) {
                    request.abort();
                    Log.w(LOG_TAG,"Error while retrieving bitmap from " + url,e);
                } 
                finally {
                    if (client instanceof AndroidHttpClient) 
                        ((AndroidHttpClient) client).close();
                }
	    	}
    	}
    	catch (Exception e) {
    		Log.w(LOG_TAG,"Runtim error",e);
	    }
	    
    	return bitmap;
    }
    
    protected void onPostExecute(Bitmap bitmap) {
    	if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
    
    private class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) 
                        break;  // we reached EOF
                    else 
                        bytesSkipped = 1; // we read one byte
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
