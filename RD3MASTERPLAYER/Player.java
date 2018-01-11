// import the API.
// See xxx for the javadocs.
import bc.*;

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
        
        Team myTeam = gc.team();
        boolean builtOneFactory = false;

        while (true) {
            try {
                System.out.println("Current round: "+gc.round());
                // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
                VecUnit units = gc.myUnits();
                for (int i = 0; i < units.size(); i++) {
                    Unit unit = units.get(i);
                    
                    // First factory logic
                    if (unit.unitType().equals(UnitType.Factory)) {
                        VecUnitID garrison = unit.structureGarrison();
                        if (garrison.size() > 0) {
                            Direction d = directions[(int)(Math.random()*directions.length)];
                            if (gc.canUnload(unit.id(), d)) {
                                System.out.println("Unloaded a Mage!");
                                gc.unload(unit.id(), d);
                                continue;
                            }
                        }
                        else if (gc.canProduceRobot(unit.id(), UnitType.Mage)) {
                            gc.produceRobot(unit.id(), UnitType.Mage);
                            System.out.println("Produced a Mage!");
                            continue;
                        }

                    }
                    
                    // First, look for nearby blueprints to work on.
                    Location location = unit.location();
                    if (location.isOnMap()) {
                        VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(), 2);
                        for (int j = 0; j < nearby.size(); j++) {
                            Unit other = nearby.get(j);
                            if (unit.unitType().equals(UnitType.Worker) && (gc.canBuild(unit.id(), other.id()))) {
                                gc.build(unit.id(), other.id());
                                System.out.println("Built a factory!");
                                builtOneFactory = true;
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
                    
                    // Ok there weren't any dudes around, pick a direction at random
                    Direction d = directions[(int)(Math.random()*directions.length)];
                    
                    // or, try to build a factory:
                    if ((gc.karbonite() > 100) && (gc.canBlueprint(unit.id(), UnitType.Factory, d)) && (!builtOneFactory)) {
                        gc.blueprint(unit.id(), UnitType.Factory, d);
                    }
                    
                    // If that fails try to move.
                    else if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), d)) {
                        gc.moveRobot(unit.id(), d);
                    }
                    
                }
            } catch (Exception e) {
                System.out.println("ERRRORRORORROOROR");
            }
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}