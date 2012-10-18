package com.example.insight.location;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.net.wifi.ScanResult;

import com.example.insight.core.*;

public class HyperbolicEuclideanDistance implements Distance {

	double defLevel = -130;
	ArrayList<Shop> shopList = new ArrayList<Shop>();	//Represents each line of the fingerprint file
	LinkedHashSet<String> globalList = null;	// All the pair of mac addresses present
	// HashMap<String,ArrayList<CrossProduct>> crossProductList = new
	// HashMap<String,ArrayList<CrossProduct>>();
	int min = 500;
	int max = 0;
	String minName = "";
	String maxName = "";
	HashMap<String, String> coor = null;
	BufferedWriter logw = null;
	int sampleSize = 0;
	String mapFile;

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public void setDefaultLevel(int defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	int defaultLevel = 0;

	public HyperbolicEuclideanDistance(String mapFile, int sampleSize,
			int defaultLevel, boolean coorDinates, String coorFile) {
		try {
			coor = new HashMap<String, String>();
			int statrtIndx = 1;
			if (coorDinates)
				statrtIndx = 2;
			this.sampleSize = sampleSize;
			this.defaultLevel = defaultLevel;
			//File log = new File("/sdcard/map/DebugLog.txt");
			this.mapFile = mapFile;
			//FileWriter writer = new FileWriter(log, true);
			//logw = new BufferedWriter(writer);
			
			if (coorDinates) 
			{
				FileReader rc = new FileReader(coorFile);
				BufferedReader rdrc = new BufferedReader(rc);
				String reaffilec = rdrc.readLine();
				while (reaffilec != null) 
				{
					// Read and store the coordinate file in the hashmap
					String values[] = reaffilec.split("=");
					coor.put(values[0].trim().toLowerCase(), values[1].trim());
					reaffilec = rdrc.readLine();
					if (reaffilec == null) {
						break;
					}
				}
			}
			
			FileReader r = new FileReader(mapFile);
			BufferedReader rdr = new BufferedReader(r);
			String reaffile = rdr.readLine();

			while (reaffile != null) {
				//reading the map file and storing it in memory
				String list[] = reaffile.split(",");
				ArrayList<CrossProduct> shopVList = new ArrayList<CrossProduct>();
				if (globalList == null) {
					globalList = new LinkedHashSet<String>();
				}
					for (int i = statrtIndx; i < list.length; i++) {
						String vec = list[i];
						String vecList[] = vec.split("\\|");

						CrossProduct shopV = new CrossProduct(vecList[0],
								vecList[1]);

						globalList.add(vecList[0] + "," + vecList[1]);

						shopV.setValue(Double.parseDouble(vecList[2]));
						shopVList.add(shopV);

					}
				/*} else {
					for (int i = statrtIndx; i < list.length; i++) {
						String vec = list[i];
						String vecList[] = vec.split("\\|");

						CrossProduct shopV = new CrossProduct(vecList[0],
								vecList[1]);
						shopV.setValue(Double.parseDouble(vecList[2]));
						shopVList.add(shopV);

					}
				}*/

				Shop shp = new Shop(list[0]);

				shp.setCp(shopVList);
				if (coorDinates) {
					String coorString = coor.get(list[0].trim().toLowerCase());
					String coors[] = coorString.split(",");
					Coordinate cd = new Coordinate(Integer.parseInt(coors[0]),
							Integer.parseInt(coors[1]),
							Integer.parseInt(coors[2]));
					shp.setCoor(cd);
				}
				shopList.add(shp);
				reaffile = rdr.readLine();

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getTimeStamp() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
				+ c.get(Calendar.DAY_OF_MONTH) + "-"
				+ c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE);
	}

	public void logStatements(ArrayList<CrossProduct> crossProductList,
			EDistance retura[]) {
		try {
			logw.write("**************************************************************************************\n");

			StringBuilder build = new StringBuilder();

			build.append("Your Current Location at time " + getTimeStamp()
					+ " MapFile= " + mapFile + " SampleSize = " + sampleSize
					+ "\n");
			build.append("1) " + retura[0] + "\n");
			build.append("2) " + retura[1] + "\n");
			build.append("3) " + retura[2] + "\n");
			build.append("4) " + retura[3] + "\n");
			build.append("5) " + retura[4] + "\n");
			build.append("6) " + retura[5] + "\n");
			build.append("7) " + retura[6] + "\n");
			build.append("8) " + retura[7] + "\n");
			build.append("9) " + retura[8] + "\n");
			build.append("10) " + retura[9] + "\n");

			logw.write(build.toString());

			Iterator<CrossProduct> iter = crossProductList.iterator();
			StringBuilder levelbuild = new StringBuilder();
			while (iter.hasNext()) {

				levelbuild.append(iter.next() + ",");
			}
			logw.write(levelbuild.toString() + "\n");

			logw.write("**************************************************************************************\n");
			logw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public double nlr(double valuei, double valuej) {
		return valuei / valuej;// (Math.log(valuei/valuej)-Math.log(1/25));

	}

	public double mean(ArrayList<String> values, int sampleSize,
			int defaultLevel) {
		double mean = 0;
		if (values == null) {
			return defaultLevel;
		}
		Iterator<String> valuesIter = values.iterator();
		int indx = 0;
		while (valuesIter.hasNext()) {
			mean += Double.parseDouble(valuesIter.next());
			indx++;
		}

		mean = (((sampleSize - indx) * defaultLevel) + mean) / sampleSize;

		return mean;
	}
	
/*	
	public EDistance[] getLocation(HashMap<String, ArrayList<String>> levelMap) {

		
		HashMap<String,CrossProduct> crossProductList_local = new HashMap<String,CrossProduct>();
		
		LinkedHashSet<String> localList=(LinkedHashSet<String>)globalList.clone();
		
		Iterator<String> cpItera = localList.iterator();
		 long time1=System.currentTimeMillis();
	//	Iterator<String> levelIter = levelMap.keySet().iterator();
		//try {
			
			while(cpItera.hasNext())
			{
				
				String ssids[]=cpItera.next().split(",");
				String bssid_a=ssids[0];
				
				ArrayList<String> levels_a = levelMap.get(bssid_a);
				double meana=mean(levels_a,sampleSize,defaultLevel);
				
//				Iterator<String> cpIterb = localList.iterator();
//				
//				while(cpIterb.hasNext())
//				{
					
					
					String bssid_b=ssids[1];
//					if(bssid_a.equals(bssid_b))
//						continue;
					ArrayList<String> levels_b = levelMap.get(bssid_b);
					double meanb=mean(levels_b,sampleSize,defaultLevel);
					
					CrossProduct cross = new CrossProduct(bssid_a,bssid_b);
					cross.setValue(nlr(meana,meanb));
					crossProductList_local.put(bssid_a+","+bssid_b, cross);
//					if(!crossProductList_local.contains(cross))
//					{
//						crossProductList_local.add(cross);
//					}
//				}
//				cpItera.remove();
			}
			 long time2=System.currentTimeMillis();
		        
			 System.err.println("Time taken CrossProduct = "+(time2-time1)+" Size = "+crossProductList_local.size()+"\n");
//		} catch (CloneNotSupportedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
			 time1=System.currentTimeMillis();
		ArrayList<EDistance> distArray = new ArrayList<EDistance>();

		Iterator<Shop> iter = shopList.iterator();

		while (iter.hasNext()) {
			Shop sh = iter.next();
			EDistance dist = null;

			dist = sh.getEDistanceHyp(crossProductList_local);

			distArray.add(dist);
		}
		Collections.sort(distArray);

		Iterator<EDistance> distIter = distArray.iterator();

		// while (distIter.hasNext()) {
		// System.err.println(distIter.next());
		// }
		//
		time2=System.currentTimeMillis();
		
		 System.err.println("Time taken Distance = "+(time2-time1)+"\n");
		 EDistance retura[] = new EDistance[distArray.size()];
		 for(int i=0;i<distArray.size();i++)
		 {
			 retura[i]=distArray.get(i);
		 }
	//	logStatements(crossProductList_local, retura);
		//crossProductList_local = null;
		return retura;
	}
	
*/
	public EDistance[] getLocation(HashMap<String, ArrayList<String>> levelMap) {

		HashMap<String, CrossProduct> crossProductList_local = new HashMap<String, CrossProduct>();

		LinkedHashSet<String> localList = (LinkedHashSet<String>) globalList.clone();

		Iterator<String> cpItera = localList.iterator();
		long time1 = System.currentTimeMillis();
		// Iterator<String> levelIter = levelMap.keySet().iterator();
		// try {

		while (cpItera.hasNext()) {

			String ssids[] = cpItera.next().split(",");
			String bssid_a = ssids[0];

			ArrayList<String> levels_a = levelMap.get(bssid_a);
			double meanA = mean(levels_a, sampleSize, defaultLevel);

			// Iterator<String> cpIterb = localList.iterator();
			//
			// while(cpIterb.hasNext())
			// {

			String bssid_b = ssids[1];
			// if(bssid_a.equals(bssid_b))
			// continue;
			ArrayList<String> levels_b = levelMap.get(bssid_b);
			double meanB = mean(levels_b, sampleSize, defaultLevel);

			CrossProduct cross = new CrossProduct(bssid_a, bssid_b);
			cross.setValue(nlr(meanA, meanB));
			crossProductList_local.put(bssid_a + "," + bssid_b, cross);
			// if(!crossProductList_local.contains(cross))
			// {
			// crossProductList_local.add(cross);
			// }
			// }
			// cpItera.remove();
		}
		long time2 = System.currentTimeMillis();

		System.err.println("Time taken CrossProduct = " + (time2 - time1)
				+ " Size = " + crossProductList_local.size() + "\n");
		// } catch (CloneNotSupportedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		time1 = System.currentTimeMillis();
		ArrayList<EDistance> distArray = new ArrayList<EDistance>();

		Iterator<Shop> iter = shopList.iterator();

		while (iter.hasNext()) {
			Shop sh = iter.next();
			EDistance dist = null;

			dist = sh.getEDistanceHyp(crossProductList_local);

			distArray.add(dist);
		}
		Collections.sort(distArray, new DistanceComparator());

		Iterator<EDistance> distIter = distArray.iterator();

		// while (distIter.hasNext()) {
		// System.err.println(distIter.next());
		// }
		//
		time2 = System.currentTimeMillis();

		System.err.println("Time taken Distance = " + (time2 - time1) + "\n");
		EDistance retura[] = new EDistance[distArray.size()];
		for (int i = 0; i < retura.length; i++) {
			retura[i] = distArray.get(i);
		}
		// logStatements(crossProductList_local, retura);
		crossProductList_local = null;
		return retura;
	}

	public class DistanceComparator implements Comparator<EDistance> {

		public int compare(EDistance d1, EDistance d2) {
			return d1.getDistance() <= d2.getDistance() ? -1 : 1;
		}
	
	}
	
	
	public String getLocationXMLRPC(HashMap<String, String> levelMap, String mapFile, String server) {

		try {
			
			XMLRPCClient client = new XMLRPCClient(server + "/LocationService/xmlrpc");
			
		    //Object[] params = new Object[]{levelMap};
		    //EDistance retura[] = (EDistance[]) client.callEx("Calculate.distanceLocObjArr", params);
			String result = (String) client.call("Calculate.distanceJSON", levelMap, mapFile);
			
		    return result;
			
		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "A server error has occurred. Please check that you have Internet connection.";
		}
	}
	
	public EDistance getLocationXMLRPCObj(HashMap<String, String> levelMap, String mapFile, String server) {

		try {
			
			XMLRPCClient client = new XMLRPCClient(server + "/LocationService/xmlrpc");
			
		    EDistance result = (EDistance) client.call("Calculate.distanceLocObj", levelMap, mapFile);
			
		    return result;
			
		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int[] getLocationXMLRPCArr(HashMap<String, String> levelMap, String mapFile, String server) {

		try {
			
			XMLRPCClient client = new XMLRPCClient(server + "/LocationService/xmlrpc");
			
		    int[] result = (int[]) client.call("Calculate.distanceLoc", levelMap, mapFile);
			
		    return result;
			
		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
