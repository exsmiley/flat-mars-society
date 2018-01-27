import bc.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Astar 
{
	public static GameController gc;
	public static PlanetMap earth, mars;
	public static HashMap<Integer, ArrayList<Direction>> paffs = new HashMap<Integer, ArrayList<Direction>>();
	public static HashMap<Integer, MapLocation> ends = new HashMap<Integer, MapLocation>();
	
	public Astar(PlanetMap mars, PlanetMap earth, GameController gc)
	{
		this.gc = gc;
		this.mars = mars;
		this.earth = earth;
	}
	
	public static int costToEnd(MapLocation current, MapLocation end)
	{
		return (int) Math.sqrt(current.distanceSquaredTo(end));
	}
	
//	public Direction reverseDir(Direction dir)
//	{
//		if (dir.equals(Direction.North)) return Direction.South;
//		else if (dir.equals(Direction.Northeast)) return Direction.Southwest;
//		else if (dir.equals(Direction.East)) return Direction.West;
//		else if (dir.equals(Direction.Southeast)) return Direction.Northwest;
//		else if (dir.equals(Direction.South)) return Direction.North;
//		else if (dir.equals(Direction.Southwest)) return Direction.Northeast;
//		else if (dir.equals(Direction.West)) return Direction.East;
//		else if (dir.equals(Direction.Northwest)) return Direction.Southeast;
//		else return Direction.Center;
//	}
	
	public static ArrayList<Direction> toDirList(HashMap<Twople, Twople> costs, MapLocation start, Twople end)
	{
		boolean atStartOfPath = false;
		ArrayList<Direction> path = new ArrayList<Direction>();
		Twople check = end;
		
		while (!atStartOfPath)
		{
			MapLocation oldloc = costs.get(check).getMapLocation();
			if (oldloc.equals(start)) atStartOfPath = true;
			Direction dir = oldloc.directionTo(check.getMapLocation());
			path.add(0, dir);
		}
		
		return path;
	}
	
	public static boolean findPath(PlanetMap marsMap, PlanetMap earthMap, int id, MapLocation start, MapLocation end)
	{
		PlanetMap map;
		
		if (start.getPlanet()!=end.getPlanet()) return false;
		else if (start.getPlanet()==Planet.Earth) map = earthMap;
		else map = marsMap;
		
		if ((int) (map.isPassableTerrainAt(start))==0 || (int)(map.isPassableTerrainAt(end))==0) return false;
		
		ends.put(id, end);
		
		Direction[] directions = new Direction[8];
		int count = 0;
		for (Direction d : Direction.values())
		{
			if (!d.equals(Direction.Center)) directions[count]=d;
			count++;
		}
		
		Comparator<Loc> comparator = new LocComparator();
		PriorityQueue<Loc> q = new PriorityQueue<Loc>(1, comparator);
		
		HashMap<Twople, Integer> costs = new HashMap<Twople, Integer>();
		HashMap<Twople, Twople> path = new HashMap<Twople, Twople>();
		
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
	
	
//	public static boolean randomWalk(int id)
//	{
//		for (Direction d : direx)
//		{
//			if (gc.canMove(id, d))
//			{
//				gc.moveRobot(id, d);
//				return true;
//			}
//		}
//		
//		return false;
//	}

	
	public boolean moveTo(PlanetMap marsMap, PlanetMap earthMap, int id, MapLocation start, MapLocation end)
	{
		if(ends.containsKey(id))
		{
			ArrayList<Direction> paff = paffs.get(id);
			
			if (!(ends.get(id).equals(end)))
			{
				findPath(marsMap, earthMap, id, start, end);
				moveTo(marsMap, earthMap, id, start, end);
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
			findPath(marsMap, earthMap, id, start, end);
			moveTo(marsMap, earthMap, id, start, end);
		}
		
		return false;
		
	}

}
	

