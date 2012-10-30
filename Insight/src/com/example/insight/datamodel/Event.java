package com.example.insight.datamodel;
import java.lang.Math ;
import java.util.Comparator;

public class Event {
	
	private int id;
	private String title;
	private String description;
	private String venue;
	private String date;
	private String time;
	private int coorx;
	private int coory;
	private String floor_id;
	private double distance;
	private int pop;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getCoorx() {
		return coorx;
	}
	public void setCoorx(int coorx) {
		this.coorx = coorx;
	}
	public int getCoory() {
		return coory;
	}
	public void setCoory(int coory) {
		this.coory = coory;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFloor_id() {
		return floor_id;
	}
	public void setFloor_id(String floor_id) {
		this.floor_id = floor_id;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(int x,int y) {
		if(coorx==0 && coory==0)
			this.distance = Math.sqrt((x)^2 + (y)^2);
		else
			this.distance = Math.sqrt((x-coorx)^2 + (y-coory)^2);
	}
	
	public int getPop() {
		return pop;
	}
	public void setPop(int pop) {
		this.pop = pop;
	}

	public class EventLocCompare implements Comparator<Event> {

		public int compare(Event o1, Event o2) {
			Double d2= new Double(o2.getDistance());
			return (d2.compareTo(o1.getDistance()));
		}
	}
	
	public class EventDateCompare implements Comparator<Event> {

		public int compare(Event o1, Event o2) {
			return (o2.getDate().compareTo(o1.getDate()));
		}
	}
	
	public class EventPopCompare implements Comparator<Event> {

		public int compare(Event o1, Event o2) {
			Integer d2= new Integer(o2.getPop());
			return (d2.compareTo(o1.getPop()));
		}
	}


}

