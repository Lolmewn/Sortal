package nl.lolmen.sortal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

public class SPlayerListener implements Listener{
	
	private Main plugin;
	
	public SPlayerListener(Main main){
		plugin = main;
	}
	
	private Main getPlugin(){
		return this.plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
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
			if(this.getPlugin().isDebug()){
				System.out.println("[Sortal - Debug] Sign clicked");
			}
			Sign s = (Sign)b.getState();
			String[] lines = s.getLines();
			int sortalLine = -1;
			for(int i = 0; i < s.getLines().length; i++){
				if(lines[i].equalsIgnoreCase("[Sortal]") || lines[i].equalsIgnoreCase(this.getPlugin().signContains)){
					sortalLine = i;
					break; //returns at the first sign that has [Sortal] or signContains
				}
			}
			if(this.getPlugin().isDebug()){
				System.out.println("[Sortal - Debug] Line with identifier: " + sortalLine);
			}
			if(sortalLine == -1){
				//Always the possibility of a registered sign
				Location c = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
				if(this.getPlugin().isDebug()){
					System.out.println("[Sortal - Debug] looking up location: " + c);
				}
				if(this.getPlugin().loc.containsKey(c)){
					//It's a registered warp sign
					if(!event.getPlayer().hasPermission("sortal.warp")){
						if(this.getPlugin().isDebug()){
							System.out.println("[Sortal - Debug] No perms.. Needed perm: sortal.warp");
						}
						p.sendMessage(this.getPlugin().noPerm);
						event.setCancelled(true);
						return;
					}
					String warp = this.getPlugin().loc.get(c);
					if(this.getPlugin().isDebug()){
						System.out.println("[Sortal - Debug] Found it, warp=" + warp);
					}
					if(!this.getPlugin().warp.containsKey(warp)){
						if(this.getPlugin().isDebug()){
							System.out.println("[Sortal - Debug] But that warp doesn't even exist O.o");
						}
						p.sendMessage("[Sortal] This sign pointer is broken! Ask an Admin to fix it!");
						event.setCancelled(true);
						return;
					}
					Warp d = this.getPlugin().warp.get(warp);
					if(!pay(p,d)){
						return;
					}
					Location goo = new Location(d.getWorld(), d.getX(), d.getY(), d.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
					goo.getChunk().load();
					if(d.getWorld().getName().equals(p.getWorld().getName())){
						p.teleport(goo);
					}else{
						p.teleport(goo);
						p.teleport(goo);
					}
					p.sendMessage("You teleported to " + ChatColor.RED +  warp + "!");
				}
				return;
			}
			if(!event.getPlayer().hasPermission("sortal.warp")){
				if(this.getPlugin().isDebug()){
					System.out.println("[Sortal - Debug] No perms.. Needed perm: sortal.warp");
				}
				p.sendMessage(this.getPlugin().noPerm);
				event.setCancelled(true);
				return;
			}
			String line2 = (lines[sortalLine+1] == null ? "" : lines[sortalLine+1]);
			if(this.getPlugin().isDebug()){
				System.out.println("[Sortal - Debug] line after that: " + line2);
			}
			if(line2.startsWith("w:")){
				if(this.getPlugin().isDebug()){
					System.out.println("[Sortal - Debug] Starts with w:");
				}
				String[] split = line2.split(":");
				String warp = split[1];
				if(!this.getPlugin().warp.containsKey(warp)){
					p.sendMessage("This warp does not exist!");
					event.setCancelled(true);
					return;
				}
				Warp d = this.getPlugin().warp.get(warp);
				if(!pay(p, d)){
					event.setCancelled(true);
					return;
				}
				Location loc = new Location(d.getWorld(), d.getX(), d.getY(), d.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
				loc.getChunk().load();
				if(loc.getWorld().getName().equals(p.getWorld().getName())){
					p.teleport(loc);
				}else{
					p.teleport(loc); //teleport to that world --\/ Teleport to exact location
					p.teleport(loc); //For some reason, you have to teleport twice to end up at the correct spot.
				}
				p.sendMessage("You teleported to " + ChatColor.RED + d.warp() +"!");
				event.setCancelled(true);
				return;
			}
			if(line2.contains(",")){
				if(!pay(p,this.getPlugin().warpUsePrice)){
					event.setCancelled(true);
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
						loc.getChunk().load();
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
						World world = this.getPlugin().getServer().getWorld(split[0]);
						Location loc = new Location(world, x, y, z, p.getLocation().getYaw(), p.getLocation().getPitch());
						loc.getChunk().load();
						p.teleport(loc);
						p.sendMessage("You teleported to " + ChatColor.RED + line2 + "!");
					}
				}
				event.setCancelled(true);
				return;
			}
			//Has [Sortal], but not w: or a , in secondline.
			p.sendMessage("[Sortal] There's something wrong with this sign..");
		}
	}

