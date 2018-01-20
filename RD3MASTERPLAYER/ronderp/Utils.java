package ronderp;
import java.util.*;
import bc.*;

public class Utils {
	
	private GameController gc;

	public Utils(GameController gc) {
		this.gc = gc;
	}
	
	/**
	 * Generic method to randomly choose an element from an array
	 * @param arr array to choose an element from
	 * @return some element in arr
	 */
	public static <T> T chooseRandom(T[] arr) {
		return arr[(int) (Math.random()*arr.length)];
	}
	
	public static int randomNum(int big) {
		return (int) (Math.random()*big);
	}
	
	/**
	 * Tries to move the robot North, West, South, East
	 * @param id id of the robot to move
	 */
	public void moveRobotSpiral(int id) {
		if(gc.canMove(id, Direction.North)) {
			gc.moveRobot(id, Direction.North);
		}
		else if(gc.canMove(id, Direction.East)) {
			gc.moveRobot(id, Direction.East);
		}
		else if(gc.canMove(id, Direction.South)) {
			gc.moveRobot(id, Direction.South);
		}
		else if(gc.canMove(id, Direction.West)) {
			gc.moveRobot(id, Direction.West);
		}
	}
	
	/**
	 * Inverting logic to rush enemy spawn
	 * @param earth - the planet object earth
	 * @param invertingLoc - the location to invert
	 * @returns location of new target
	 */
	public MapLocation invertion(PlanetMap earth, MapLocation invertingLoc) {
        int newx = (int)earth.getWidth()-invertingLoc.getX();
        int newy = (int)earth.getHeight()-invertingLoc.getY();
        return new MapLocation(gc.planet().Earth,newx,newy);
    }
	
	/**
	 * Tries to harvest on a square adjacent to the robot.
	 * @param id id of the robot looking to harvest
	 */
	public void harvestSomething(int id) {
	    if (gc.canHarvest(id, Direction.North)) {
	        gc.harvest(id, Direction.North);
	    }
	    else if (gc.canHarvest(id, Direction.South)) {
            gc.harvest(id, Direction.South);
        }
	    else if (gc.canHarvest(id, Direction.West)) {
            gc.harvest(id, Direction.West);
        }
	    else if (gc.canHarvest(id, Direction.East)) {
            gc.harvest(id, Direction.East);
        }
	}
	
	/**
	 * Tries to replicate this robot on an adjacent square if it can
	 * @param id id of the robot to replicate
	 */
	public void replicateSomewhere(int id) {
		if(gc.canReplicate(id, Direction.South)) {
			gc.replicate(id, Direction.South);
		}
		else if(gc.canReplicate(id, Direction.East)) {
			gc.replicate(id, Direction.East);
		}
		else if(gc.canReplicate(id, Direction.North)) {
			gc.replicate(id, Direction.North);
		}
		else if(gc.canReplicate(id, Direction.West)) {
			gc.replicate(id, Direction.West);
		}
	}
	
	/**
	 * Looks at the orbit pattern to get the optimal rocket launching times (>100) and orders them
	 */
	public void planRocketLaunches() {
		// TODO find optimal stuffs
		OrbitPattern pattern = gc.orbitPattern();
		pattern.getPeriod();
	}
	
	/**
	 * Returns the exact opposite direction of the direction passed in.
	 * High quality programming going on here.
	 */
	public static Direction getOppositeDirection(Direction dir) {
	    if (dir.equals(Direction.North)) {
	        return Direction.South;
	    }
	    else if (dir.equals(Direction.Northeast)) {
	        return Direction.Southwest;
	    }
	    else if (dir.equals(Direction.East)) {
            return Direction.West;
        }
	    else if (dir.equals(Direction.Southeast)) {
            return Direction.Northwest;
        }
	    else if (dir.equals(Direction.South)) {
            return Direction.North;
        }
	    else if (dir.equals(Direction.Southwest)) {
            return Direction.Northeast;
        }
	    else if (dir.equals(Direction.West)) {
            return Direction.East;
        }
	    else {
            return Direction.Southeast;
        }
	}
	
	/**
	 * Makes two Mmaps, one with amount of Karbonite and one with passable terrain
	 * @param earth Earth PlanetMap
	 * @return Array [kMap, passableMap]
	 */
	public Mmap[] makeKMapandPassableMap(PlanetMap earth) {
		Mmap passableMap = new Mmap((int)earth.getWidth(), (int)earth.getHeight(), gc);
		Mmap kMap = new Mmap((int)earth.getWidth(),(int)earth.getHeight(), gc);
		for (int y = 0; y < earth.getHeight(); y++) {
			for (int x = 0; x < earth.getWidth(); x++) {
				MapLocation ml = new MapLocation(Planet.Earth,x,y);
				passableMap.set(ml, (int)earth.isPassableTerrainAt(ml));
				kMap.set(ml, (int)earth.initialKarboniteAt(ml));
				
			}
		}
		Mmap[] output = {kMap, passableMap};
		return output;
	}
	
	/**
	 * Gets a list of locations with karbonite in order of closest to farthest
	 * @param earth The map we are looking on (doesnt have to be earth i think)
	 * @param kMap The Mmap that has Karbonite amounts in it
	 * @param location The location you want to compare two when finding them
	 * @return kLocs, a lost of locations with karbonite in order of closest to farthest
	 */
	public ArrayList<MapLocation> getKLocs(PlanetMap earth, Mmap kMap, MapLocation location) {
		
		ArrayList<MapLocation> kLocs = new ArrayList<MapLocation>();
		Mmap evalMap = new Mmap((int)earth.getWidth(), (int)earth.getHeight(),gc);
		ArrayList<MapLocation> currentLocs = new ArrayList<MapLocation>();
		currentLocs.add(location);
		Direction[] directions = {Direction.North, Direction.Northeast, Direction.East, Direction.Southeast, Direction.South, Direction.Southwest, Direction.West, Direction.Northwest};
		
		//This is like some weird BFS looking for locations with karbonite
		while(currentLocs.size()>0) {
			ArrayList<MapLocation> nextLocs = new ArrayList<MapLocation>();
			for (int i = 0; i < currentLocs.size(); i++) {
				MapLocation loc = currentLocs.get(i);
				for (Direction dir : directions) {
					MapLocation newPlace = loc.add(dir);
					if (evalMap.get(newPlace)==0){
						evalMap.set(newPlace, 1);
						nextLocs.add(newPlace);
						if (kMap.get(newPlace)>0){
							kLocs.add(loc);
						}
					}
				}
			}
			currentLocs = nextLocs;
		}
		return kLocs;
	}
	
}
