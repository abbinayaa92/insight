package com.example.insight.datamodel;

public class Event {
	
	private int id;
	private String title;
	private String description;
	private String venue;
	private String date;
	private String time;
	private int coorx;
	private int coory;
	
	
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

}
