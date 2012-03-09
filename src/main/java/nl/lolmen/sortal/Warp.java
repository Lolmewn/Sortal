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
	private String warp;
	private World world;
	private double x;
	private double y;
	private double z;
	private Main plugin;
	private int cost;
	private boolean hasCost = false;

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
	public Warp(Main main, String warp, World world, double x, double y, double z, int price){
		this(main, warp, world, x, y, z);
		this.cost = price;
		this.setHasCost(true);
	}
	public Warp(Main main, String warp, Location loc){
		this(main, warp, loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
	}
	public Warp(Main main, String warp, Location loc, int cost){
		this(main, warp, loc);
		this.cost = cost;
		this.hasCost = true;
	}

	/**
	 * Saves the warp
	 */
	public void saveWarp() {
		try {
			if(!this.warps.exists()){
				this.warps.createNewFile();
			}
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(this.warps);
			prop.load(in);
			prop.put(warp(), toString());
			FileOutputStream out = new FileOutputStream(this.warps);
			prop.store(out, "[WarpName]=[World],[X],[Y],[Z]");
			this.plugin.warp.put(warp(), new Warp(this.plugin, this.warp, this.world, this.x,this.y,this.z));
			in.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean delWarp(){
		try{
			if(!this.warps.exists()){
				this.warps.createNewFile();
				return false;
			}
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(this.warps);
			prop.load(in);
			if(prop.containsKey(this.warp)){
				prop.remove(this.warp);
				FileOutputStream out = new FileOutputStream(this.warps);
				prop.store(out, "[WarpName]=[World],[X],[Y],[Z]");
				this.plugin.warp.remove(this.warp);
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
		try{
			if(!this.warps.exists()){
				this.warps.createNewFile();
				return false;
			}
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(warps);
			prop.load(in);
			if(prop.containsKey(this.warp)){
				prop.setProperty(this.warp, this.toString());
				FileOutputStream out = new FileOutputStream(warps);
				prop.store(out, "[WarpName]=[World],[X],[Y],[Z]");
				this.plugin.warp.remove(this.warp);
				this.plugin.warp.put(this.warp, new Warp(this.plugin, this.warp, new Location(this.getWorld(), this.getX(), this.getY(), this.getZ()), this.cost));
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
	public int getCost(){
		if(this.cost == 0){
			return this.plugin.warpUsePrice;
		}else{
			return this.cost;
		}
	}
	public String warp(){
		return this.warp;
	}
	public World getWorld(){
		return this.world;
	}
	public double getX(){
		return this.x;
	}
	public double getY(){
		return this.y;
	}
	public double getZ(){
		return this.z;
	}
	public String toString(){
		return this.world.getName() + "," + Double.toString(this.x) + "," + Double.toString(this.y) + "," + Double.toString(this.z) + "," + (hasCost()?getCost():0);
	}
	public boolean hasCost() {
		return hasCost;
	}
	public void setHasCost(boolean hasCost) {
		this.hasCost = hasCost;
	}
}
