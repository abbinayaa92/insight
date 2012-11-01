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
import java.util.Calendar;
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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.insight.datepicker.DateSlider;
import com.example.insight.datepicker.DefaultDateSlider;


public class EventForm extends Activity {

	String selected_venue;
	Button addevent,Upload;
	Spinner venue;
	EditText title, desc, date;
	TimePicker time;
	Context context;
	Activity callingActivity;
	JSONParser jsonParser = new JSONParser();
	ListView attachments;
	public static ArrayList<String> attachment_list = new ArrayList<String>();
	public static ArrayList<String> url_list = new ArrayList<String>();
	private String url = "http://137.132.82.133/pg2/events_add.php";
	private static final String TAG_SUCCESS = "success";
	static final int DEFAULTDATESELECTOR_ID = 0;
	String[][] Venue_coord={{"COM 1, Foyer Area","460","150"},{"0","0","0"},{"0","0","0"}};
	String xcoord, ycoord;
	private AttachmentBaseAdapter adapter_attachlist;
	InsightGlobalState obj;
	private static final int SELECT_VIDEO = 2;
	String selectedPath = "";
	String eventid="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context =this;
        callingActivity =this;
        setContentView(R.layout.event_form);
        addevent=(Button)findViewById(R.id.AddEvent);
        title= (EditText) findViewById(R.id.newEventTitle);
        desc= (EditText) findViewById(R.id.newEventDesc);
        venue= (Spinner) findViewById(R.id.newEventVenue);
        date= (EditText) findViewById(R.id.newEventDate);
        time= (TimePicker) findViewById(R.id.newEventTime);
        attachments=(ListView)findViewById(R.id.attachments);
        
        obj=(InsightGlobalState)getApplication();
        
       
        
        /* Author : Puneet	
         *  Date : 18/10/2012	
         *  Description : Populating the EventVenue spinner
         */ 
       
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Venue_Array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        venue.setAdapter(adapter);
        
        adapter_attachlist = new AttachmentBaseAdapter(context);
        attachments.setAdapter(adapter_attachlist);
		
        /* ends */
        
        context=this;
        Upload=(Button)findViewById(R.id.Upload);
        
        Upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	System.out.println("BEFORE");
                //startActivity(new Intent(context,UploadVideo.class));
            	openGalleryVideo();
            	
            	
            	//attachment_list.add(obj.getCurrentAttachment());
            } 
        });
        
        
        addevent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//            	for(int i=0;i<attachment_list.size();i++)
