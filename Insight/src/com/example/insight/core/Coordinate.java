package com.example.insight.core;

import java.io.Serializable;

public class Coordinate implements Cloneable, Serializable {
	private int X;
	private int Y;
	private int Z;
	private float consTantX = 0;
	private float consTantY = 0;
	private int angle=0;
	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public Coordinate(int X, int Y, int Z,boolean meters) {

		
		if (Z == 1) {
			this.consTantX = Constants.getInstance().getLevel1_X_C();
			this.consTantY = Constants.getInstance().getLevel1_Y_C();
		} else if (Z == 2) {
			this.consTantX = Constants.getInstance().getLevel2_X_C();
			this.consTantY = Constants.getInstance().getLevel2_Y_C();

		} else {
			this.consTantX = 6.25f;
			this.consTantY = 8.33f;

		}
		this.X = (int) Math.round(Math.ceil(X * consTantX));
		this.Y = (int) Math.round(Math.ceil(Y * consTantY));
		this.Z = Z;
	}

	public Coordinate(int X, int Y,int Z) {

		this.X = X;
		this.Y = Y;
		this.Z=Z;
		
		if (Z == 1) {
			this.consTantX = Constants.getInstance().getLevel1_X_C();
			this.consTantY = Constants.getInstance().getLevel1_Y_C();
		} else if (Z == 2) {
			this.consTantX = Constants.getInstance().getLevel2_X_C();
			this.consTantY = Constants.getInstance().getLevel2_Y_C();

		} else {
			this.consTantX = 6.25f;
			this.consTantY = 8.33f;

		}
	}

	public Object clone() {
		Coordinate coor = new Coordinate(X, Y,Z);
				
		return coor;
	}

	public void setX(int x) {
		X = x;
	}

	public void setY(int y) {
		Y = y;
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}

	public int getZ() {
		return Z;
	}

//	public void incX(float x) {
//		X += (int) Math.round(Math.ceil(x * consTantX));
//	}
//
//	public void incY(float y) {
//		Y += (int) Math.round(Math.ceil(y * consTantY));
//		;
//	}
//
//	public void decX(float x) {
//		X -= (int) Math.round(Math.ceil(x * consTantX));
//	}
//
//	public void decY(float y) {
//		Y -= (int) Math.round(Math.ceil(y * consTantY));
//		;
//	}

	public void setZ(int z) {
		Z = z;
	}

	public String toString() {
		
			return X + "," + Y + "," + Z+","+angle;
		

	}
}
