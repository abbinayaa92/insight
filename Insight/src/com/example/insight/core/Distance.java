package com.example.insight.core;

import java.util.ArrayList;
import java.util.HashMap;

public interface Distance {
	public EDistance[] getLocation(HashMap<String, ArrayList<String>> levelMap) ;
	public void setSampleSize(int sampleSize);
	public String getLocationXMLRPC(
			HashMap<String, String> levelMap, String mapsf, String server);
	public int[] getLocationXMLRPCArr(
			HashMap<String, String> levelMap, String mapsf, String server);
	public EDistance getLocationXMLRPCObj(
			HashMap<String, String> levelMap, String mapsf, String server);
}
