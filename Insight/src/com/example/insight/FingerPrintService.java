package com.example.insight;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.example.insight.core.Distance;
import com.example.insight.core.EDistance;
import com.example.insight.location.*;

public class FingerPrintService extends Service {
	private static final String SERVER = "http://encaladus.lucan.ddns.comp.nus.edu.sg:8080";
	private List<ScanResult> WifiList;
	private WifiManager myWifiManager;
	WifiReceiver myWifiReceiver;
	public Timer refreshTimer = new Timer();
	private int collectedSamples = 1;
	Distance distance = null;
	private int sampleSize;
	private String mapsf = "";
	HashMap<String, ArrayList<String>> levelMap = new HashMap<String, ArrayList<String>>();
	HashMap<String, String> levelMap2 = new HashMap<String, String>();
	HashMap<String, String> levelMapTemp = new HashMap<String, String>();

	private SharedPreferences config;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		myWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (false == myWifiManager.isWifiEnabled()) {
			myWifiManager.setWifiEnabled(true);
		}
		myWifiReceiver = new WifiReceiver();
		registerReceiver(myWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		config = getSharedPreferences("FingerPrint", MODE_PRIVATE);
		int index = config.getInt("modelId", 0);

		if (config.getBoolean("tbServer", false)) {
			String[] mapArr = config.getString("map", "").split("/");
			mapsf = mapArr[mapArr.length - 1];
		} else {
			mapsf = config.getString("map", "");
		}

		String coorFile = config.getString("coor", "");
		sampleSize = config.getInt("sampleSize", 3);

		if (distance == null) {
			if (index == 1) {
				distance = new EuclideanDistance(mapsf, sampleSize, -130, false, coorFile);
			}
			if (index == 0) {
				distance = new HyperbolicEuclideanDistance(mapsf, sampleSize, -130, false, coorFile);
			}
		} else {
			distance.setSampleSize(sampleSize);
		}

		myWifiManager.startScan();

		return START_STICKY;
	}

	@Override
	public void onDestroy() {

		collectedSamples = 0;
		levelMap.clear();
		levelMap2.clear();

		unregisterReceiver(myWifiReceiver);

		super.onDestroy();
	}

	private String getTimeStamp() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE);
	}

	private void writeToFile(String myStringBuilder) {
		// TODO Auto-generated method stub
		File path = Environment.getExternalStorageDirectory();

		if (true) {
			// for writing to file
			try {
				BufferedWriter f = new BufferedWriter(new FileWriter(path + "/download/DEBUG_LOG" + ".txt", true));
				Long time = System.currentTimeMillis();
				f.write("NUMBER" + myStringBuilder + "\n\n");
				f.flush();
				f.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	class WifiReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context c, Intent i) {

			// getting scan information
			// if(bStart.isEnabled())
			// return;
			WifiList = myWifiManager.getScanResults();
			writeToFile(WifiList.toString());

			if (WifiList != null) {
				try {
					collectedSamples++;

					Iterator<ScanResult> resIter = WifiList.iterator();

					while (resIter.hasNext()) {
						ScanResult res = resIter.next();

						if (res.SSID.equals("NUS") || res.SSID.equals("NUSOPEN")) {
							ArrayList<String> scanl = levelMap.get(res.BSSID);
							String scanl2 = levelMap2.get(res.BSSID);

							if (scanl == null) {
								scanl = new ArrayList<String>();
								scanl2 = new String();
							}

							scanl.add(String.valueOf(res.level));
							levelMap.put(res.BSSID, scanl);
							scanl2 += String.valueOf(res.level) + ",";
							levelMap2.put(res.BSSID, scanl2);
							Log.d("Check", "id: " + res.BSSID + " values: " + scanl2);
						}
					}

					if (collectedSamples % sampleSize == 0) {
						config = getSharedPreferences("FingerPrint", MODE_PRIVATE);

						if (config.getBoolean("tbServer", false)) {
							levelMapTemp = levelMap2;
							Log.d("Critical", "" + levelMap2.size());
							new GetFromServer((HashMap<String, String>) levelMap2.clone()).execute(levelMap2);
						}

						else {
							EDistance retura[] = distance.getLocation(levelMap);

							Log.e("retura Size: ", retura.length + "");
							StringBuilder build = new StringBuilder();

							build.append("Location at time " + getTimeStamp() + " for Map " + mapsf + "\n");
							for (int j = 0; j < retura.length; j++) {
								if (retura[j] != null)
									build.append(j + ") " + retura[j] + "\n");

							}

							Intent intent = new Intent("LOCATION_UPDATE");
							intent.putExtra("location", build.toString());
							sendBroadcast(intent);
						}

						levelMap.clear();
						levelMap2.clear();
					}

				} catch (Exception e) {
					e.printStackTrace();
					Log.e(" App", e.toString());
				}

			}

			// schedule next scan
			refreshTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					myWifiManager.startScan();
				}
			}, 500);
		}
	}

	// OPTIONAL: MODIFY THE GetFromServer CLASS BASED ON RESULT TYPE
	class GetFromServer extends AsyncTask<HashMap<String, String>, Integer, String> {

		HashMap<String, String> lm = null;

		public GetFromServer(HashMap<String, String> lMap) {
			lm = lMap;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Intent intent = new Intent("FingerPrint_LOCATION_UPDATE");
			intent.putExtra("location", result);

			// WRITE YOUR CODES HERE
			// Hint: You need to broadcast the data obtained from the
			// localization server to other Activities
			try {
				JSONObject resultJson = new JSONObject(result);
				intent.putExtra("name", resultJson.getString("name"));
				intent.putExtra("floorID", resultJson.getString("floorID"));
				intent.putExtra("userPositionX", resultJson.getInt("floorLocationX"));
				intent.putExtra("userPositionY", resultJson.getInt("floorLocationY"));
				JSONObject coorJson = resultJson.getJSONObject("coor");
				Log.d("coor json", coorJson.toString());
				intent.putExtra("coorX", coorJson.getInt("X"));
				intent.putExtra("coorY", coorJson.getInt("Y"));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			sendBroadcast(intent);
		}

		@Override
		protected String doInBackground(HashMap<String, String>... params) {

			// WRITE YOUR CODES HERE
			// Hint 1: You will need to use the Distance object, distance
			// Hint 2: You need to pass variables lm, mapsf and SERVER when
			// calling the server API
			String response = "";
			response = distance.getLocationXMLRPC(lm, mapsf, SERVER);
			Log.d("response from server", response);

			return response;
			// return "Server result goes here";
		}

	}
}
