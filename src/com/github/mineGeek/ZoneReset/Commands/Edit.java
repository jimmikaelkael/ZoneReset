package com.github.mineGeek.ZoneReset.Commands;


import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.github.mineGeek.ZoneReset.ZoneReset;
import com.github.mineGeek.ZoneReset.Player.Markers;
import com.github.mineGeek.ZoneReset.Utilities.Config;
import com.github.mineGeek.ZoneReset.Utilities.Utilities;
import com.github.mineGeek.ZoneReset.Utilities.Zone;
import com.github.mineGeek.ZoneReset.Utilities.Zones;

public class Edit extends CommandBase {

	public Edit(ZoneReset plugin) {
		super(plugin);
		
	}
	
	private Zone getEditZone( Player p ) {
		
		if ( p.hasMetadata("zr") ) {
			
			return (Zone)p.getMetadata("zr").get(0).value();

			
		}
		
		return null;
		
	}
	
	private void setEditZone( Player p, Zone z ) {
		
		if ( p.hasMetadata("zr") ) p.removeMetadata("zr", this.plugin );	
		p.setMetadata("zr", new FixedMetadataValue( this.plugin, z ) );
	}

	
	private void saveZone( Player p ) {
		
		Zone z = this.getEditZone(p);
		
		if ( z == null ) {
			p.sendMessage("You are not currently editing a zone. Start with /zredit zonename");
		}
		
		Config.saveZoneConfig(z);
		z.saveBlocks();
		Zones.addZone( z.getTag(), Config.c.getConfigurationSection("zones." + z.getTag() ) );
		Markers.hideZoneBoundaries( p );
		Utilities.clearPlayerMetaData( p );
		p.sendMessage( z.getTag() + " has been saved.");		
		
		
	}
	
	private void cancelEdit( Player p ) {
		Utilities.clearPlayerMetaData(p);
	}
	
	private void copyZone( Player p, String newTag ) {
		Zone z = this.getEditZone( p );
		
		if ( z == null ) {
			p.sendMessage( "You are not currently editing a zone. Start with /zredit zonename" );
			return;
		} else {
			this.setEditZone(p, new Zone(z, newTag));
		}
	}
	
	private void createZone( Player p, String tag ) {
		
		if ( Zones.getZone(tag) != null ) {
			p.sendMessage( "Zone " + tag + " already exists." );
			return;
		}
		
		Zone z = new Zone();
		z.setTag(tag);
		z.setWorldName( p.getWorld().getName() );
		this.setEditZone(p, z);
		p.sendMessage( tag + " created but not yet saved. type [/zr save] when done editing or [/zr cancel] to exit without saving changes.");
		
	}
	
	private void setAreaEditOn( Player p ) {
		
		Zone z = this.getEditZone(p);
		if ( z == null ) {
			p.sendMessage("You have no zone being edited. /zr zoneName to start editing.);");
			return;
		}
		
		this.setAreaEditOff(p);
		p.setMetadata("zra", new FixedMetadataValue( this.plugin, true ) );
		
	}
	
	private void setAreaEditOff( Player p ) {
		if ( p.hasMetadata("zra") ) p.removeMetadata("zra", this.plugin );
	}
	
	private void setInteractEditOn( Player p ) {
		
		Zone z = this.getEditZone( p );
		if ( z == null ) {
			this.execMessage = "You have no zone being edited. /zr zonename to start editing.";
			return;
		}
		
		p.setMetadata( "zrinteract", new FixedMetadataValue( this.plugin, true ) );
		
	}
	
	private void setInteractEditOff( Player p ) {
		if ( p.hasMetadata("zrinteract" ) ) p.removeMetadata("zrinteract", this.plugin );
	}
	
