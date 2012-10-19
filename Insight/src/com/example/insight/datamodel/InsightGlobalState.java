package com.example.insight.datamodel;

import android.app.Application;

public class InsightGlobalState extends Application {

	private String email;
	private String id;
	private int coorx;
	private int coory;
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
}
