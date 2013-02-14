package com.github.mineGeek.ZoneReset.Commands;

import org.bukkit.entity.Player;

import com.github.mineGeek.ZoneReset.ZoneReset;
import com.github.mineGeek.ZoneReset.Utilities.Zone;

public class Set extends CommandBase {

	public Set( ZoneReset plugin) {
		super(plugin);
	}

	/**
	 * Allows setting of zone properties in game.
	 */
	@Override
	protected Boolean exec( String cmdName, String[] args ) {
		
		if ( !(sender instanceof Player) ) {
			mess = "You cannot use this command from the console.";
			return true;
		}
		
		Player p = (Player) sender;
		Zone z = this.getEditZone(p);
		
		if ( z == null ) {
			mess = "Dude, you need to be editing a zone. Right now you aren't. Type /edit ZONENAME to get started";
			return true;
		}
		
		if ( args.length < 1 ) {
			mess = "Too few arguments.";
			return false;
		}
		
		
		String key = args[0].toLowerCase();
		
		if ( key.equals( "world" ) ) {
			
			z.setWorldName( args[1] );
			
			mess = z.getTag() + " world set to " + args[1];
			
		} else if ( key.equals("zone") ) {
			
			if ( args.length == 1 ) {
				
				this.setAreaEdit( p , !this.getAreaEdit(p));
				
			} else if ( args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true") ) {
				
				this.setAreaEdit(p, true );
				
			} else {
				
				this.setAreaEdit(p, false );
			}
				
			mess = z.getTag() + " area edit is now " + ( this.getAreaEdit(p) ? " ON " : " OFF");
			
		} else if ( key.equals( "req") ) {
			
			if ( args.length < 2 ) {
				mess = "Incorrect number of parameters";
				return false;
			}
			
			String noun = args[1].toLowerCase();
			
			if ( noun.equals( "players" ) ) {
				
				if ( args.length == 2 ) {
					z.setRequireNoPlayers( !z.isRequireNoPlayers() );
				} else if ( args[3].equalsIgnoreCase( "on" ) || args[3].equalsIgnoreCase("true") ) {
					z.setRequireNoPlayers( true );
				} else {
					z.setRequireNoPlayers( false );
				}
				
				mess = z.getTag() + ( !z.isRequireNoPlayers() ? " does not require " : " now requires " ) + "zone to be empty of players";
				
			}
			
		} else if ( key.equals("no") ) {
			
			String noun = args[1].toLowerCase();
			
			if ( noun.equals("mobs") ) {
				
				if ( args.length == 2 ) {
					z.setKillEntities( !z.isKillEntities() );
				} else if ( args[2].equalsIgnoreCase( "on" ) || args[2].equalsIgnoreCase("true") ) {
					z.setKillEntities( true );
				} else {
					z.setKillEntities( false );
				}
				
				mess = z.getTag() + ( !z.isKillEntities() ? " will remove " : " will NOT remove " ) + "mobs in zone.";				
				
			} else if ( noun.equals("spawns") ) {
				
				if ( args.length == 2 ) {
					z.setRemoveSpawnPoints( !z.isPreNoSpawns() );
				} else if ( args[2].equalsIgnoreCase( "on" ) || args[2].equalsIgnoreCase("true") ) {
					z.setRemoveSpawnPoints( true );
				} else {
					z.setRemoveSpawnPoints( false );
				}
				
				mess = z.getTag() + ( z.isPreNoSpawns() ? " will remove " : " will NOT remove " ) + "any online player spawn points in zone.";
				
			}
			
			
		} else if ( key.equals("location") || key.equals("loc") ) {
			
			if ( args.length == 2 ) {
				
				if ( args[1].equalsIgnoreCase( "move" ) ) {
					z.setTransportPlayers( p.getWorld().getName(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ() );
					mess = "Any players in '" + z.getTag() + "' will be moved to your current location on reset";
					return true;
					
				} else if ( args[1].equalsIgnoreCase( "spawn") ) {
					
					z.setResetSpawnPoints( p.getWorld().getName(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ() );
					mess = "Any players in '" + z.getTag() + "' will have their spawn point set to here on reset.";
					return true;
					
				}
				
			} else if ( args.length == 3 && args[2].equalsIgnoreCase( "none" ) ) {
				
				if ( args[1].equalsIgnoreCase( "move" ) ) {
					z.setTransportPlayers( null, 0, 0, 0 );
					
					mess = "Any players in '" + z.getTag() + "' will be no longer be moved on reset.";
					return true;
					
				} else if ( args[1].equalsIgnoreCase( "spawn") ) {
					
					z.setResetSpawnPoints(  null, 0, 0, 0 );
					mess = "Any players in '" + z.getTag() + "' will no longer have spawn points reset.";
					return true;
					
				}				
				
			}
			
			if ( args.length < 6 ) {
				
				mess = "Invalid number of arguments. Expected: /set loc NOUN ACTION x y z";
				return false;
				
			}
			
			String 	noun 		= args[2].toLowerCase();
			String 	action 		= args[3].toLowerCase();
			Integer locx 		= Integer.parseInt( args[4] );
			Integer locy 		= Integer.parseInt( args[5] );
			Integer locz 		= Integer.parseInt( args[6] );
			
			if ( noun.equals("move") ) {
				
				if ( action.equals( "players") ) {

					z.setTransportPlayers( z.getWorldName(), locx, locy, locz);
					mess = "Any players in '" + z.getTag() + "' will be moved to " + locx + ", " + locy + ", " + locz + " when zone resets.";
					
				} else if ( action.equals("spawn") ) {

					z.setResetSpawnPoints( z.getWorldName(), locx, locy, locz);
					mess = "Any players in '" + z.getTag() + "' will have their spawn set to " + locx + ", " + locy + ", " + locz + " when zone resets.";					
					
				}
				
			} else if ( key.equals("trigger" ) ) {
				
				if ( args.length < 3 ) {
					
					mess = "Invalid number of arguments";
					return false;
				}
				
				String type = args[2].toLowerCase();
				
				if ( type.equals("join") ) {
					
					if ( args.length == 2 ) {
						
						z.setOnPlayerJoin( !z.isOnPlayerJoin() );
						mess = z.getTag() + " will " + ( z.isOnPlayerJoin() ? "now" : "no longer") + " auto reset when a player joins.";
						
					} else if ( args.length > 2 ) {
						
						if ( args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("on") ) {
							z.setOnPlayerJoin( true );
							mess = z.getTag() + " will now auto reset when a player joins.";
						} else if ( args[2].equalsIgnoreCase( "false" ) || args[2].equalsIgnoreCase("off" ) ) {
							z.setOnPlayerJoin( false );
							mess = z.getTag() + " will no longer auto reset when a player joins.";
						} else if ( args[2].equalsIgnoreCase("add" ) && args.length > 3) {
							
							for ( int x = 3; x < args.length; x++ ) {
								z.getOnPlayerJoinList().remove( args[x] );
								z.getOnPlayerJoinList().add( args[x] );
								p.sendMessage( "Player " + args[x] + " will now reset zone '" + z.getTag() + "' when they join." );
							}								
							
							
						} else if ( args[2].equalsIgnoreCase("remove") && args.length > 2 ) {
							
							for ( int x = 3; x < args.length; x++ ) {
								z.getOnPlayerJoinList().remove( args[x] );
								p.sendMessage( "Player " + args[x] + " will no longer reset zone '" + z.getTag() + "' when they join." );
							}							
							
						} else if ( args[2].equalsIgnoreCase( "clear") ) {
							
							z.getOnPlayerJoinList().clear();
							mess = z.getTag() + " player reset onJoin list cleared.";
							
						}
						
					} else if ( type.equals("quit") ) {
						
						if ( args.length == 2 ) {
							
							z.setOnPlayerQuit( !z.isOnPlayerQuit() );
							mess = z.getTag() + " will " + ( z.isOnPlayerQuit() ? "now" : "no longer") + " auto reset when a player leaves.";
							
						} else if ( args.length > 2 ) {
							
							if ( args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("on") ) {
								z.setOnPlayerQuit( true );
								mess = z.getTag() + " will now auto reset when a player quits.";
							} else if ( args[2].equalsIgnoreCase( "false" ) || args[2].equalsIgnoreCase("off" ) ) {
								z.setOnPlayerQuit( false );
								mess = z.getTag() + " will no longer auto reset when a player quits.";
							} else if ( args[2].equalsIgnoreCase("add" ) && args.length > 3) {
								
								for ( int x = 3; x < args.length; x++ ) {
									z.getOnPlayerQuitList().remove( args[x] );
									z.getOnPlayerQuitList().add( args[x] );
									p.sendMessage( "Player " + args[x] + " will now reset zone '" + z.getTag() + "' when they quit." );
								}								
								
								
							} else if ( args[2].equalsIgnoreCase("remove") && args.length > 2 ) {
								
								for ( int x = 3; x < args.length; x++ ) {
									z.getOnPlayerQuitList().remove( args[x] );
									p.sendMessage( "Player " + args[x] + " will no longer reset zone '" + z.getTag() + "' when they quit." );
								}							
								
							} else if ( args[2].equalsIgnoreCase( "clear") ) {
								
								z.getOnPlayerQuitList().clear();
								mess = z.getTag() + " player reset onQuit list cleared.";
								
							}
							
						}
						
					} else if ( key.equals( "time" ) ) {
						
						if ( args.length == 2 ) {
							mess = "Invalid option. Expect a format of #d#h#m#s (e.g. 2d3m for every 48 hours and 3 minutes)";
							return true;
						}
						
						z.setOnMinutesFormat( args[2] );
						
						if ( z.getOnMinutes() == 0 ) {
							mess = "Failure reading time argument. format should be in days, hours, minutes, seconds #[d|h|m|s]. Example for 24 hours: 1d or 24h. You can combined them all together, eg : /zr set time 1d3h5m0s";
						} else {
							mess = "Set Zone '" + z.getTag() + "' to automatically reset evey " + z.getOnMinutesFormat();
						}
						
						return true;
						
					} else if ( key.equals( "interact") ) {
						
						if ( args.length == 3 ) {
							if ( args[2].equalsIgnoreCase("off") ) {
								this.setInteractEditOff(p);
								mess = "Interaction trigger editing off.";
								return true;
							}
						}
						
						this.setInteractEditOn(p);
						mess = "Right click object to set trigger.";
						return true;
						
					}
					
				}
				
				
			}
			
		}
		
		return true;
	}
}