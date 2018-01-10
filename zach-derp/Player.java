// import the API.
// See xxx for the javadocs.
import bc.*;

public class Player {
	
	public static void moveRobotSpiral(GameController gc, int id) {
		if(gc.canMove(id, Direction.North)) {
			gc.moveRobot(id, Direction.North);
		}
		else if(gc.canMove(id, Direction.West)) {
			gc.moveRobot(id, Direction.West);
		}
		else if(gc.canMove(id, Direction.South)) {
			gc.moveRobot(id, Direction.South);
		}
		else if(gc.canMove(id, Direction.East)) {
			gc.moveRobot(id, Direction.East);
		}
	}
	
	public static void replicateSomewhere(GameController gc, int id) {
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
	
    public static void main(String[] args) {
        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        // Connect to the manager, starting the game
        GameController gc = new GameController();

        // Direction is a normal java enum.
        Direction[] directions = Direction.values();

        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            
            System.out.println("I have " + units.size() + " units! :)");

            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                int id = unit.id();

                if(gc.isMoveReady(id)) {
                		moveRobotSpiral(gc, id);
                		replicateSomewhere(gc, id);
                }
                
            }
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}