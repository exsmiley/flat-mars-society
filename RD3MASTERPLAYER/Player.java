// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.*;

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
        
        // Direction is a normal java enum.
        Direction[] ordinals = new Direction[]{Direction.North, Direction.South, Direction.West, Direction.East};
        
        Map<String, Set<Integer>> allMyUnitIDs = new HashMap<>();
        allMyUnitIDs.put("Workers", new HashSet<Integer>());
        allMyUnitIDs.put("Rangers", new HashSet<Integer>());
        allMyUnitIDs.put("Mages", new HashSet<Integer>());
        allMyUnitIDs.put("Healers", new HashSet<Integer>());
        allMyUnitIDs.put("Knights", new HashSet<Integer>());
        allMyUnitIDs.put("Factories", new HashSet<Integer>());
        allMyUnitIDs.put("Rockets", new HashSet<Integer>());
        
        Map<Integer, Unit> allMyUnits = new HashMap<>();
        
        // A dumb way to keep track of factories after they've been blueprinted.
        int numberOfFactories = 0;

        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.      
            if (gc.round() == 1) {
                VecUnit units = gc.myUnits();
                for (int i = 0; i < units.size(); i++) {
                    Unit unit = units.get(i);
                    allMyUnitIDs.get("Workers").add(unit.id());
                    allMyUnits.put(unit.id(), unit);
                }
                gc.nextTurn();
                continue;
            }        
                
            // First factory logic
            for (int factoryID: allMyUnitIDs.get("Factories")) {
                Unit factory = allMyUnits.get(factoryID);
                VecUnitID garrison = factory.structureGarrison();
                
                if (garrison.size() > 0) {
                    Direction d = ordinals[(int)(Math.random()*ordinals.length)];
                    if (gc.canUnload(factory.id(), d)) {
                        System.out.println("Unloaded a Mage!");
                        gc.unload(factory.id(), d);
                        continue;
                    }
                }
                
                else if (gc.canProduceRobot(factory.id(), UnitType.Mage)) {
                    gc.produceRobot(factory.id(), UnitType.Mage);
                    System.out.println("Produced a Mage!");
                    continue;
                }

            }
            
            for (int workerID: allMyUnitIDs.get("Workers")) {
                // First, look for nearby blueprints to work on.
                Unit worker = allMyUnits.get(workerID);
                Location location = worker.location();
                if (location.isOnMap()) {
                    VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(), 2);
                    for (int j = 0; j < nearby.size(); j++) {
                        Unit other = nearby.get(j);
                        if (gc.canBuild(workerID, other.id())) {
                            gc.build(workerID, other.id());
                            System.out.println("Built a factory!");    
                            allMyUnitIDs.get("Factories").add(other.id());
                            allMyUnits.put(other.id(), other);
                            // move onto the next unit;
                            continue;
                        }
                    }
                    Direction d = ordinals[(int)(Math.random()*ordinals.length)];
                    if ((gc.karbonite() > 100) && (gc.canBlueprint(workerID, UnitType.Factory, d)) && numberOfFactories < 9) {
                        gc.blueprint(workerID, UnitType.Factory, d);
                        numberOfFactories++;
                    }
                }
            }
                
            /*
            if (location.isOnMap()) {
                VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(), 2);
                for (int j = 0; j < nearby.size(); j++) {
                    Unit other = nearby.get(j);
                    if (unit.unitType().equals(UnitType.Worker) && (gc.canBuild(unit.id(), other.id()))) {
                        gc.build(unit.id(), other.id());
                        System.out.println("Built a factory!");
                        allMyUnitIDs.get("Factories").add(unit.id());
                        allMyUnits.put(unit.id(), unit);
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
            Direction d = ordinals[(int)(Math.random()*ordinals.length)];
            
            // or, try to build a factory:
            if ((gc.karbonite() > 100) && (gc.canBlueprint(unit.id(), UnitType.Factory, d)) && Math.random() < 0.1) {
                gc.blueprint(unit.id(), UnitType.Factory, d);
            }
            
            // If that fails try to move.
            else if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), d)) {
                gc.moveRobot(unit.id(), d);
            }
            */            
            
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}