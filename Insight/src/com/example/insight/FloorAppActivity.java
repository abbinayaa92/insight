package com.example.insight;

import java.io.IOException;
import java.io.InputStream;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class FloorAppActivity extends Activity {
	private Context globalContext;
	
	private Point displaySize;
	private int displayWidth;
	private int displayHeight;
	
	private String floorID;
	private int positionX;
	private int positionY;
	
	private Bitmap floorPlan;
	private Bitmap userMark;
	
	private FloorView floorView;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        globalContext = this;
        
		floorID = getIntent().getStringExtra("floorID");
		positionX = getIntent().getIntExtra("userPositionX",-1);
		positionY = getIntent().getIntExtra("userPositionY",-1);
		
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		displaySize = new Point();
        
        try {
        	// GetSize is not available in older models of Android
        	display.getSize(displaySize);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
        	displaySize.x = display.getWidth();
        	displaySize.y = display.getHeight();
        }

		try {
	        InputStream source = getAssets().open(floorID);
	        floorPlan = BitmapFactory.decodeStream(source);
	        
	        displayWidth = Math.min(displaySize.x, floorPlan.getWidth()); 		
			displayHeight = Math.min(displaySize.y, floorPlan.getHeight());
			
			userMark = BitmapFactory.decodeResource(getResources(),R.drawable.star);
			
			floorView = new FloorView(this);
	        setContentView(floorView);
	    } 
		catch (IOException e) {
			MapServerAPI server = new MapServerAPI(globalContext,"Retrieving floor plan. Please wait...") {
    			@Override
    			protected void onPostExecute(Bitmap bitmap) {
    				super.onPostExecute(bitmap);
    				
    				if (bitmap == null)
    					Toast.makeText(globalContext, "Error in retrieving floor plan!", Toast.LENGTH_LONG).show();
    				else {
    					floorPlan = bitmap;
    					
    					displayWidth = Math.min(displaySize.x, floorPlan.getWidth()); 		
    					displayHeight = Math.min(displaySize.x, floorPlan.getHeight());
    					
    					userMark = BitmapFactory.decodeResource(getResources(),R.drawable.star);
    					
    					floorView = new FloorView(globalContext);
    			        setContentView(floorView);
    				}
    			}
			};
			server.execute(floorID);
	    }
    }
    
