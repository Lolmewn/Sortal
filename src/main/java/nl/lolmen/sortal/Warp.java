package nl.lolmen.sortal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.Location;
import org.bukkit.World;

public class Warp{	
	public String maindir = "plugins/Sortal/";
	public File warps = new File(maindir + "warps.txt");
	String warp;
	World world;
	double x;
	double y;
	double z;
	public Main plugin;
	int cost;
	
	public Warp(Main main) {
		plugin = main;
	}
	public Warp(Main main, String warp, World world, double x, double y, double z){
		this(main);
		this.warp = warp;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Warp(Main main, String warp, Location loc){
		this(main, warp, loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
	}
	public Warp(Main main, String warp, Location loc, int cost){
		this(main, warp, loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		this.cost = cost;
	}
	
	/**
	 * Saves the warp
	 */
	public void saveWarp() {
		//if(!plugin.useSQL && !plugin.useMySQL){
			try {
				if(!warps.exists()){
					warps.createNewFile();
				}
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(warps);
				prop.load(in);
				prop.put(warp(), toString());
				FileOutputStream out = new FileOutputStream(warps);
				prop.store(out, "[WarpName]=[World],[X],[Y],[Z]");
				plugin.warp.put(warp(), new Warp(plugin, warp, world, x,y,z));
				in.close();
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		//}
		/*if(plugin.useSQL){
			plugin.sql.checkConnection();
			plugin.sql.query("INSERT INTO Sortal (name, world, x,y,z,warp,cost) VALUES ('" + warp + "', '" + world + "', " + x + ", " + y + ", " + z + ", 1, 0;");
			plugin.warp.put(warp(), new Warp(plugin, warp, world, x,y,z));
		}
		if(plugin.useMySQL){
			plugin.mysql.checkConnection();
			try {
				plugin.mysql.query("INSERT INTO Sortal (name, world, x,y,z,warp,cost) VALUES ('" + warp + "', '" + world + "', " + x + ", " + y + ", " + z + ", 1, 0;");
				plugin.warp.put(warp(), new Warp(plugin, warp, world, x,y,z));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
	}
	public boolean delWarp(){
		//if(!plugin.useSQL && !plugin.useMySQL){
			try{
				if(!warps.exists()){
					warps.createNewFile();
					return false;
				}
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(warps);
				prop.load(in);
				if(prop.containsKey(warp)){
					prop.remove(warp);
					FileOutputStream out = new FileOutputStream(warps);
					prop.store(out, "[WarpName]=[World],[X],[Y],[Z]");
					plugin.warp.remove(warp);
					out.flush();
					out.close();
					in.close();
					return true;
				}
				in.close();
				return false;
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
	}
	public boolean setCost(int cost){
		//if(!plugin.useSQL && !plugin.useMySQL){//
			try{
				if(!warps.exists()){
					warps.createNewFile();
					return false;
				}
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(warps);
				prop.load(in);
				if(prop.containsKey(warp)){
					prop.setProperty(warp, prop.getProperty(warp) + "," + cost);
					FileOutputStream out = new FileOutputStream(warps);
					prop.store(out, "[WarpName]=[World],[X],[Y],[Z]");
					plugin.warp.remove(warp);
					plugin.warp.put(warp, new Warp(plugin, warp, new Location(getWorld(), getX(), getY(), getZ()), cost));
					out.flush();
					out.close();
					in.close();
					return true;
				}
				in.close();
				return false;
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
		//}
		/*if(plugin.useSQL){
			plugin.sql.checkConnection();
			plugin.sql.query("UPDATE Sortal SET cost = " + cost+ " WHERE warp = '"+warp+ "';");
			return true;
		}else{
			plugin.mysql.checkConnection();
			try {
				plugin.sql.query("UPDATE Sortal SET cost = " + cost+ " WHERE warp = '"+warp+ "';");
			}  catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}*/
	}
	public int getCost(){
		if(cost == 0){
			return plugin.warpUsePrice;
		}else{
			return cost;
		}
	}
	public double getCostDouble(){
		if(cost == 0){
			return (double)plugin.warpUsePrice;
		}else{
			return (double)cost;
		}
	}
	public String warp(){
		return warp;
	}
	public World getWorld(){
		return world;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public double getZ(){
		return z;
	}
	public String toString(){
		return world.getName() + "," + Double.toString(x) + "," + Double.toString(y) + "," + Double.toString(z);
	}
}
