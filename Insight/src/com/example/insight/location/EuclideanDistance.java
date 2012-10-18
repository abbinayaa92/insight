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
import java.util.HashMap;
import java.util.Iterator;

import com.example.insight.core.*;

public class EuclideanDistance implements Distance{

	
	double defLevel = -130;
	ArrayList<Shop> shopList = new ArrayList<Shop>();
	HashMap<String, Vector> indivssidlevel_global = null;
	HashMap<String, String> coor = null;
	int min = 500;
	int max = 0;
	String minName = "";
	String maxName = "";
	
	BufferedWriter logw=null;
	int sampleSize=0;
	String mapFile;
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
	public void setDefaultLevel(int defaultLevel) {
		this.defaultLevel = defaultLevel;
	}
	int defaultLevel=0;
	public EuclideanDistance(String mapFile,int sampleSize,int defaultLevel,boolean coorDinates,String coorFile) {
		try {
			
				coor = new HashMap<String, String>();
			    this.sampleSize=sampleSize;
				this.defaultLevel=defaultLevel;
				File log = new File("/sdcard/map/DebugLog.txt");
				this.mapFile=mapFile;
				FileWriter writer = new FileWriter(log,true);
				 logw = new BufferedWriter(writer);
				
				 int statrtIndx=1;
				 
					if(coorDinates)
						statrtIndx=2;
					
			FileReader rc = new FileReader(coorFile);
			BufferedReader rdrc = new BufferedReader(rc);
			String reaffilec = rdrc.readLine();
			while (reaffilec != null) {
				
				String values[] = reaffilec.split("=");
				coor.put(values[0].trim().toLowerCase(), values[1].trim());
				reaffilec = rdrc.readLine();
				if(reaffilec==null)
				{
					break;
				}
			}
			FileReader r = new FileReader(mapFile);
			BufferedReader rdr = new BufferedReader(r);
			String reaffile = rdr.readLine();
			
			while (reaffile != null) {
				int ncount = 0;
				int histPlot[] = new int[500];
				String list[] = reaffile.split(",");
				ArrayList<Vector> shopVList = new ArrayList<Vector>();
				if (indivssidlevel_global == null) {
					indivssidlevel_global = new HashMap<String, Vector>();
					for (int i = statrtIndx; i < list.length; i++) {
						String vec = list[i];
						String vecList[] = vec.split("\\|");
						Vector shopV = new Vector(vecList[0],
								Double.parseDouble(vecList[1]),
								Double.parseDouble(vecList[2]));
						shopVList.add(shopV);
					

						Vector levelv = new Vector(vecList[0], -130, 0);
						indivssidlevel_global.put(vecList[0], levelv);
					}
				} else {
					for (int i = statrtIndx; i < list.length; i++) {
						String vec = list[i];
						String vecList[] = vec.split("\\|");
						
						Vector shopV = new Vector(vecList[0],
								Double.parseDouble(vecList[1]),
								Double.parseDouble(vecList[2]));
						shopVList.add(shopV);

					}
				}

			
				Shop shp = new Shop(list[0]);
				shp.setFingerPrintMap(shopVList);
				if(coorDinates)
				{
					String coorString=coor.get(list[0].trim().toLowerCase());
					String coors[]=coorString.split(",");
					Coordinate cd = new Coordinate(Integer.parseInt(coors[0]), Integer.parseInt(coors[1]), Integer.parseInt(coors[2]));
					shp.setCoor(cd);
				}
				//StringBuilder shopHist = new StringBuilder();
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
	 private String getTimeStamp(){
	    	Calendar c = Calendar.getInstance();
	    	return c.get(Calendar.YEAR) + "-" +
	    	(c.get(Calendar.MONTH)+1) + "-"+
	    	c.get(Calendar.DAY_OF_MONTH) + "-" +
	    	c.get(Calendar.HOUR_OF_DAY) + 
	    	c.get(Calendar.MINUTE);
	    }

	 public void logStatements(HashMap<String, Vector> indivssidlevel,EDistance retura[])
	    {
		 try {
			logw.write("**************************************************************************************\n");
			
			StringBuilder build = new StringBuilder();
			
			 build.append("Your Current Location at time "+getTimeStamp()+" MapFile= "+mapFile+" SampleSize = "+sampleSize+"\n");
			 build.append("1) "+retura[0]+"\n");
			 build.append("2) "+retura[1]+"\n");
			 build.append("3) "+retura[2]+"\n");
			 build.append("4) "+retura[3]+"\n");
			 build.append("5) "+retura[4]+"\n");
			 build.append("6) "+retura[5]+"\n");
			 build.append("7) "+retura[6]+"\n");
			 build.append("8) "+retura[7]+"\n");
			 build.append("9) "+retura[8]+"\n");
			 build.append("10) "+retura[9]+"\n");
			 
			 logw.write(build.toString());
			 
			 Iterator<String> iter = indivssidlevel.keySet().iterator();
			 StringBuilder levelbuild= new StringBuilder();
			 while(iter.hasNext())
			 {
				
				 
				 String key=iter.next();
				 String value=String.valueOf(indivssidlevel.get(key).meanLevel);
				 levelbuild.append(key+","+value+"#");
			 }
			 logw.write( levelbuild.toString()+"\n");
			 
			 logw.write("**************************************************************************************\n");
			 logw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		 
	    }
	 public  double mean(ArrayList<String> values,int sampleSize,int defaultLevel)
		{
			double mean=0;
			if(values==null)
			{
				return defaultLevel;
			}
			Iterator<String> valuesIter = values.iterator();
			int indx = 0;
			while(valuesIter.hasNext())
			{
				mean+=Double.parseDouble(valuesIter.next());
				indx++;
			}
			
			mean=(((sampleSize-indx)*defaultLevel)+mean)/sampleSize;
		
			return mean;
		}
	public EDistance[] getLocation(HashMap<String, ArrayList<String>> levelMap) {

		HashMap<String, Vector> indivssidlevel=(HashMap<String, Vector>)indivssidlevel_global.clone();
	
		Iterator<String> levelIter = levelMap.keySet().iterator();
		double mean=0.0;
		while (levelIter.hasNext()) {
		
			String bssid=levelIter.next();
			
			 ArrayList<String> levels=levelMap.get(bssid);
			 
			 mean=mean(levels,sampleSize,defaultLevel);
			
			
		
			Vector vect = indivssidlevel.get(bssid);
		
			if (vect != null) {
				vect.setMeanLevel(mean);
				indivssidlevel.put(bssid, vect);
			}
			
		}

		ArrayList<EDistance> distArray = new ArrayList<EDistance>();

		Iterator<Shop> iter = shopList.iterator();

		while (iter.hasNext()) {
			Shop sh = iter.next();
			EDistance dist = null;

			dist = sh.getEDistance(indivssidlevel);

			distArray.add(dist);
		}
		//Collections.sort(distArray);

		Iterator<EDistance> distIter = distArray.iterator();

		//		while (distIter.hasNext()) {
		//			System.err.println(distIter.next());
		//		}
		//		
		
		EDistance retura[] = new EDistance[10];
		 for(int i=0;i<10;i++)
		 {
		 retura[i]=distArray.get(i);
		 }
		 
		 logStatements(indivssidlevel,retura);
		 indivssidlevel=null;
		 return retura;
	}
	public String getLocationXMLRPC(HashMap<String, String> levelMap, String mapFile, String server) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int[] getLocationXMLRPCArr(HashMap<String, String> levelMap, String mapFile, String server) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public EDistance getLocationXMLRPCObj(HashMap<String, String> levelMap, String mapFile, String server) {
		// TODO Auto-generated method stub
		return null;
	}

	
//	public static void main(String[] args) {}

}
