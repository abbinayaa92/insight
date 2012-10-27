package com.example.insight.datamodel;

import android.app.Application;

public class InsightGlobalState extends Application {

	private String email;
	private String id;
	private int coorx;
	private int coory;
	private double lat;
	private double lon;
	private String floor_id;
	private String floor_name;
	private Eventlist eventlist= new Eventlist();
	private Event events= new Event();
	private FriendList friendlist= new FriendList();
	private Friend friends= new Friend();
	
	public Eventlist getEventlist() {
		return eventlist;
	}
	public void setEventlist(Eventlist eventlist) {
		this.eventlist = eventlist;
	}
	public Event getEvents() {
		return events;
	}
	public void setEvents(Event events) {
		this.events = events;
	}
	public FriendList getFriendlist() {
		return friendlist;
	}
	public void setFriendlist(FriendList friendlist) {
		this.friendlist = friendlist;
	}
	public Friend getFriends() {
		return friends;
	}
	public void setFriends(Friend friends) {
		this.friends = friends;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public String getFloor_id() {
		return floor_id;
	}
	public void setFloor_id(String floor_id) {
		this.floor_id = floor_id;
	}
	public String getFloor_name() {
		return floor_name;
	}
	public void setFloor_name(String floor_name) {
		this.floor_name = floor_name;
	}
}
