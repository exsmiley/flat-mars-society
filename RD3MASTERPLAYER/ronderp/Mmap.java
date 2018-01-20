package ronderp;
import java.util.*;
import bc.*;


public class Mmap {
	public int width;
	public int height;
	public int[][] arr;
	public GameController gc;
	
	//w is map width, h is map height
	public Mmap(int w, int h, GameController gc) {
		this.width = w;
		this.height = h;
		this.gc = gc;
		this.arr = new int[h][w];
	}
	
	public Boolean onMap(MapLocation loc) {
		if (loc.getX()<0 || loc.getY()<0 || loc.getX()>=this.width || loc.getY()>=this.height) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the value at target location
	 * @param loc A MapLocation
	 * @return If the MapLocation is on the map, the value, else -1
	 */
	public int get(MapLocation loc) {
		if (!onMap(loc)) {
			return -1;
		}
		return this.arr[loc.getY()][loc.getX()];
	}
	
	/**
	 * Sets the value at target location
	 * @param loc A MapLocation you want to set
	 * @param val The int Value you want to set it
	 */
	public void set(MapLocation loc, int val) {
		this.arr[loc.getY()][loc.getX()] = val;
	}
	
	/**
	 * Adds a disk centered on loc with radius squared r2 with the int val
	 * @param loc A MapLocation that is the center of the disk
	 * @param r2 A int that is the radius squared
	 * @param val The int you want added to the map in this disk shape
	 */
	public void addDisk(MapLocation loc, int r2, int val) {
		VecMapLocation locs = gc.allLocationsWithin(loc,r2);
		for (int i = 0; i < locs.size(); i++) {
			MapLocation location = locs.get(i);
			if (onMap(location)) {
				set(location, get(location) + val);
			}
		}
	}
	
	/**
	 * Multiplies two maps together.
	 * Useful when you want to cut values from one map by multiplying by a map with 0's, -1's and 1's.
	 * @param mmap2 The secondary map you want to multiply by
	 */
	public void multiply(Mmap mmap2) {
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				MapLocation ml = new MapLocation(Planet.Earth,j,i);
				set(ml, get(ml)*mmap2.get(ml));
			}
		}
	}
	
	/**
	 * Finds the location with the best location value within a radius around a target location.
	 * @param location The location you want to look around for the best value
	 * @param r2 The radius squared of the area you want to look in
	 * @return The best MapLocation, or null if all locations are negative valued
	 */
	public MapLocation findBest(MapLocation location, int r2) {
		VecMapLocation locs = gc.allLocationsWithin(location, r2);
		int bestAmt = 0;
		MapLocation bestLoc = null;
		for (int i = 0; i < locs.size(); i++) {
			MapLocation loc = locs.get(i);
			int amt = get(loc);
			if (amt > bestAmt) {
				bestAmt = amt;
				bestLoc = loc;
			}
			
		}
		//can't return two things, so just call get() on this location
		//also note that bestLoc could be null! check for that.
		return bestLoc;
	}
	
	/**
	 * Prints out the map in hopefully the right order
	 */
	public void printout() {
		for (int y = this.arr.length-1; y > 0; y--) {
			for (int x = 0; x < this.arr[0].length; x++) {
				System.out.print(this.arr[y][x]+ " ");
			}
			System.out.print("\n");
			
		}
	}
	
	
}
