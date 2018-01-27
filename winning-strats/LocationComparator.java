import java.util.Comparator;
import bc.MapLocation;
import bc.*;

public class LocationComparator implements Comparator<MapLocation>
{	
	public MapLocation start, end;
	
	public LocationComparator(MapLocation start, MapLocation end)
	{
		this.start = start;
		this.end = end;
	}
	
	@Override
	public int compare(MapLocation checkloc, MapLocation oldloc)
	{
		int checkWeight = (int) checkloc.distanceSquaredTo(start) + (int) checkloc.distanceSquaredTo(end);
		int oldWeight = (int) oldloc.distanceSquaredTo(start) + (int) oldloc.distanceSquaredTo(end);
		
		return checkWeight - oldWeight;
	}
}
