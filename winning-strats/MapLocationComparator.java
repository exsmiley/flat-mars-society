import java.util.Comparator;
import bc.MapLocation;
import bc.*;

public class MapLocationComparator implements Comparator<MapLocation>
{	
	public MapLocation start, end;
	
	public MapLocationComparator(MapLocation start, MapLocation end)
	{
		this.start = start;
		this.end = end;
	}
	
	@Override
	//ranks MapLocs in PriorityQueue such that those with the lowest (distance_from_start + expected_distance_to_end) are checked first
	public int compare(MapLocation checkloc, MapLocation oldloc)
	{
		int checkWeight = (int) checkloc.distanceSquaredTo(start) + (int) checkloc.distanceSquaredTo(end);
		int oldWeight = (int) oldloc.distanceSquaredTo(start) + (int) oldloc.distanceSquaredTo(end);
		
		if (checkWeight>oldWeight) return -1;
		else return 1;
	}
}
