package com.github.mineGeek.ZoneReset.Data;

import com.github.mineGeek.ZoneReset.Messaging.Message;
import com.github.mineGeek.ZoneReset.Tasks.Tasks;
import com.github.mineGeek.ZoneReset.Triggers.Triggers;
import com.github.mineGeek.ZoneRest.Actions.Actions;

public class Zone {

	
	public String 	tag;
	public String	worldName;
	
	private Area	area = new Area();
	
	public boolean	enabled = true;
	
	public Triggers triggers;
	public Tasks 	tasks = null;
	public Actions 	preActions = null;
	public Actions	resetActions = null;
	public Actions 	postActions = null;
	
	public Message	resetMessage = null;
	public boolean	requiresNoPlayers = false;
	
	public String 	getTag() 				{ return this.tag; }
	public void 	setTag( String value ) 	{ this.tag = value; }
	
	public String	getWorldName()			{ return this.worldName; }
	public void		setWorldName(String val){ this.worldName = val;}

	public Area 	getArea() 				{ return this.area; }	
	public void 	setArea( Area a ) 		{ this.area = a; }	
	
	public Zone(){};
	
	public Zone( Zone clone, String tag ) {
		
		this();
		this.tag = tag;
		this.worldName = clone.worldName;
		this.area = clone.area;
		this.enabled = clone.enabled;
		this.triggers = clone.triggers;
		this.preActions = clone.preActions;
		this.resetActions = clone.resetActions;
		this.postActions = clone.postActions;
		this.resetMessage = clone.resetMessage;
		this.tasks = clone.tasks;		
	}
	
	public void start() {
		tasks.start();
	}
	
	public void reset() {
		tasks.stop();
		preActions.run();
		resetActions.run();
		postActions.run();
		tasks.start();
	}
	
	public void close() {
		tasks.stop();
		triggers.close();
		
	}
	

	
	
	
}