	private boolean pay(Player p, int money) {
		if(!this.getPlugin().useVault){
			if(this.getPlugin().isDebug()){
				System.out.println("[Sortal - Debug] Not using vault");
			}
			return true;
		}
		Economy econ;
		RegisteredServiceProvider<Economy> rsp = this.getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null){
			if(this.getPlugin().isDebug()){
				System.out.println("[Sortal - Debug] Can't find vault");
			}
			return true; //No Vault found
		}
		econ = rsp.getProvider();
		if(econ == null){
			if(this.getPlugin().isDebug()){
				System.out.println("[Sortal - Debug] Can't find vault");
			}
			return true; //No Vault found
		}
		if(money == 0){
			return true;
		}
		if(!econ.has(p.getName(), money)){
			if(this.getPlugin().isDebug()){
				System.out.println("[Sortal - Debug] Not enough money..");
			}
			p.sendMessage("Sorry, but you don't have enough money to do this!");
			p.sendMessage("Money needed: " + econ.format(money));
			return false;
		}
		econ.withdrawPlayer(p.getName(), money);
		p.sendMessage("Withdrawing " + econ.format(money) + " from your account!");
		if(this.getPlugin().isDebug()){
			System.out.println("[Sortal - Debug] Payed - Warp power granted!");
		}
		return true;
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
		if(this.getPlugin().register.containsKey(p)){
			String warp = this.getPlugin().register.get(p);
			try{
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(this.getPlugin().locs);
				prop.load(in);
				prop.setProperty(b.getWorld().getName() +"," +  Integer.toString(b.getX()) +"," + Integer.toString(b.getY()) + "," + Integer.toString(b.getZ()), warp);
				FileOutputStream out = new FileOutputStream(this.getPlugin().locs);
				prop.store(out, "[World],[X],[Y],[Z]=[Warpname]");
				out.flush();
				out.close();
				in.close();
				this.getPlugin().loc.put(b.getLocation(), warp);
			}catch(IOException e){
				e.printStackTrace();
			}
			this.getPlugin().register.remove(p);
			p.sendMessage("Registered!");
			return true;
		}
		if(this.getPlugin().cost.containsKey(p)){
			int cost = this.getPlugin().cost.get(p);
			if(b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN){
				Sign s = (Sign)b.getState();
				Location loc = b.getLocation();
				if(this.getPlugin().loc.containsKey(loc)){
					//Sign is a registered one.
					String warp = this.getPlugin().loc.get(loc);
					if(this.getPlugin().warp.containsKey(warp)){
						Warp d = this.getPlugin().warp.get(warp);
						d.setCost(cost);
						p.sendMessage("Cost set to " + ChatColor.RED + Integer.toString(cost) + "!");
						this.getPlugin().cost.remove(p);
					}else{
						p.sendMessage("This sign has been registered, but it's pointing to a non-existing warp!");
					}
				}else{
					String[] lines = s.getLines();
					if(lines[0].equalsIgnoreCase(this.getPlugin().signContains) || lines[0].equalsIgnoreCase("[Sortal]")){
						String line2 = lines[1];
						if(line2.contains("w:")){
							String[] splot = line2.split(":");
							String warp = splot[1];
							Warp d = this.getPlugin().warp.get(warp);
							d.setCost(cost);
							p.sendMessage("Cost set to " + ChatColor.RED + Integer.toString(cost) + "!");
							this.getPlugin().cost.remove(p);
						}
					}else
						if(lines[1].equalsIgnoreCase(this.getPlugin().signContains) || lines[1].equalsIgnoreCase("[Sortal]")){
							String line2 = lines[2];
							if(line2.contains("w:")){
								String[] splot = line2.split(":");
								String warp = splot[1];
								if(!this.getPlugin().warp.containsKey(warp)){
									p.sendMessage("This sign is pointing to " +ChatColor.RED +  warp +ChatColor.WHITE +  ", a non-existant warp!");
								}
								Warp d = this.getPlugin().warp.get(warp);
								d.setCost(cost);
								p.sendMessage("Cost set to " + ChatColor.RED + Integer.toString(cost) + "!");
								this.getPlugin().cost.remove(p);
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
		if(this.getPlugin().unreg.contains(p)){
			if(this.getPlugin().loc.containsKey(b.getLocation())){
				try {
					Properties prop = new Properties();
					prop.load(new FileInputStream(this.getPlugin().locs));
					prop.remove(b.getLocation());
					prop.store(new FileOutputStream(this.getPlugin().locs), "[Location] = [Name]");
					p.sendMessage("Deletion completed!");
					this.getPlugin().unreg.remove(p);
					this.getPlugin().loc.remove(b.getLocation());
				} catch (Exception e) {
					e.printStackTrace();
					p.sendMessage("Something went wrong while deleting the warp. :O");
				}
			}else{
				p.sendMessage("This sign is not registered!");
			}
			return true;
		}
		return false;
	}


	public boolean pay(Player p, Warp d) {
		return this.pay(p, d.getCost());
	}
}
