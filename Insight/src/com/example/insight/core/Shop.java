package com.example.insight.core;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Shop {

//	private String landMark = "";
//	private String level = "";
	private String name = "";
	private ArrayList<Vector> fingerPrintMap;
	private ArrayList<CrossProduct> cp;
	private Coordinate coor =null;
	
	public Shop(String name) {
		
		this.name = name;

	}

	public Coordinate getCoor() {
		return coor;
	}

	public void setCoor(Coordinate coor) {
		this.coor = coor;
	}

	public ArrayList<CrossProduct> getCp() {
		return cp;
	}

	public void setCp(ArrayList<CrossProduct> cp) {
		this.cp = cp;
	}

	public void setFingerPrintMap(ArrayList<Vector> fingerPrintMap) {
		this.fingerPrintMap = fingerPrintMap;
	}

//	public String getLandMark() {
//		return landMark;
//	}
//
//	public String getLevel() {
//		return level;
//	}

	public String getName() {
		return name;
	}

	public ArrayList<Vector> getFingerPrintMap() {
		return fingerPrintMap;
	}

	public EDistance getEDistance(HashMap<String, Vector> map) {

		Iterator<Vector> iter = fingerPrintMap.iterator();
		int indx = 0;
		double distance = 0;
		while (iter.hasNext()) {
			Vector a = iter.next();
			Vector b = map.get(a.getBSSID());

			distance += ((a.meanLevel - b.meanLevel) * (a
					.meanLevel - b.meanLevel));
			indx++;
		}
	//	distance = Math.sqrt(distance);
		if(coor!=null)
		{
			EDistance dist = new EDistance(distance,  name);
			dist.setCoor(coor);
			return dist;
		}
		return new EDistance(distance,  name);
	}
	public EDistance getEDistanceHyp(HashMap<String,CrossProduct> crossProductList_local) {

		Iterator<CrossProduct> iter = cp.iterator();
		int indx = 0;
		double distance = 0;
		while (iter.hasNext()) {
			CrossProduct a = iter.next();
			//int cpindx = crossProductList_local.indexOf(a);
			//if (cpindx != -1) 
			{
				CrossProduct b = crossProductList_local.get(a.BSSID_A+","+a.BSSID_B);

				//	if (b!=null) 
					{
						//Squaring might be better for comparison
						distance += Math.abs(a.value - b.value);// * (a.value - b.value);
					}
//				else
//				{
//					b = crossProductList_local.get(a.getBSSID_B()+","+a.getBSSID_A());
//					System.out.println("Ignoring "+a+ " Found "+b + " indx "+indx);
//					System.exit(-1);
//					
//				}

			}
			indx++;
		}
		//System.err.println("Total ignored ******************************"+indx);
	//	distance = Math.sqrt(distance);
		if(coor!=null)
		{
			EDistance dist = new EDistance(distance,  name);
			dist.setCoor(coor);
			return dist;
		}
		return new EDistance(distance,  name);
	}
//	public EDistance getEDistance(ArrayList<CrossProduct> crossp) {
//
//		Iterator<CrossProduct> iter = cp.iterator();
//		int indx = 0;
//		double distance = 0;
//		while (iter.hasNext()) {
//			CrossProduct a = iter.next();
//			int cpindx = crossp.indexOf(a);
//			//if (cpindx != -1) 
//			{
//				CrossProduct b = crossp.get(cpindx);
//
//				if (a.getBSSID_A().equals(b.getBSSID_A())&&a.getBSSID_B().equals(b.getBSSID_B())) {
//					distance += ((a.getValue() - b.getValue()) * (a.getValue() - b
//							.getValue()));
//				}
//				else
//				{
//					System.out.println("Ignoring "+b);
//				}
//
//			}
//			indx++;
//		}
//		distance = Math.sqrt(distance);
//		return new EDistance(distance, landMark + "," + level + "," + name);
//	}
}
