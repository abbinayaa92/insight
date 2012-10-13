package com.example.insight.datamodel;

import android.app.Application;

public class InsightGlobalState extends Application {

	private Eventlist eventlist= new Eventlist();
	private Event events= new Event();
	
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
}
