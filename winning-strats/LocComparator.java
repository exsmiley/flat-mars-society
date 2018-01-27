import java.util.Comparator;
import bc.MapLocation;
import bc.*;

public class LocComparator implements Comparator<Loc>
{	
	@Override
	//ranks MapLocs in PriorityQueue such that those with the lowest (distance_from_start + expected_distance_to_end) are checked first
	public int compare(Loc checkloc, Loc oldloc)
	{
		if (checkloc.w>=oldloc.w) return -1;
		else if (checkloc.w<oldloc.w) return 1;
		else 
		{
			System.out.println("FUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCK");
			return 69;
		}
	}
}
