package com.example.insight.datamodel;

import java.util.ArrayList;

public class Eventlist {

	private ArrayList<Event> events = new ArrayList<Event>();

	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}
	
	public void addEvent (Event event)
	{
		this.events.add(event);
	}
	
	public int size()
	{
		return this.events.size();
	}
	
	public Event get(int index)
	{
		return this.events.get(index);
	}
}
