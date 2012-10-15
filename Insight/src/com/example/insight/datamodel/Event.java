package com.example.insight.datamodel;

public class Event {
	
	private int event_id;
	private String title;
	private String description;
	private String venue;
	private String date;
	private String time;
	private String coorx;
	private String coory;
	
	public int getEvent_id() {
		return event_id;
	}
	public void setEvent_id(int event_id) {
		this.event_id = event_id;
	}
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
	public String getCoorx() {
		return coorx;
	}
	public void setCoorx(String coorx) {
		this.coorx = coorx;
	}
	public String getCoory() {
		return coory;
	}
	public void setCoory(String coory) {
		this.coory = coory;
	}

}