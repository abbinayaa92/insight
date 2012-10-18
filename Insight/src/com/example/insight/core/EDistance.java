package com.example.insight.core;

import java.io.Serializable;
import java.text.DecimalFormat;

public class EDistance implements Comparable, Serializable {

	private double distance=0;
	
	private String name="";
	private Coordinate coor=null;
	public double getDistance() {
		return distance;
	}

	
	public Coordinate getCoor() {
		return coor;
	}


	public void setCoor(Coordinate coor) {
		this.coor = coor;
	}


	public String getName() {
		return name;
	}


	public EDistance(double distance,String name)
	{
		DecimalFormat df = new DecimalFormat("#.###");
		this.distance=Double.parseDouble(df.format(distance) ) ;
		this.name=name;
	}
	public int compareTo(Object o) {
		
		EDistance comp = (EDistance)o;
		
		if(getDistance()<comp.getDistance())
		{
			return -1;
		}
		else if(getDistance()>comp.getDistance())
		{
			return 1;
		}
		else
		{
		return 0;
		}
	}

	public String toString()
	{
		
		return "Distance to "+name+" = "+distance;// + " coordinate "+coor;
	}
	
	public String locText()
	{
		
		return "You are in " + name + " coordinate " + coor;
	}
}
