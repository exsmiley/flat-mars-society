
// import the API.
// See xxx for the javadocs.
import bc.*;
import zachderp.*;

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
        Utils utils = new Utils(gc);
        
        Team myTeam = gc.team();
        
        UnitType[] attackers = new UnitType[] {UnitType.Knight, UnitType.Mage, UnitType.Ranger};
        
        // make sure we can get a rocket
        gc.queueResearch(UnitType.Rocket);
        // TODO queue other research
        
        utils.planRocketLaunches();

        // Direction is a normal java enum.
        Direction[] ordinals = new Direction[]{Direction.North, Direction.South, Direction.West, Direction.East};
        
        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            
            System.out.println("I have " + units.size() + " units! :)");

            for (int i = 0; i < units.size(); i++) {
            		try {
	                Unit unit = units.get(i);
	                int id = unit.id();
	                UnitType toConstruct = utils.chooseRandom(attackers);
	                
	                if (unit.unitType().equals(UnitType.Factory)) {
	                    VecUnitID garrison = unit.structureGarrison();
	                    if (garrison.size() > 0) {
	                        Direction d = utils.chooseRandom(ordinals);
	                        if (gc.canUnload(unit.id(), d)) {
	                            System.out.println("Unloaded a unit!");
	                            gc.unload(unit.id(), d);
	                            continue;
	                        }
	                    }
	                    else if (gc.canProduceRobot(unit.id(), toConstruct)) {
	                        gc.produceRobot(unit.id(), toConstruct);
	                        System.out.println("Produced an attacker!");
	                        continue;
	                    }
	                }
	
	                else if(gc.isMoveReady(id)) {
	                	// First, look for nearby blueprints to work on.
	                    Location location = unit.location();
	                    if (location.isOnMap()) {
	                    
	                        VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(), 	unit.attackRange());
	                        for (int j = 0; j < nearby.size(); j++) {
	                            Unit other = nearby.get(j);
	                            if (unit.unitType().equals(UnitType.Worker) && (gc.canBuild(unit.id(), other.id()))) {
	                                gc.build(unit.id(), other.id());
	                                System.out.println("Built a factory!");
	                                // move onto the next unit;
	                                continue;
	                            }
	                            if (!(other.team().equals(myTeam)) && (gc.isAttackReady(unit.id())) && gc.canAttack(unit.id(), other.id())) {
	                                System.out.println("Attacked a thing.");
	                                gc.attack(unit.id(), other.id());
	                                continue;
	                            }
	                        }
	                    }
	                		// move and replicate
	                		if(Math.random() < 0.85) {
		                		try {
		                			utils.moveRobotSpiral(id);
		                			if(Math.random() < 0.2) {
		                				utils.replicateSomewhere(id);
		                			}
		                       	
		                		} catch(Exception e) {
		                			System.out.println(e);
		                		}
	                		} else {
	                			Direction d = utils.chooseRandom(ordinals);
	                			if ((gc.karbonite() > 100) && (gc.canBlueprint(unit.id(), UnitType.Factory, d))) {
	                            gc.blueprint(unit.id(), UnitType.Factory, d);
	                        }
	                		}
	                
	
	                }
            		} catch(Exception e) {
                		System.out.println(e);
                }
                
            }
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}