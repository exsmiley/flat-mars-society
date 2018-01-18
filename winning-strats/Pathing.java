import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import MapLocationComparator; //HOW THE FUCK DO YOU IMPORT THIS SHIT???
import java.util.Comparator;
import bc.*;

public class Pathing 
{
	public static GameController gc = new GameController();
	public static PlanetMap earth, mars;
	public static Map<Unit, ArrayList<MapLocation>> paths = new HashMap<Unit, ArrayList<MapLocation>>();
	
	public Pathing(VecUnit units, PlanetMap mars, PlanetMap earth)
	{
		this.earth = earth;
		this.mars = mars;
		Map<Unit, ArrayList<MapLocation>> paths = new HashMap<Unit, ArrayList<MapLocation>>();
		this.paths = paths;
	}
	
	public static Direction[] getDirs()
	{
		Direction[] directions = new Direction[8];
		for (Direction d : Direction.values())
		{
			int count = 0;
			if (!d.equals(Direction.Center)) directions[count]=d;
			count++;
		}
		
		return directions;
	}
	
	public static Direction flipDirection(Direction dir)
	{
		if (dir.equals(Direction.North)) return Direction.South;
		else if (dir.equals(Direction.Northeast)) return Direction.Southwest;
		else if (dir.equals(Direction.East)) return Direction.West;
		else if (dir.equals(Direction.Southeast)) return Direction.Northwest;
		else if (dir.equals(Direction.South)) return Direction.North;
		else if (dir.equals(Direction.Southwest)) return Direction.Southeast;
		else if (dir.equals(Direction.West)) return Direction.East;
		else return Direction.Southeast;
	}
	
	public static void findPath(Unit unit, MapLocation end)
	{
		int id = unit.id();
		MapLocation start = unit.location().mapLocation();
		
		PlanetMap map;
		if (unit.location().isOnPlanet(Planet.Earth)) map = earth;
		else map = mars;
		
		//Generates Direction[] w/o center in order N, NE, E, SE, S, SW, W, NW
		Direction[] directions = new Direction[8];
		for (Direction d : Direction.values())
		{
			int count = 0;
			if (!d.equals(Direction.Center)) directions[count]=d;
			count++;
		}
		
		//Initializes comparator to be used for PriorityQueue
		Comparator<MapLocation> comparator = new MapLocationComparator(start, end);
		
		//Initializes PriorityQueue
		PriorityQueue<MapLocation> q = new PriorityQueue<MapLocation>(1, comparator);
		
		//Initializes Hashmap to store length of path to a given spaced
		Map<MapLocation, Integer> cost_to_spot = new HashMap<MapLocation, Integer>();
		
		//Initializes Hashmap to store best path to a given space
		Map<MapLocation, MapLocation> path = new HashMap<MapLocation, MapLocation>();
		
		//Add first space to above iterables
		cost_to_spot.put(start, 0);
		path.put(start, null);
		q.add(start);
		
		//Search spaces until it gets to end spot
		while (!q.contains(end))
		{
			MapLocation current = q.peek();
			
			//Checks all directions around tile in PriorityQueue
			for (Direction dir : directions)
			{
				MapLocation next = current.add(dir);
				
				boolean passable;
				if (map.onMap(next)) passable = false;
				else if ((int) map.isPassableTerrainAt(next) == 1) passable = true;
				else passable = false;
				
				//Check if viable tile
				if(passable)
				{
					//Checks if the path taken to this tile is the most efficient, if so, add to path so far
					int new_cost = (int) next.distanceSquaredTo(start);
					
					q.add(next);
					
					if (!cost_to_spot.containsKey(next) || cost_to_spot.get(next)>new_cost) 
					{
						cost_to_spot.put(next, new_cost);
						path.put(next, current);
					}
				}
			}
			
			//removes location from q so not searched twice
			q.remove(current);
		}
		
		//Constructs and returns ArrayList of MapLocs from start to end
		ArrayList<MapLocation> pathto = new ArrayList<MapLocation>();
		
		MapLocation nextspot = end;
		
		while (!(nextspot.equals(start)))
		{
			pathto.add(nextspot);
			nextspot = path.get(nextspot);
		}
		
		paths.put(unit, pathto);
	}
	
	public void moveTo(Unit unit, MapLocation end)
	{
		int id=unit.id();
		
		//If the bot doesn't have a saved path, generate one and move it
		if (!(paths.containsKey(unit)))
		{
			findPath(unit, end);
			moveTo(unit, end);
		}
		
		//Move the bot
		else
		{
			MapLocation loc = paths.get(unit).get(0);
			Direction dir = unit.location().mapLocation().directionTo(loc);
			if (gc.canMove(id, dir))
			{
				gc.moveRobot(id, dir);
			}
		}
	}
		
}  


