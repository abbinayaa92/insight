package com.example.insight;

import java.util.ArrayList;
import java.util.List;

import com.example.insight.datamodel.Friend;
import com.example.insight.datamodel.InsightGlobalState;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class FriendViewActivity extends MapActivity {

	TextView Name, Phone, Email;
	InsightGlobalState globalState;
	MapView mapView;
	private Context globalContext;
	private MapController mapController;
	private List<Overlay> mapOverlays;
	private MarkOverlay markOverlay;

	private Point indoorPosition;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalContext=this;
        setContentView(R.layout.activity_friend_view);
        Name=(TextView)findViewById(R.id.FriendName);
        Phone=(TextView)findViewById(R.id.Friendmobile);
        Email=(TextView)findViewById(R.id.Friendemail);
        
        globalState =(InsightGlobalState)getApplication();
        
        mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(17);

		mapOverlays = mapView.getOverlays();
		markOverlay = new MarkOverlay(getResources().getDrawable(R.drawable.dot));
		mapOverlays.add(markOverlay);
		mapOverlays.add(new MapGestureDetectorOverlay());

		
        Friend friend= globalState.getFriends();
        Log.d("latitude",""+friend.getLat());
        indoorPosition=new Point(friend.getCoorx(),friend.getCoory());
        Log.d("longitude",""+friend.getLon());
        Name.setText(friend.getName());
        Phone.setText(friend.getPhone());
        Email.setText(friend.getEmail());
        Floor floor= new Floor();
        floor.id = friend.getFloor_id();
        Log.d("floor id", friend.getFloor_id());
		floor.name = "com1_L1_37";
		floor.location = new GeoPoint(indoorPosition.x, indoorPosition.y);
		mapController.animateTo(floor.location);
		mapView.invalidate();
    	Mark mark = new Mark(floor);
		markOverlay.removeAll();
		markOverlay.addMark(mark);
		mark.showInfo();
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friend_view, menu);
        return true;
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private class MapGestureDetectorOverlay extends Overlay {
		private final GestureDetector gestureDetector;

		public MapGestureDetectorOverlay() {
			gestureDetector = new GestureDetector(globalContext, new MyGestureListener());
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			return gestureDetector.onTouchEvent(event);
		}
	}
	
	// user gesture listener
		private class MyGestureListener extends SimpleOnGestureListener {
			@Override
			public void onShowPress(MotionEvent e) {
				GeoPoint p = mapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());
				markOverlay.onTap(p, mapView);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				return mapController.zoomIn();
			}
		}

		private class MarkOverlay extends ItemizedOverlay<OverlayItem> {
			private final ArrayList<Mark> items = new ArrayList<Mark>();

			public MarkOverlay(Drawable marker) {
				super(boundCenter(marker));
				populate();
			}

			@Override
			protected OverlayItem createItem(int i) {
				return items.get(i);
			}

			@Override
			public int size() {
				return items.size();
			}

			public void addMark(Mark item) {
				items.add(item);
				setLastFocusedIndex(-1);
				populate();

				mapController.animateTo(item.getPoint());
			}

			public void removeAll() {
				for (int i = 0; i < items.size(); i++)
					items.get(i).removeView();
				items.clear();
				setLastFocusedIndex(-1);
				populate();
			}

			@Override
			protected boolean onTap(int i) {
				Mark mark = items.get(i);
				mark.showInfo();
				return true;
			}
		}

		private class Mark extends OverlayItem {
			private final Floor floor;
			private MarkView infoView;

			// private String markId;

			public Mark(Floor floor) {
				super(floor.location, floor.name, "Long Press to see the detailed indoor position");
				this.floor = floor;
			}

			/*
			 * public Coordinate getPosition() { return position; }
			 */

			public Floor getFloor() {
				return floor;
			}

			public void showInfo() {
				if (infoView == null) {
					infoView = new MarkView(this);
					MapView.LayoutParams mapParams = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, this.getPoint(), MapView.LayoutParams.BOTTOM_CENTER);
					mapParams.mode = MapView.LayoutParams.MODE_MAP;

					mapView.addView(infoView, mapParams);
				} else {
					infoView.setVisibility(View.VISIBLE);
					infoView.bringToFront();
				}

				mapView.invalidate();
			}

			public void hideView() {
				if (infoView != null)
					infoView.setVisibility(View.GONE);
			}

			public void removeView() {
				if (infoView != null)
					mapView.removeView(infoView);
			}

		}

		private class MarkView extends FrameLayout {
			private Mark mark;

			public MarkView(Mark item) {
				super(globalContext);
				mark = item;

				setPadding(10, 0, 10, 10);
				LinearLayout layout = new LinearLayout(globalContext);
				layout.setVisibility(VISIBLE);

				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = inflater.inflate(R.layout.markview, layout);
				TextView titleView = (TextView) v.findViewById(R.id.mark_title);
				TextView snippetView = (TextView) v.findViewById(R.id.mark_snippet);
				titleView.setText(mark.getTitle());
				snippetView.setText("Long press for more information");

				ImageView close = (ImageView) v.findViewById(R.id.mark_close_button);
				close.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mark.hideView();
					}
				});

				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.NO_GRAVITY;
				addView(layout, params);

				setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						v.bringToFront();
					}
				});

				setOnLongClickListener(new OnLongClickListener() {
					public boolean onLongClick(View v) {
						Intent i = new Intent(globalContext, FloorAppActivity.class);
						i.putExtra("floorID", mark.getFloor().id);
						i.putExtra("userPositionX", indoorPosition.x);
						i.putExtra("userPositionY", indoorPosition.y);

						startActivity(i);

						return true;
					}
				});
			}
		}

		private class Floor {
			public String id;
			public String name;

			public GeoPoint location;
		}
	}
