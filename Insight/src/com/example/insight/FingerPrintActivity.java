package com.example.insight;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.insight.core.Constants;

public class FingerPrintActivity extends Activity implements View.OnClickListener{
	
	private int sampleSize;
	private String mapsf = "";
	TextView sample, location;
	Button bStart, bStop;
	ToggleButton tbServer;
	Spinner model;
	
	private SharedPreferences config;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		model = (Spinner) findViewById(R.id.model);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.modelList, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		model.setAdapter(adapter);
		model.setOnItemSelectedListener(new ModelSelectedListener());

		bStart = (Button) findViewById(R.id.bStart);
		bStop = (Button) findViewById(R.id.bStop);
		sample = (TextView) findViewById(R.id.sampleSize);
		location = (TextView) findViewById(R.id.location);
		tbServer = (ToggleButton) findViewById(R.id.tbServer);

		config = getSharedPreferences("FingerPrint",MODE_PRIVATE);
		tbServer.setChecked(config.getBoolean("tbServer",false));
		boolean started = config.getBoolean("started",false);
		bStart.setOnClickListener(this);
		bStop.setOnClickListener(this);
		tbServer.setOnClickListener(this);
		if (!started)
			bStop.setEnabled(false);
		else {
			bStart.setEnabled(false);
			int index = config.getInt("modelId",0); 
			model.setSelection(index);
			Spinner maps = (Spinner) findViewById(R.id.maps);
			index = config.getInt("mapId",0);
			maps.setSelection(index);
			index = config.getInt("sampleId",0);
			Spinner samples = (Spinner) findViewById(R.id.samples_tocollect);
			index = config.getInt("sampleId",0);
			samples.setSelection(index);
		}
		
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		Constants.getInstance(width, height, 8);
	}
	
	public void onClick(View v) {
		SharedPreferences.Editor editor = config.edit();
		switch (v.getId()) {
		
		case R.id.bStart:
			bStart.setEnabled(false);
			bStop.setEnabled(true);

			Spinner samples = (Spinner) findViewById(R.id.samples_tocollect);

			sampleSize = Integer.parseInt(samples.getSelectedItem()
					.toString());

			model = (Spinner) findViewById(R.id.model);

			int index = model.getSelectedItemPosition();

			Spinner maps = (Spinner) findViewById(R.id.maps);
			mapsf = maps.getSelectedItem().toString();
			File path = Environment.getExternalStorageDirectory();
			String map = path + "/map/" + mapsf;

			String coorFile = path + "/map/coordinate";
			
			model.setEnabled(false);
			
			editor.putInt("modelId",index);
			editor.putInt("mapId", maps.getSelectedItemPosition());
			editor.putString("map",map);
			editor.putString("coor",coorFile);

			location.setText("");

			editor.putBoolean("started",true);
			editor.putInt("sampleId",samples.getSelectedItemPosition());
			editor.putInt("sampleSize",sampleSize);
			editor.putBoolean("tbServer",tbServer.isChecked());
			editor.commit();
			
			startService(new Intent(this.getApplicationContext(),FingerPrintService.class));
			
			break;
		
		case R.id.bStop:
			bStart.setEnabled(true);
			bStop.setEnabled(false);
			model.setEnabled(true);

			editor.putBoolean("started",false);
			editor.commit();
			
			stopService(new Intent(this.getApplicationContext(),FingerPrintService.class));
			
			break;
			
		case R.id.tbServer:
			
			break;
			
		}
	}

	public class ModelSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			Spinner model = (Spinner) findViewById(R.id.model);

			int index = model.getSelectedItemPosition();
			if (index == 1) {
				Spinner spinner = (Spinner) findViewById(R.id.maps);
				ArrayAdapter<CharSequence> adapter = ArrayAdapter
						.createFromResource(parent.getContext(),
								R.array.mapsList,
								android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				spinner.setAdapter(adapter);
			} else if (index == 0) {
				Spinner spinner = (Spinner) findViewById(R.id.maps);
				ArrayAdapter<CharSequence> adapter = ArrayAdapter
						.createFromResource(parent.getContext(),
								R.array.hmapsList,
								android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				spinner.setAdapter(adapter);
			}

		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}
}