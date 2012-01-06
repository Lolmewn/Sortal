package nl.lolmen.sortal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.iConomy.system.Account;
import com.iConomy.system.Holdings;

public class SPlayerListener extends PlayerListener{
	public Main plugin;
	public SPlayerListener(Main main){
		plugin = main;
	}


	public void onPlayerInteract(PlayerInteractEvent event){
		Player p = event.getPlayer();
		Action a = event.getAction();
		if(a.equals(Action.LEFT_CLICK_BLOCK)){
			if(leftClick(p, event.getClickedBlock())){
				event.setCancelled(true);
			}
			return;
		} 
		if(!a.equals(Action.RIGHT_CLICK_BLOCK)){
			return;
		}
		Block b = event.getClickedBlock();
		if(b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN){
			Sign s = (Sign)b.getState();
			String[] lines = s.getLines();
			if(lines[0].equalsIgnoreCase(plugin.signContains) || lines[0].equalsIgnoreCase("[Sortal]")){
				if(event.getPlayer().hasPermission("sortal.warp")){
					String line2 = lines[1];
					if(line2.startsWith("w:")){
						String[] split = line2.split(":");
						String warp = split[1];
						if(!plugin.warp.containsKey(warp)){
							p.sendMessage("This warp does not exist!");
							return;
						}
						Warp d = plugin.warp.get(warp);
						if(!pay(p, d)){
							return;
						}
						if(d.getWorld().equals(p.getLocation().getWorld())){
							p.teleport(new Location(d.getWorld(), d.getX(), d.getY(), d.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
						}else{
							p.teleport(new Location(d.getWorld(), 0, 100, 0));
							p.teleport(new Location(d.getWorld(), d.getX(), d.getY(), d.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
						}
						p.sendMessage("You teleported to " + ChatColor.RED + d.warp() +"!");

					}else{
						if(line2.contains(",")){
							Warp d = new Warp(plugin);
							if(!pay(p,d)){
								return;
							}
							String[] split = line2.split(",");
							if(split.length == 3){
								//No world specified, using Players World
								if(isInt(split[0]) && isInt(split[1]) && isInt(split[2])){
									int x = Integer.parseInt(split[0]);
									int y = Integer.parseInt(split[1]);
									int z = Integer.parseInt(split[2]);
									Location loc = new Location(p.getWorld(), x, y, z, p.getLocation().getYaw(), p.getLocation().getPitch());
									p.teleport(loc);
									p.sendMessage("You teleported to " + ChatColor.RED + line2 + "!");
								}
							}
							if(split.length == 4){
								//World specified. Probally.
								if(isInt(split[3]) && isInt(split[1]) && isInt(split[2])){
									int x = Integer.parseInt(split[1]);
									int y = Integer.parseInt(split[2]);
									int z = Integer.parseInt(split[3]);
									World world = plugin.getServer().getWorld(split[0]);
									Location loc = new Location(world, x, y, z, p.getLocation().getYaw(), p.getLocation().getPitch());
									p.teleport(loc);
									p.sendMessage("You teleported to " + ChatColor.RED + line2 + "!");
								}
							}
						}else{
							//Has [Sortal], but not w: or a , in secondline.
							p.sendMessage("[Sortal] There's something wrong with this sign..");
						}
					}
				}else{
					p.sendMessage(plugin.noPerm);
				}
			}else if(lines[1].equalsIgnoreCase(plugin.signContains) || lines[1].equalsIgnoreCase("[sortal]")){
				if(event.getPlayer().hasPermission("sortal.warp")){
					String line2 = lines[2];
					if(line2.startsWith("w:")){
						String[] split = line2.split(":");
						String warp = split[1];
						if(!plugin.warp.containsKey(warp)){
							p.sendMessage("This warp does not exist!");
							return;
						}
						Warp d = plugin.warp.get(warp);
						if(!pay(p,d)){
							return;
						}
						if(d.getWorld().equals(p.getLocation().getWorld())){
							p.teleport(new Location(d.getWorld(), d.getX(), d.getY(), d.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
						}else{
							p.teleport(new Location(d.getWorld(), 0, 100, 0));
							p.teleport(new Location(d.getWorld(), d.getX(), d.getY(), d.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
						}
						p.sendMessage("You teleported to " + ChatColor.RED + d.warp());
					}else{
						if(line2.contains(",")){
							String[] split = line2.split(",");
							if(split.length == 3){
								//No world specified, using Players World
								if(isInt(split[0]) && isInt(split[1]) && isInt(split[2])){
									int x = Integer.parseInt(split[0]);
									int y = Integer.parseInt(split[1]);
									int z = Integer.parseInt(split[2]);
									Location loc = new Location(p.getWorld(), x, y, z, p.getLocation().getYaw(), p.getLocation().getPitch());
									p.teleport(loc);
									p.sendMessage("You teleported to " + ChatColor.RED + line2 + "!");
								}
							}
							if(split.length == 4){
								//World specified. Probally.
								if(isInt(split[3]) && isInt(split[1]) && isInt(split[2])){
									int x = Integer.parseInt(split[1]);
									int y = Integer.parseInt(split[2]);
									int z = Integer.parseInt(split[3]);
									World world = plugin.getServer().getWorld(split[0]);
									Location loc = new Location(world, x, y, z, p.getLocation().getYaw(), p.getLocation().getPitch());
									p.teleport(loc);
									p.sendMessage("You teleported to " + ChatColor.RED + line2 + "!");
								}
							}
						}
					}
				}
			}else{
				if(event.getPlayer().hasPermission("sortal.warp")){
					Location c = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
					if(plugin.loc.containsKey(c)){
						String warp = plugin.loc.get(c);
						if(!plugin.warp.containsKey(warp)){
							p.sendMessage("[Sortal] This sign pointer is broken! Ask an Admin to fix it!");
							return;
						}
						Warp d = plugin.warp.get(warp);
						Location goo = new Location(d.getWorld(), d.getX(), d.getY(), d.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
						if(d.getWorld().equals(p.getWorld())){
							p.teleport(goo);
						}else{
							p.teleport(goo);
							p.teleport(goo);
						}
						p.sendMessage("You teleported to " + ChatColor.RED +  warp + "!");
					}
				}
			}
		}
	}

	public boolean isInt(String i){
		try{
			Integer.parseInt(i);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

	private boolean leftClick(Player p, Block b) {
		if(plugin.register.containsKey(p)){
			String warp = plugin.register.get(p);
			try{
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(plugin.locs);
				prop.load(in);
				prop.setProperty(b.getWorld().getName() +"," +  Integer.toString(b.getX()) +"," + Integer.toString(b.getY()) + "," + Integer.toString(b.getZ()), warp);
				FileOutputStream out = new FileOutputStream(plugin.locs);
				prop.store(out, "[World],[X],[Y],[Z]=[Warpname]");
				out.flush();
				out.close();
				in.close();
				plugin.loc.put(b.getLocation(), warp);
			}catch(IOException e){
				e.printStackTrace();
			}
			plugin.register.remove(p);
			p.sendMessage("Registered!");
			return true;
		}
		if(plugin.cost.containsKey(p)){
			int cost = plugin.cost.get(p);
			if(b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN){
				Sign s = (Sign)b.getState();
				Location loc = b.getLocation();
				if(plugin.loc.containsKey(loc)){
					//Sign is a registered one.
					String warp = plugin.loc.get(loc);
					if(plugin.warp.containsKey(warp)){
						Warp d = plugin.warp.get(warp);
						d.setCost(cost);
						p.sendMessage("Cost set to " + ChatColor.RED + Integer.toString(cost) + "!");
						plugin.cost.remove(p);
					}else{
						p.sendMessage("This sign has been registered, but it's pointing to a non-existing warp!");
					}
				}else{
					String[] lines = s.getLines();
					if(lines[0].equalsIgnoreCase(plugin.signContains) || lines[0].equalsIgnoreCase("[Sortal]")){
						String line2 = lines[1];
						if(line2.contains("w:")){
							String[] splot = line2.split(":");
							String warp = splot[1];
							Warp d = plugin.warp.get(warp);
							d.setCost(cost);
							p.sendMessage("Cost set to " + ChatColor.RED + Integer.toString(cost) + "!");
							plugin.cost.remove(p);
						}
					}else
						if(lines[1].equalsIgnoreCase(plugin.signContains) || lines[1].equalsIgnoreCase("[Sortal]")){
							String line2 = lines[2];
							if(line2.contains("w:")){
								String[] splot = line2.split(":");
								String warp = splot[1];
								if(!plugin.warp.containsKey(warp)){
									p.sendMessage("This sign is pointing to " +ChatColor.RED +  warp +ChatColor.WHITE +  ", a non-existant warp!");
								}
								Warp d = plugin.warp.get(warp);
								d.setCost(cost);
								p.sendMessage("Cost set to " + ChatColor.RED + Integer.toString(cost) + "!");
								plugin.cost.remove(p);
							}
						}else{
							p.sendMessage("This sign is not usable for setting a price!");
						}
				}
			}else{
				p.sendMessage("[Sortal] This is not a sign, cannot set cost!");
			}
			return true;
		}
		if(plugin.unreg.containsKey(p)){
			if(plugin.loc.containsKey(b.getLocation())){
				try {
					Properties prop = new Properties();
					prop.load(new FileInputStream(plugin.locs));
					prop.remove(b.getLocation());
					prop.store(new FileOutputStream(plugin.locs), "[Location] = [Name]");
					p.sendMessage("Deletion completed!");
					plugin.unreg.remove(p);
					plugin.loc.remove(b.getLocation());
				} catch (Exception e) {
					e.printStackTrace();
					p.sendMessage("Something went wrong while deleting the warp.");
				}
			}else{
				p.sendMessage("This sign is not registered!");
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public boolean pay(Player p, Warp d) {
		double money = d.getCostDouble();
		if(plugin.iCo != null){
			Account acc = plugin.iCo.getAccount(p.getName());
			if(acc != null){
				Holdings h = acc.getHoldings();
				if(h.hasEnough(money)){
					h.subtract(money);
					p.sendMessage("This has cost you " + plugin.iCo.format(money));
					return true;
				}else{
					p.sendMessage("You do not have enough money to warp!");
					return false;
				}
			}else{
				p.sendMessage("Your iConomy account seems to be empty! Try using /money!");
				return false;
			}
		}
		return true;
	}
}
