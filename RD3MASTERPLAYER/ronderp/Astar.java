package ronderp;
import bc.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/*
 * HOW TO USE:
 * Initialize a Pathing object in the form Astar x = new Astar(PlanetMap mars, PlanetMap earth, GameController gc); OUTSIDE OF THE LOOP THAT IS YOUR TURN.
 * This object stores all the paths for all you bots.
 * 
 * To move or create a path,  call x.moveTo(int unitID, MapLocation start, MapLocation end). IT IS THE SAME FUNCTION TO MOVE A BOT AND GENERATE A PATH. IF THE ROBOT HAS A PATH IT MOVES, IF NOT IT MAKES ONE
 * AND MOVES. If you want to make a new path just call moveTo(unitID, start, end) with a different end.
 */

public class Astar 
{
	public static GameController gc;
	public static PlanetMap earth, mars;
	public static HashMap<Integer, ArrayList<Direction>> paffs = new HashMap<Integer, ArrayList<Direction>>();
	public static HashMap<Integer, MapLocation> ends = new HashMap<Integer, MapLocation>();
	
	//input earth, mars, gameController
	public Astar(PlanetMap m, PlanetMap e, GameController g)
	{
		gc = g;
		mars = m;
		earth = e;
	}
	
	//calculates least possible cost to end for heuristic
	public static int costToEnd(MapLocation current, MapLocation end)
	{
		return (int) Math.sqrt(current.distanceSquaredTo(end));
	}
	
	//changes path of MapLocations to Dirs for easier moving
	public static ArrayList<Direction> toDirList(HashMap<Twople, Twople> p, MapLocation start, Twople end)
	{
		boolean atStartOfPath = false;
		ArrayList<Direction> path = new ArrayList<Direction>();
		Twople check = end;
		
		while (!atStartOfPath)
		{
			MapLocation oldloc = p.get(check).getMapLocation();
			atStartOfPath = oldloc.equals(start);
			Direction dir = oldloc.directionTo(check.getMapLocation());
			path.add(0, dir);
			check = new Twople(oldloc);
		}
		
		return path;
	}
	
	//finds path to end
	public static boolean findPath(int id, MapLocation start, MapLocation end)
	{
		//Finds which planet and returns false is wrong/bad start or end loc
		PlanetMap map;
		
		if (start.getPlanet()!=end.getPlanet()) return false;
		else if (start.getPlanet()==Planet.Earth) map = earth;
		else map = mars;
		
		if ((int) (map.isPassableTerrainAt(start))==0 || (int) (map.isPassableTerrainAt(end))==0) return false;
		
		//adds id to list of units with paths
		ends.put(id, end);
		
		//generates array of dirs w/o center
		Direction[] directions = new Direction[8];
		int count = 0;
		for (Direction d : Direction.values())
		{
			if (!d.equals(Direction.Center)) directions[count]=d;
			count++;
		}
		
		//makes PriorityQueue that ranks tiles by the sum cost to that tile and straight path to end 
		Comparator<Loc> comparator = new LocComparator();
		PriorityQueue<Loc> q = new PriorityQueue<Loc>(1, comparator);
		
		//HashMap that stores cost to spot
		HashMap<Twople, Integer> costs = new HashMap<Twople, Integer>();

		//HashMap that stores the tile that came before each tile in the best path
		HashMap<Twople, Twople> path = new HashMap<Twople, Twople>();
		
		//adds start to iterables
		Loc s = new Loc(start, 0);
		q.add(s);
		Twople startTwople =new Twople(start);
		costs.put(startTwople, 0);
		
		Twople e = new Twople(end);

		while(!(costs.containsKey(e)))
		{
			Loc curr = q.peek();
			Twople o = new Twople(curr.l);
			MapLocation current = curr.l; 
			
			for (Direction d : directions)
			{
				MapLocation next = current.add(d);
				Twople n = new Twople(next);
				int costTo = costs.get(o)+1;
				
				if (map.onMap(next))
				{
					if((int)map.isPassableTerrainAt(next)==1)
					{
						if (!(costs.containsKey(n)))
						{
							costs.put(n, costTo);
							path.put(n, o);
						}
						else if(costTo<costs.get(n))
						{
							costs.put(n, costTo);
							path.put(n, o);
						}
						
						Loc nextLoc = new Loc(next, costTo+costToEnd(next, end));
						q.add(nextLoc);
					}
				}
			}
			q.remove(curr);
		}
		
		ArrayList<Direction> dirPath;
		
		dirPath = toDirList(path, start, e);
		paffs.put(id, dirPath);
		
		return true;
	}
	
	//If has path and can move, moves. Else makes path and moves.
	public boolean moveTo(int id, MapLocation start, MapLocation end)
	{
		if(ends.containsKey(id))
		{
			ArrayList<Direction> paff = paffs.get(id);
			
			if (!(ends.get(id).equals(end)))
			{
				findPath(id, start, end);
				moveTo(id, start, end);
			}
			
			else if (!(paff.isEmpty()))
			{
				Direction dir = paff.get(0);
				
				if (gc.canMove(id, dir));
				{
					gc.moveRobot(id, dir);
					
					paff.remove(0);
					paffs.put(id, paff);
					
					return true;
				}
			}
		}
		
		else
		{
			findPath(id, start, end);
			moveTo(id, start, end);
		}
		
		return false;
		
	}

}
	

