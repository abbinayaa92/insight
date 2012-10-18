package com.example.insight.core;


public class CrossProduct implements Cloneable{

public String BSSID_A="";
public String BSSID_B="";

public double value=0.0;

public CrossProduct(String BSSID_A,String BSSID_B)
{
	this.BSSID_B=BSSID_B;
	this.BSSID_A=BSSID_A;
}
//public String getBSSID_A() {
//	return BSSID_A;
//}
//
//
//public String getBSSID_B() {
//	return BSSID_B;
//}
//
//
//public double getValue() {
//	return value;
//}

public void setValue(double value) {
	this.value = value;
}
public Object clone () 
        throws CloneNotSupportedException
   {
	return new CrossProduct(BSSID_A,BSSID_B);
   }
public boolean equals(Object obj)
{
	CrossProduct objc = (CrossProduct)obj;
	
	if(objc.BSSID_A.equals(BSSID_A)&&objc.BSSID_B.equals(BSSID_B))
	{
		return true;
	}
	
	if(objc.BSSID_B.equals(BSSID_A)&&objc.BSSID_A.equals(BSSID_B))
	{
		return true;
	}
	return false;
}
public String toString()
{
	return this.BSSID_A+"|"+this.BSSID_B+"|"+this.value;
	
}
}
