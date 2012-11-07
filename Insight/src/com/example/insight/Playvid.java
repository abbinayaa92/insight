package com.example.insight;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.net.Uri;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Playvid extends Activity {

	String urlpath;
	Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playvid);
        
        VideoView vid;
        ImageView img;
        context=this;
            vid=(VideoView)findViewById(R.id.video);
            img =(ImageView)findViewById(R.id.image);
           // urlpath="http://137.132.82.133/pg2/uploads/VID-20121104-00003.3GP";
            Bundle extras = getIntent().getExtras();
            urlpath=extras.getString("url");
            Log.d("url path in plavid",urlpath);
            if(urlpath.endsWith(".jpg") || urlpath.endsWith(".png") || urlpath.endsWith(".gif") || urlpath.endsWith(".bmp")|| urlpath.endsWith(".tif"))
            {
            	vid.setVisibility(View.GONE);
            	img.setVisibility(View.VISIBLE);
            	new DownloadImageTask((ImageView)  img).execute(urlpath);
            }
            else if(urlpath.endsWith(".mp4") || urlpath.endsWith(".3GP") || urlpath.endsWith(".MP4") || urlpath.endsWith(".3gp"))
            {
            	img.setVisibility(View.GONE);
            	vid.setVisibility(View.VISIBLE);
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            
            MediaController mc=new MediaController(this);
            mc.setMediaPlayer(vid);
            
            vid.setMediaController(mc);
            vid.setVideoURI((Uri.parse(urlpath)));
            vid.requestFocus();
            vid.start();
            }
            else
            	Toast.makeText(context, "File format is not supported", Toast.LENGTH_LONG).show();
            
    
            /* 
             * void playvideo(String url) 
    {
         String  link=url;
         Log.e("url",link);
         view1 = (VideoView) findViewById(R.id.myVideoView); 
          getWindow().setFormat(PixelFormat.TRANSLUCENT);

          MediaController mc = new MediaController(this); 
          mc.setMediaPlayer(view1); 

          view1.setMediaController(mc); 
          view1.setVideoURI(Uri.parse(link)); 
          view1.requestFocus(); 
          view1.start();

     }
             */ /*
            String urlpath="http://137.132.82.133/pg2/uploads/20121027_135348.mp4";
            Intent i= new Intent (Intent.ACTION_VIEW);
            i.setData(Uri.parse(urlpath));
            startActivity(i); */
        }

  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_playvid, menu);
        return true;
    }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }


/*    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        showProgressDialog();
    }*/

    protected void onPostExecute(Bitmap result) {
        //pDlg.dismiss();
        bmImage.setImageBitmap(result);
    }}
}
