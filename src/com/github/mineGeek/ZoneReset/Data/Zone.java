package com.github.mineGeek.ZoneReset.Data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.github.mineGeek.ZoneReset.Actions.Actions;
import com.github.mineGeek.ZoneReset.Messaging.Message;
import com.github.mineGeek.ZoneReset.Tasks.Tasks;
import com.github.mineGeek.ZoneReset.Triggers.Triggers;
import com.github.mineGeek.ZoneReset.Utilities.Tracking;

public class Zone {

	public enum ZRPVPMode {ON, OFF, DEFAULT, TIMED}
	public String 	tag;
	public String	worldName;
	
	private Area	area = new Area();
	
	public boolean	enabled = true;
	public ZRPVPMode pvpMode = ZRPVPMode.DEFAULT;
	
	public Triggers triggers = new Triggers();
	public Tasks 	tasks = new Tasks();
	public Actions 	preActions = null;
	public Actions	resetActions = null;
	public Actions 	postActions = null;
	
	public Message	resetMessage = null;
	public boolean	requiresNoPlayers = false;
	
	public List<Integer> chunkHashes = new ArrayList<Integer>();
	
	public String 	getTag() 				{ return this.tag; }
	
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
	
	public Zone( String tag ) {
		this.setTag( tag );
	}
	
	public void setTag( String tag ) {
		
		this.tag = tag;
		triggers.tag = tag;
		preActions = new Actions( tag );
		resetActions = new Actions( tag );
		postActions = new Actions( tag );		
		
		
	}
	
	public List<String> getPlayers() {
		
		return Tracking.getPlayersInZone( this );
		
	}	
	
	
	
	public void start() {
		tasks.start();
	}
	
	
	public void reset() {
		Bukkit.getLogger().info("resetting " + tag );
		tasks.stop();
		preActions.run();
		resetActions.run();
		postActions.run();
		tasks.start();
		if ( this.resetMessage != null ) this.resetMessage.run();
	}
	
	public void close() {
		tasks.stop();
		triggers.close();
		
	}
	

	
	
	
}