//            	{
//            		UploadVid doUpload = new UploadVid(context, callingActivity,attachment_list.get(i));
//                    doUpload.execute();
//            	}
            	addTest newevent = new addTest(context, callingActivity);
        		newevent.execute();
            } 
        });
        
        date.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DEFAULTDATESELECTOR_ID);
			}

		});

    }
    
    public void openGalleryVideo(){
    	System.out.println("Open Gallery");
    	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    	intent.setType("*/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(Intent.createChooser(intent,"Select Video "), SELECT_VIDEO);
   }
    
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
                System.out.println("I am inside");
                attachment_list.add(selectedPath);
                attachments.setAdapter(adapter_attachlist);
                
                
                
            }
 
        }
    }
    
    private final DateSlider.OnDateSetListener mDateSetListener = new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			date.setText(selectedDate.get(Calendar.YEAR) + "-" + (selectedDate.get(Calendar.MONTH) + 1) + "-" + selectedDate.get(Calendar.DATE));
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DEFAULTDATESELECTOR_ID:
			final Calendar c = Calendar.getInstance();
			return new DefaultDateSlider(this, mDateSetListener, c);
		}
		return null;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_form, menu);
        return true;
    }
    
    public class addTest extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;

		public addTest(Context context, Activity callingActivity) {
			this.context = context;
			this.callingActivity = callingActivity;
		}

		
		 protected String doInBackground(String... args) {
	          
	 
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            //put the appropriate textbox content instead of the actual strings entered here
	            params.add(new BasicNameValuePair("title", title.getText().toString())); //put the text of the title textbox here instead of "teting testing events"
	            params.add(new BasicNameValuePair("description", desc.getText().toString()));
	            params.add(new BasicNameValuePair("date", date.getText().toString()));
	            params.add(new BasicNameValuePair("time", "00:00"));
	            params.add(new BasicNameValuePair("venue", venue.getSelectedItem().toString()));
	            //for coorx and coory need to call the location server to find coordinates of venue and add it here instead of the values entered
	            // get the coordinates for the event
	            for (int i=0;i<Venue_coord.length;i++)
	            {
	            	if(Venue_coord[i][0].equals(venue.getSelectedItem().toString()))
	            	{
	            		xcoord=Venue_coord[i][1];
	            		ycoord=Venue_coord[i][2];
	            	}
	            }
	            
	            
	            params.add(new BasicNameValuePair("coorx", xcoord));
	            params.add(new BasicNameValuePair("coory", ycoord));
	 
	            // getting JSON Object
	            // Note that create product url accepts POST method
	            JSONObject json = jsonParser.makeHttpRequest(url,
	                    "POST", params);
	 
	            // check log cat fro response
	          //  Log.d("Create Response", json.toString());
	            int success;
				try {
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
                	try {
						JSONObject resultJson = new JSONObject(result);
						eventid = resultJson.getString("message");
	                	Log.d("eventid",eventid);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	
                  Log.d("result","success");
                    // closing this screen
                    
                } else {
                    // failed to create product
                	Log.d("result","failure");
                }
                Intent eventsViewIntent = new Intent(context, HomeActivity.class);
				callingActivity.startActivity(eventsViewIntent);
				callingActivity.finish();
		}
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
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
        String path;
        String responseFromServer = "";
        public String urlString = "http://137.132.82.133/pg2/mult_upload.php";

    	public UploadVid(Context context, Activity callingActivity, String path) {
    		this.context = context;
    		this.callingActivity = callingActivity;
    		this.path=path;
    	}

    	
    	 protected String doInBackground(String... args) {
    		 String str="";
     
    		 try
    	        {
    	         //------------------ CLIENT REQUEST
    	        FileInputStream fileInputStream = new FileInputStream(new File(path) );
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
    	         System.out.println("uploadedfile" +"\t"+ path + "\t" + lineEnd);
    	         dos.writeBytes("Content-Disposition: form-data; name=uploadedfile;filename="+ path + lineEnd);
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


  public class AttachmentBaseAdapter extends BaseAdapter {

	  private final LayoutInflater mInflater;

	  public AttachmentBaseAdapter(Context context) {
	   mInflater = LayoutInflater.from(context);
	  }

	  
	  public int getCount() {
	   return attachment_list.size();
	  }

	  
	  public Object getItem(int position) {
	   return attachment_list.get(position);
	  }

	  
	  public long getItemId(int position) {
	   return position;
	  }

	  public View getView(final int position, View convertView, ViewGroup parent) {
	   final ViewHolder holder;
	  
	   if (convertView == null) {
	    convertView = mInflater.inflate(R.layout.base_layout, null);
	    holder = new ViewHolder();
	    holder.videoname = (TextView) convertView.findViewById(R.id.tv_videolistname);
	    holder.removeButton = (ImageButton) convertView.findViewById(R.id.remove_button);
	  
	    convertView.setTag(holder);
	   } else {
	    holder = (ViewHolder) convertView.getTag();
	   }
	   holder.videoname.setText(attachment_list.get(position));

	   holder.removeButton.setOnClickListener(new View.OnClickListener() {
	    
	    public void onClick(View v) {
	    	attachment_list.remove(position); // remove when button is clicked
	     AttachmentBaseAdapter.this.notifyDataSetChanged();
	    }
	   });
	   return convertView;
	  }

	  class ViewHolder {
	   TextView videoname;
	   ImageButton removeButton;
	  }

	 }
}