//    @Override
//	public void onResume() {
//		super.onResume();	
//		registerReceiver(broadcastReceiver,new IntentFilter("FingerPrint_LOCATION_UPDATE"));
//	}
//	
//	@Override
//	public void onPause() {
//		super.onPause();
//		unregisterReceiver(broadcastReceiver);
//	}
//	
//	// listen for user location change
//	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//		    String result = intent.getStringExtra("location");
//		    Toast.makeText(context,result,Toast.LENGTH_LONG).show();
//		    
//		    String floorId = intent.getStringExtra("floorID");
//		    int userLocationX = intent.getIntExtra("userPositionX",-1);
//			int userLocationY = intent.getIntExtra("userPositionY",-1);
//			
//			if(floorId.equals(floorID)){
//				positionX = userLocationX;
//				positionY = userLocationY;
//				
//				floorView.invalidate();
//			}
//		}
//	};
//    
    private class FloorView extends View {
		private Rect displayRect; //rect we display to
		private Rect scrollRect; //rect we scroll over our bitmap with
		
		private int scrollRectX = 0; //current left location of scroll rect
		private int scrollRectY = 0; //current top location of scroll rect
		private float scrollByX = 0; //x amount to scroll by
		private float scrollByY = 0; //y amount to scroll by
		private float startX = 0; //track x from one ACTION_MOVE to the next
		private float startY = 0; //track y from one ACTION_MOVE to the next
		
		public FloorView(Context context) {
			super(context);

			// Destination rect for our main canvas draw. It never changes.
			displayRect = new Rect(0, 0, displayWidth, displayHeight);
			// Scroll rect: this will be used to 'scroll around' over the 
			// bitmap in memory.
			if (positionX + displayWidth / 2 > floorPlan.getWidth()) 
				scrollRectX = floorPlan.getWidth() - displayWidth;
			else 
				scrollRectX = positionX - displayWidth / 2;
			if (scrollRectX < 0)
				scrollRectX = 0;
			
			if (positionY + displayHeight / 2 > floorPlan.getHeight()) 
				scrollRectY = floorPlan.getHeight() - displayHeight;
			else 
				scrollRectY = positionY - displayHeight / 2;
			if (scrollRectY < 0)
				scrollRectY = 0;
			
			scrollRect = new Rect(scrollRectX, scrollRectY, displayWidth, displayHeight);
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// Remember our initial down event location.
					startX = event.getRawX();
					startY = event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE:
					float x = event.getRawX();
					float y = event.getRawY();
					// Calculate move update. This will happen many times
					// during the course of a single movement gesture.
					scrollByX = x - startX; //move update x increment
					scrollByY = y - startY; //move update y increment
					startX = x; //reset initial values to latest
					startY = y;
					invalidate(); //force a redraw
					break;
			}
			return true; //done with this event so consume it
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// Our move updates are calculated in ACTION_MOVE in the opposite direction
			// from how we want to move the scroll rect. Think of this as dragging to
			// the left being the same as sliding the scroll rect to the right.
			int newScrollRectX = scrollRectX - (int)scrollByX;
			int newScrollRectY = scrollRectY - (int)scrollByY;

			// Don't scroll off the left or right edges of the bitmap.
			if (newScrollRectX < 0)
				newScrollRectX = 0;
			else if (newScrollRectX > (floorPlan.getWidth() - displayWidth))
				newScrollRectX = (floorPlan.getWidth() - displayWidth);

			// Don't scroll off the top or bottom edges of the bitmap.
			if (newScrollRectY < 0)
				newScrollRectY = 0;
			else if (newScrollRectY > (floorPlan.getHeight() - displayHeight))
				newScrollRectY = (floorPlan.getHeight() - displayHeight);

			// We have our updated scroll rect coordinates, set them and draw.
			scrollRect.set(newScrollRectX, newScrollRectY, 
				newScrollRectX + displayWidth, newScrollRectY + displayHeight);
			Paint paint = new Paint();
			canvas.drawBitmap(floorPlan, scrollRect, displayRect, paint);
			
			// Update user position
		    if (positionX >= newScrollRectX && positionX - newScrollRectX <= displayWidth
					&& positionY >= newScrollRectY && positionY - newScrollRectY <= displayHeight) 
				canvas.drawBitmap(userMark,positionX-newScrollRectX-userMark.getWidth()/2,positionY-newScrollRectY-userMark.getHeight()/2,paint);

				// Reset current scroll coordinates to reflect the latest updates, 
			// so we can repeat this update process.
			scrollRectX = newScrollRectX;
			scrollRectY = newScrollRectY;
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			// Cache our new dimensions; we'll need them for drawing.
			displayWidth = Math.min(w, floorPlan.getWidth()); 		
			displayHeight = Math.min(h, floorPlan.getHeight());
					
			// Destination rect for our main canvas draw.
			displayRect = new Rect(0, 0, displayWidth, displayHeight);
			// Scroll rect: this will be used to 'scroll around' over the
			// bitmap in memory.
			if (positionX + displayWidth / 2 > floorPlan.getWidth()) 
				scrollRectX = floorPlan.getWidth() - displayWidth;
			else 
				scrollRectX = positionX - displayWidth / 2;
			if (scrollRectX < 0)
				scrollRectX = 0;
			
			if (positionY + displayHeight / 2 > floorPlan.getHeight()) 
				scrollRectY = floorPlan.getHeight() - displayHeight;
			else 
				scrollRectY = positionY - displayHeight / 2;
			if (scrollRectY < 0)
				scrollRectY = 0;
			scrollRect = new Rect(scrollRectX, scrollRectY, displayWidth, displayHeight);

			super.onSizeChanged(w, h, oldw, oldh);
		}
	}
}