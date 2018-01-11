// import the API.
// See xxx for the javadocs.
import bc.*;
import math;

public class Player {
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
        
        Team my_team = gc.team();

        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                
                if unit.unitType == bc.UnitType.Factory {
                	if gc.canProduceRobot(unit.id(), bc.UnitType.Knight){
                		gc.produceRobot(unit.id(), bc.UnitType.Knight);
                		System.out.println("Made a Knight");
                	}
                }
                
                Location location = unit.location();
                if location.is_on_map(){
                	VecUnit nearby = gc.senseNearbyUnits(unit.map_location(),2)
                	for (int i = 0; i < nearby.size(); i++) {
                		Unit other = nearby.get(i);
                		if gc.canBuild(unit.id(), other.id()){
                			gc.build(unit.id(), other.id());
                			System.out.println("Built a thing");
                			continue;
                		}
                		if ((other.team() != my_team) && gc.isAttackReady(unit.id()) && gc.can_attack(unit.id(), other.id()){
                			gc.attack(unit.id(), other.id());
                			System.out.println("Attached a thing")
                			continue;
                		
                	}
                }
                
                //try and make a factory
                if (gc.karbonite() >= bc.unitType.Factory.blueprintCost() && gc.canBlueprint(unit.id(), bc.UnitType.Facory, Direction.North)) {
                	gc.blueprint(unit.id(), bc.unitType.Facorty, Direction.North);
                } else if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), Direction.South){
                	gc.moveRobot(unit.id(),Direction.South);
                }
                	
                
                	
                // Most methods on gc take unit IDs, instead of the unit objects themselves.
                //if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), Direction.Southeast)) {
                //    gc.moveRobot(unit.id(), Direction.Southeast);
                //}
                
            }
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}