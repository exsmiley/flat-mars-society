package ronderp;
import java.util.*;
import java.util.stream.Stream;

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
	
	public static <T> T arrayListRandom(ArrayList<T> arr) {
		return arr.get((int)(Math.random()*arr.size()));
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
        int newx = (int)earth.getWidth()-invertingLoc.getX()-1;
        int newy = (int)earth.getHeight()-invertingLoc.getY()-1;
        return new MapLocation(Planet.Earth, newx, newy);
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
	
	/**
	 * Checks to see if a location is on the given planet (doesnt have to be earth)
	 * @param earth The planet Map you are checking on
	 * @param loc The MapLocation you are checking
	 * @return True or False
	 */
	public Boolean onEarth(PlanetMap earth, MapLocation loc) {
		if (loc.getX()<0 || loc.getY()<0 || loc.getX()>=earth.getWidth() || loc.getY()>=earth.getHeight()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determines location to go to get to the adjacent square with the most karbonite
	 * @param earth planet we are on
	 * @param loc Location we are searching from
	 * @return A Direction
	 */
	public Direction bestKarboniteDirection(PlanetMap earth, MapLocation loc) {
		int mostK = 0;
		Direction bestDir = Direction.North;
		Direction[] directions = {Direction.North, Direction.Northeast, Direction.East, Direction.Southeast, Direction.South, Direction.Southwest, Direction.West, Direction.Northwest, Direction.Center};
		for (Direction dir : directions) {
			int newK = 0;
			MapLocation newLoc = loc.add(dir);
			if (!onEarth(earth, newLoc)) {
				newK = 0;
			}else {
				newK= (int)gc.karboniteAt(newLoc);
			}
			if (newK > mostK) {
				mostK = newK;
				bestDir = dir;
			}
		}
		return bestDir;
		
		
	}
	
	/**
	 * Determines if any factories are adjacent to the inputed location
	 * @param loc A MapLocation you want to test
	 * @return True or False
	 */
	public Boolean isAFactoryAdjacent(MapLocation loc) {
		if (gc.senseNearbyUnitsByType(loc, 2, UnitType.Factory).size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determines if placing a factory at the given location on the given planet would block a path
	 * @param earth The PlanetMap you want to check on
	 * @param loc The MapLocation you are checking
	 * @return True or False
	 */
	public Boolean isBlockingPath(PlanetMap earth, MapLocation loc) {

		Boolean isNorth = 1 == (int)earth.isPassableTerrainAt(loc.add(Direction.North));
		Boolean isNortheast = 1 == (int)earth.isPassableTerrainAt(loc.add(Direction.Northeast));
		Boolean isEast = 1 == (int)earth.isPassableTerrainAt(loc.add(Direction.East));
		Boolean isSoutheast = 1 == (int)earth.isPassableTerrainAt(loc.add(Direction.Southeast));
		Boolean isSouth = 1 == (int)earth.isPassableTerrainAt(loc.add(Direction.South));
		Boolean isSouthwest = 1 == (int)earth.isPassableTerrainAt(loc.add(Direction.Southwest));
		Boolean isWest = 1 == (int)earth.isPassableTerrainAt(loc.add(Direction.West));
		Boolean isNorthwest = 1 == (int)earth.isPassableTerrainAt(loc.add(Direction.Northwest));
		
		//I couldn't figure out how to actually do this. This is a bit strong of a check than needed.
		//This will return true when it is blocking a path, but also return true when its in a corner or a position where a path through didnt exist anyway
		//But maybe that is a good thing! Do we want factories with only 1-3 exit points?
		if (isWest && (isNortheast || isEast || isSoutheast)) {
			return true;
		}
		if (isNorthwest && (isSouthwest || isSouth || isSoutheast || isEast || isNortheast)) {
			return true;
		}
		if (isNorth && (isSouthwest || isSouth || isSoutheast)) {
			return true;
		}
		if (isNortheast && (isWest || isSouthwest || isSouth || isSoutheast)) {
			return true;
		}
		if (isEast && (isNorthwest || isWest || isSouthwest)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the Maplocation of the nearby unit with the least health
	 * @param nearby A VecUnit of nearby friendly units
	 * @return The location of the lowest hp unit that is nearby
	 */
	public int getLowestHealthId(VecUnit nearby) {
		int LowestHealth = 1000;
		int bestUnit = -1;
		for (int j = 0; j < nearby.size(); j++) {
			Unit nearbyUnit = nearby.get(j);
			if (nearbyUnit.health() < LowestHealth && nearbyUnit.health() < nearbyUnit.maxHealth() && !nearbyUnit.unitType().equals(UnitType.Factory) && !nearbyUnit.unitType().equals(UnitType.Rocket)) {
				LowestHealth = (int)nearbyUnit.health();
				bestUnit = nearbyUnit.id();
			}
		}
		return bestUnit;
	}
	
	public Direction smartDirection(PlanetMap planet, Unit unit) {
		ArrayList<Direction> directions = new ArrayList<Direction>(Arrays.asList(Direction.North, Direction.Northeast, Direction.East, Direction.Southeast, Direction.South, Direction.Southwest, Direction.West, Direction.Northwest));
		Direction tempDir = Direction.Center;
		boolean foundDir = false;
		while (!foundDir) {
			tempDir = arrayListRandom(directions);
			if (gc.canMove(unit.id(), tempDir) && onEarth(planet, unit.location().mapLocation())) {
				foundDir = true;
				return tempDir;
			}else {
				directions.remove(tempDir);
			}
		}
		return Direction.Center;
	}
}
