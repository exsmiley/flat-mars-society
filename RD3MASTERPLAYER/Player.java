// import the API.
// See xxx for the javadocs.
import bc.*;
import ronderp.Pathing;
import ronderp.Utils;
import ronderp.Mmap;
import ronderp.SimplePathing;

import java.util.*;

public class Player {
    public static void main(String[] args) {   
        // Connect to the manager, starting the game
        GameController gc = new GameController();

        Utils utils = new Utils(gc);
        
        PlanetMap earth = gc.startingMap(Planet.Earth);
        PlanetMap mars = gc.startingMap(Planet.Mars);
        
        Team myTeam = gc.team();
        int numOfFactories = 0;
        int numOfRockets = 0;
        int numOfWorkers = 0;
        int numOfRangers = 0;
        
        // Guess enemy spawn
        VecUnit startingUnits = gc.myUnits();
        MapLocation enemyLoc = new MapLocation(Planet.Earth, 1, 1);
        if (gc.planet().equals(Planet.Earth)) {
            MapLocation startLoc = startingUnits.get(0).location().mapLocation();           
            enemyLoc = utils.invertion(earth, startLoc);
        }
        
        //Fun with maps!
        Mmap[] output = utils.makeKMapandPassableMap(earth);
        Mmap kMap = output[0]; //map wiht karbonite values
        Mmap passableMap = output[1]; //map with 1 on passable terrain, 0 on impassible terrain
        
        //List of locations that have karbonite from closets to farthest.
        ArrayList<MapLocation> kLocs = utils.getKLocs(earth, kMap, startingUnits.get(0).location().mapLocation());
        
        
        
        // make sure we can get a rocket
        System.out.println("Queuing rocket research: " + gc.queueResearch(UnitType.Rocket));
        System.out.println("Queuing ranger research: " + gc.queueResearch(UnitType.Ranger));
        System.out.println("Queuing ranger research: " + gc.queueResearch(UnitType.Ranger));
        System.out.println("Queuing ranger research: " + gc.queueResearch(UnitType.Ranger));
        System.out.println("Queuing worker research: " + gc.queueResearch(UnitType.Worker));
        System.out.println("Queuing worker research: " + gc.queueResearch(UnitType.Worker));
        System.out.println("Queuing worker research: " + gc.queueResearch(UnitType.Worker));
        // TODO queue other research
        
        utils.planRocketLaunches();
        MapLocation ml = new MapLocation(Planet.Mars, Utils.randomNum((int) mars.getWidth()), Utils.randomNum((int) mars.getHeight()));
        while((!mars.onMap(ml)) || mars.isPassableTerrainAt(ml) == 0) {
            ml = new MapLocation(Planet.Mars, Utils.randomNum((int) mars.getWidth()), Utils.randomNum((int) mars.getHeight()));
        }
        
        boolean canRocket = false;
        
        // Direction is a normal java enum.
        Direction[] ordinals = new Direction[]{Direction.North, Direction.South, Direction.West, Direction.East};
        
        // Initialize pathing object to help bois move places
        SimplePathing pathing = new SimplePathing(mars, earth, gc);
        
        while (true) {
            System.out.println("Current round: "+gc.round());
            if(!canRocket && gc.researchInfo().getLevel(UnitType.Rocket) >= 1) {
                    canRocket = true;
            }
            
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();

            for (int i = 0; i < units.size(); i++) {
                try {
                    Unit unit = units.get(i);
                    int id = unit.id();
                    UnitType toConstruct = UnitType.Ranger;
                    
                    if(unit.unitType().equals(UnitType.Worker)) {
                        // First, look for nearby blueprints to work on.
                        Location location = unit.location();
                        
                        if (location.isOnMap()) {     
                            VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(), unit.visionRange());
                            boolean hasMoved = false;
                            boolean hasActed = false;
                            for (int j = 0; j < nearby.size(); j++) {
                                Unit other = nearby.get(j);
                                
                                // We don't want to build the enemy factories for them, lol.
                                if ((other.unitType().equals(UnitType.Factory) || other.unitType().equals(UnitType.Rocket)) && other.team().equals(myTeam) && !hasActed) {
                                    if (gc.canBuild(id, other.id())) {
                                        gc.build(unit.id(), other.id());
                                        // move onto the next unit;
                                        hasActed = true;
                                        continue;
                                    }
                                    // Walk toward factory to work on it.
                                    else {
                                        MapLocation factoryRocketLocation = other.location().mapLocation();
                                        if (location.mapLocation().distanceSquaredTo(factoryRocketLocation) > 1) {
                                            Direction directionToWalk = location.mapLocation().directionTo(factoryRocketLocation);
                                            if (gc.canMove(id, directionToWalk) && unit.movementHeat() < 10 && !hasMoved) {
                                                gc.moveRobot(id, directionToWalk); // TODO: Change path
                                                hasMoved = true;
                                            }
                                        }
                                        
                                        // Otherwise, just chill next to the factory for now.
                                    }
                                }
                                
                                // Run away from the enemy.
                                else if (!other.team().equals(myTeam)) {
                                    MapLocation enemyLocation = other.location().mapLocation();
                                    Direction directionToWalkAwayFrom = location.mapLocation().directionTo(enemyLocation);
                                    Direction newDirection = Utils.getOppositeDirection(directionToWalkAwayFrom);
                                    if (gc.canMove(id, newDirection) && !hasMoved && unit.movementHeat() < 10) {
                                        gc.moveRobot(id, newDirection); // TODO: Change path
                                        hasMoved = true;
                                    }
                                }
                            }       
                            
                            Direction randomDirection = Utils.chooseRandom(ordinals);
                            // Replicate yourself
                            if (gc.canReplicate(id, randomDirection) && numOfWorkers < 3 && unit.abilityHeat() < 10 && !hasActed) {
                                gc.replicate(id, randomDirection);
                                numOfWorkers++;
                            }
                            
                            // Blueprint a factory                           
                            else if (gc.canBlueprint(id, UnitType.Factory, randomDirection) && numOfFactories < 3 && !hasActed) {
                                gc.blueprint(id, UnitType.Factory, randomDirection);
                                numOfFactories++;
                            }
                            
                            // If not a rocket
                            else if (canRocket) {
                                if (gc.canBlueprint(id, UnitType.Rocket, randomDirection) && numOfRockets < 3 && !hasActed) {
                                    gc.blueprint(id, UnitType.Rocket, randomDirection);
                                    numOfRockets++;
                                }
                            }
                            
                            else if (!hasActed) {
                                utils.harvestSomething(id); // Attempts to harvest karbonite from adjacent squares.
                            }
                            
                            // Move if you haven't already
                            if (!hasMoved && unit.movementHeat() < 10) {
                                if (gc.canMove(id, randomDirection)) {
                                    gc.moveRobot(id, randomDirection); // TODO: Change path
                                }
                            }
                            
                        }
                    }
                    
                    else if (unit.unitType().equals(UnitType.Rocket)) { 
                        if(gc.canLaunchRocket(id, ml)) {
                            gc.launchRocket(id, ml);
                            System.out.println("Launched a rocket to (" + ml.getX() + ", " + ml.getY() + ")");
                            ml = new MapLocation(Planet.Mars, Utils.randomNum((int) mars.getWidth()), Utils.randomNum((int) mars.getHeight()));
                            while(!mars.onMap(ml) || mars.isPassableTerrainAt(ml) == 0) {
                                ml = new MapLocation(Planet.Mars, Utils.randomNum((int) mars.getWidth()), Utils.randomNum((int) mars.getHeight()));
                            }
                        }
                    }
                    
                    else if (unit.unitType().equals(UnitType.Factory)) {
                        VecUnitID garrison = unit.structureGarrison();
                        if (garrison.size() > 0) {
                            Direction d = Utils.chooseRandom(ordinals);
                            if (gc.canUnload(unit.id(), d)) {
                                System.out.println("Unloaded a unit!");
                                gc.unload(unit.id(), d);
                                continue;
                            }
                        }
                        else if (gc.canProduceRobot(unit.id(), toConstruct) && numOfRangers < 2) {
                            gc.produceRobot(unit.id(), toConstruct);
                            System.out.println("Produced an attacker!");
                            numOfRangers++;
                            continue;
                        }
                    }
                    
                    else if (unit.unitType().equals(UnitType.Ranger)) {
                        boolean hasMoved = false;
                        Location location = unit.location();                      
                        if (location.isOnMap()) {     
                            VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(), 50);
                            for (int j = 0; j < nearby.size(); j++) {
                                Unit other = nearby.get(j);
                                if (!other.team().equals(myTeam)) {
                                    /*
                                    Direction enemyDirection = location.mapLocation().directionTo(other.location().mapLocation());
                                    if (gc.canAttack(id, other.id()) && unit.attackHeat() < 10) {
                                        gc.attack(id, other.id());
                                    }
                                    else if (gc.canMove(id, enemyDirection) && !hasMoved && unit.movementHeat() < 10) {
                                        gc.moveRobot(id, enemyDirection);
                                        hasMoved = true;
                                    }
                                    */
                                    //pathing.findPath(unit, other.location().mapLocation());
                                    //hasMoved = true;
                                }
                            }
                            
                            if (!hasMoved && unit.movementHeat() < 10) {
                                /*
                                Direction directionTowardEnemy = location.mapLocation().directionTo(enemyLoc);
                                Direction randomDirection = Utils.chooseRandom(ordinals);
                                if (gc.canMove(id, directionTowardEnemy)) {
                                    gc.moveRobot(id, directionTowardEnemy); // TODO: Change path
                                    hasMoved = true;
                                }                              
                                else if (gc.canMove(id, randomDirection)) {
                                    gc.moveRobot(id, randomDirection);
                                }
                                */
                                pathing.findPath(unit, new MapLocation(Planet.Earth, 4 ,4));
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