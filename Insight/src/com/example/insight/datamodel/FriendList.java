package com.example.insight.datamodel;

import java.util.ArrayList;

public class FriendList {

	private ArrayList<Friend> friendlist = new ArrayList<Friend>();

	public ArrayList<Friend> getFriendlist() {
		return friendlist;
	}

	public void setFriendlist(ArrayList<Friend> friendlist) {
		this.friendlist = friendlist;
	}
	
	public int size()
	{
		return friendlist.size();
	}
	
	public Friend get(int index)
	{
		return friendlist.get(index);
	}
	
	public void set(int index, Friend friend)
	{
		friendlist.add(index, friend);
	}
}
