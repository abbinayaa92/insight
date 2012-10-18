package com.example.insight.core;

public class Constants {

	public static Constants constant=null;
	private   float boxLength=8;
	private   float ScreenX=480;
	private   float ScreenY=800;
	private   float Level1_X_C=ScreenX/(8*boxLength);
	private   float Level1_Y_C=ScreenY/(12*boxLength);
	
	private   float Level2_X_C=ScreenX/(7.5f*boxLength);
	private   float Level2_Y_C=ScreenY/(16*boxLength);
	private   int offsetAngle=45;
	
	private float treadlength[] = {0.4f,0.45f,.5f};
	

	private Constants(float X,float Y,float boxLength)
	{
		 this.boxLength=boxLength;
		 ScreenX=X-40;
		 ScreenY=Y;
		 Level1_X_C=ScreenX/(8*boxLength);
		 Level1_Y_C=ScreenY/(12*boxLength);
		 Level2_X_C=ScreenX/(7.5f*boxLength);
		 Level2_Y_C=ScreenY/(16*boxLength);
	}
	
	public float[] getTreadlength() {
		return treadlength;
	}
	

	public int getSampleSize()
	{
		return treadlength.length;
	}

	public int getOffsetAngle() {
		return offsetAngle;
	}

	public void setOffsetAngle(int offsetAngle) {
		this.offsetAngle = offsetAngle;
	}

	public  static Constants getInstance(float X,float Y,float boxLength)
	{
		if(constant==null)
		{
			constant=new Constants(X,Y,boxLength);
		}
		return constant;
	}
	public  static Constants getInstance()
	{
		
		return constant;
	}

	
	public float getBoxLength() {
		return boxLength;
	}

	public float getScreenX() {
		return ScreenX;
	}

	public float getScreenY() {
		return ScreenY;
	}

	public float getLevel1_X_C() {
		return Level1_X_C;
	}

	public float getLevel1_Y_C() {
		return Level1_Y_C;
	}

	public float getLevel2_X_C() {
		return Level2_X_C;
	}

	public float getLevel2_Y_C() {
		return Level2_Y_C;
	}
}