	@Override
	protected Boolean exec( String cmdName, String[] args ) {
		
		if ( !(sender instanceof Player) ) {
			execMessage = "You cannot use this command from the console.";
			return true;
		}
		
		Player p = (Player) sender;
		
		if ( args[0].equalsIgnoreCase("save") ) {
			
			this.saveZone(p);
			Markers.hideZoneBoundaries(p);
			return true;
			
		} else if ( args[0].equalsIgnoreCase("cancel") ) {
			
			this.cancelEdit(p);
			Markers.hideZoneBoundaries(p);
			return true;
	
		} else if ( args[0].equalsIgnoreCase("copy") ) {
			
			if ( args.length == 2 ) {
				
				this.copyZone(p, args[1] );
			} else {
				
				execMessage = "Incorrect number of argumnets. Expect 2 for copy command. e.g. /zr copy zoneName";				
			}
			
			return true;
			
			
		} else if ( args[0].equalsIgnoreCase("create") ) {
			
			if ( args.length == 2 ) {
				this.createZone(p, args[1]);
			} else {
				execMessage = "Incorrect number of argumnets. Expect 2 for copy command. e.g. /zr create zoneName";
			}
			return true;
			
		} else if ( args[0].equalsIgnoreCase("set") ) {
			
			if ( args.length == 1 ) {
				execMessage = "Incorrect number of arguments. Expect more with set command.";
				return true;
			}
			
			Zone z = this.getEditZone(p);
			
			if ( z == null ) {
				execMessage = "You are not currently editing a zone. Start by typeing /zr zonename or /zr create zonename";
				return true;
			}
			
			if ( args[1].equalsIgnoreCase( "world") ) {
				z.setWorldName( args[1] );
			} else if ( args[1].equalsIgnoreCase("area") ) {
				
				if ( args.length == 3 ) {
					if ( args[2].equalsIgnoreCase("off") ) {
						this.setAreaEditOff(p);
						execMessage = "Area editing off.";
						return true;
					}
				}				
				this.setAreaEditOn(p);
				execMessage = "Area editing on. Left click and right click seperate corners to set zone. To end edit save, cancel or type /zr set area off";
				return true;
			} else if ( args[1].equalsIgnoreCase("noplayers") ) {
				
				if ( args.length == 2 ) {
					z.setRequireNoPlayers( !z.isRequireNoPlayers() );
				} else if ( args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false") ) {
					z.setRequireNoPlayers( args[2].equalsIgnoreCase("true") );
				} else {
					execMessage = "Invalid options. /zr set noplayers [true|false]";
					return true;
				}
				execMessage = "Zone " + z.getTag() + " noplayers is now " + z.isRequireNoPlayers();
				return true;
			} else if ( args[1].equalsIgnoreCase("removeentities") ) {
				
				if ( args.length == 2 ) {
					z.setKillEntities( !z.isKillEntities() );
				} else if ( args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false") ) {
					z.setKillEntities( args[2].equalsIgnoreCase("true") );
				} else {
					execMessage = "Invalid options. /zr set removeentities [true|false]";
					return true;
				}
				execMessage = "Zone " + z.getTag() + " removeentities is now " + z.isKillEntities();
				return true;
				
			} else if ( args[1].equals("entities" ) ) {
				
				z.setSpawns( Utilities.getEntitiesInZone( z ) );
				execMessage = "All entities in zone " + z.getTag() + " are set.";
				
			} else if ( args[1].equalsIgnoreCase("removespawn" ) ) {
				
				if ( args.length == 2 ) {
					z.setRemoveSpawnPoints( !z.isRemoveSpawnPoints() );
				} else if ( args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false") ) {
					z.setRemoveSpawnPoints( args[2].equalsIgnoreCase("true") );
				} else {
					execMessage = "Invalid option. /zr set removespawn [true|false]";
					return true;
				}
				
				execMessage = "Zone " + z.getTag() + " will" + ( !z.isRemoveSpawnPoints() ? " not " : "" ) + " remove its spawn points on reset.";
				
			} else if (args[1].equalsIgnoreCase("movespawn") ) {
				
				if ( args.length < 5 || args.length > 6 ) {
					execMessage = "Incorrect number of arguments. try /zr set spawn [world] x y z";
					return true;
				}
				
				String worldName = z.getWorldName();
				int zx = 0;
				int zy = 0;
				int zz = 0;				
				
				
				if ( args.length == 6 ) {
					worldName = args[2];
					zx = Integer.parseInt( args[3] );
					zy = Integer.parseInt( args[4] );
					zz = Integer.parseInt( args[5] );
				} else {
					zx = Integer.parseInt( args[2] );
					zy = Integer.parseInt( args[3] );
					zz = Integer.parseInt( args[4] );					
				}
					
				z.setResetSpawnPoints(worldName, zx, zy, zz);
				execMessage = "Players will now spawn at (" + zx + ", " + zy + ", " + zz + ") when they spawn (if they were in Zone " + z.getTag() + " at the time).";
				return true;				
				
			} else if ( args[1].equalsIgnoreCase( "moveplayers") ) {
				
				if ( args.length < 5 || args.length > 6 ) {
					execMessage = "Incorrect number of arguments. try /zr set movePlayers [world] x y z";
					return true;
				}
				
				String worldName = z.getWorldName();
				int zx = 0;
				int zy = 0;
				int zz = 0;				
				
				
				if ( args.length == 6 ) {
					worldName = args[2];
					zx = Integer.parseInt( args[3] );
					zy = Integer.parseInt( args[4] );
					zz = Integer.parseInt( args[5] );
				} else {
					zx = Integer.parseInt( args[2] );
					zy = Integer.parseInt( args[3] );
					zz = Integer.parseInt( args[4] );					
				}
					
				z.setResetSpawnPoints( worldName, zx, zy, zz);
				execMessage = "Players who are in Zone " + z.getTag() + " will be moved to (" + zx + ", " + zy + ", " + zz + ") when zone resets.";
				return true;				
				
			} else if ( args[1].equalsIgnoreCase( "trigger") ) {
			
				
				if ( args.length < 3 ) {
					execMessage = "Incorrect number of arguments. Expected /zr set trigger [options]";
					return true;
				}
				
				if ( args[2].equalsIgnoreCase( "onplayerjoin") ) {
					
					if ( args.length == 3 ) {
						z.setOnPlayerJoin( !z.isOnPlayerJoin() );
						execMessage = "Zone '" + z.getTag() + "' will" + ( z.isOnPlayerJoin() ? " now" : " not")  + " reset when any player joins.";
						return true;
						
					} else if ( args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false") ) {
						z.setOnPlayerJoin( args[3].equalsIgnoreCase("true") );
						execMessage = "Zone '" + z.getTag() + "' will" + ( z.isOnPlayerJoin() ? " now" : " not")  + " reset when any player joins.";
						return true;
					} else if ( args[3].equalsIgnoreCase("remove" ) ){
						
						for ( int x = 4; x < args.length; x++ ) {
							z.getOnPlayerJoinList().remove( args[x] );
							p.sendMessage( "Player " + args[x] + " will no longer reset zone '" + z.getTag() + "' when they join." );
						}
						
					} else if ( args[3].equalsIgnoreCase("add") ) {
						for ( int x = 4; x < args.length; x++ ) {
							z.getOnPlayerJoinList().remove( args[x] );
							z.getOnPlayerJoinList().add( args[x] );
							p.sendMessage( "Player " + args[x] + " will now reset zone '" + z.getTag() + "' when they join." );
						}						
					}
					
				} else if ( args[2].equalsIgnoreCase("onplayerquit") ) {
					
					if ( args.length == 3 ) {
						
						z.setOnPlayerQuit( !z.isOnPlayerQuit() );
						execMessage = "Zone '" + z.getTag() + "' will" + ( z.isOnPlayerQuit() ? " now" : " not")  + " reset when any player quits.";
						return true;						
						
					} else if ( args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false") ) {
						z.setOnPlayerQuit( args[3].equalsIgnoreCase("true") );	
						execMessage = "Zone '" + z.getTag() + "' will" + ( z.isOnPlayerQuit() ? " now" : " not")  + " reset when any player quits.";
						return true;
					} else if ( args[3].equalsIgnoreCase("remove" ) ){
						
						for ( int x = 4; x < args.length; x++ ) {
							z.getOnPlayerQuitList().remove( args[x] );
							p.sendMessage( "Player " + args[x] + " will no longer reset zone '" + z.getTag() + "' when they quit." );
						}
						
					} else if ( args[3].equalsIgnoreCase("add") ) {
						for ( int x = 4; x < args.length; x++ ) {
							z.getOnPlayerQuitList().remove( args[x] );
							z.getOnPlayerQuitList().add( args[x] );
							p.sendMessage( "Player " + args[x] + " will now reset zone '" + z.getTag() + "' when they quit." );
						}						
					}					
					
				} else if ( args[2].equalsIgnoreCase( "time" )  ) {
					
					if ( args.length == 3 ) {
						execMessage = "Invalid option. Expect a format of dhms (e.g. 2d3m for every 48 hours and 3 minutes)";
						return true;
					}
					
					z.setOnMinutesFormat( args[3] );
					
					if ( z.getOnMinutes() == 0 ) {
						execMessage = "Failure reading time argument. format should be in days, hours, minutes, seconds #[d|h|m|s]. Example for 24 hours: 1d or 24h. You can combined them all together, eg : /zr set time 1d3h5m0s";
					} else {
						execMessage = "Set Zone '" + z.getTag() + "' to automatically reset evey " + z.getOnMinutesFormat();
					}
					
					return true;
					
				} else if ( args[2].equalsIgnoreCase( "interact") ) {
					
					if ( args.length == 4 ) {
						if ( args[3].equalsIgnoreCase("off") ) {
							this.setInteractEditOff(p);
							execMessage = "Interaction trigger editing off.";
							return true;
						}
					}
					
					this.setInteractEditOn(p);
					execMessage = "Right click object to set trigger.";
					return true;
					
				}
				
				
			}
		} else {
		
			if ( Zones.getZone( args[0] ) == null ) {
				this.createZone(p, args[0] );
				execMessage = "You are now editing a new Zone called " + args[0] + ". /cancel or /save to complete. Set options via /zr set [options]";
				Markers.showZoneBoundaries(p, this.getEditZone(p) );
			} else {
				this.setEditZone(p,new Zone( Zones.getZone( args[0] ), args[0]) );
				execMessage = "You are now editing zone " + args[0] + ". /cancel or /save to end edit mode. Set options via /zr set [options]";
				Markers.showZoneBoundaries(p, this.getEditZone(p) );
			}
			
			
			
		}
			
		return true;
		
	}
	


}