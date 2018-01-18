// import the API.
// See xxx for the javadocs.
import bc.*;
import dariusUtil.Utils;

import java.util.*;

public class Player {
	
    public static MapLocation invertion(GameController gc, PlanetMap earthMap, MapLocation invertingLoc) {
    	int newx = (int)earthMap.getWidth()-invertingLoc.getX();
    	int newy = (int)earthMap.getHeight()-invertingLoc.getY();
    	return new MapLocation(gc.planet().Earth,newx,newy);
    }
	
    public static void main(String[] args) {
        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
//        MapLocation locMars = new MapLocation(Planet.Mars, 10, 20);
//        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
//        System.out.println("loc x: "+loc.getX());
//        

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
//        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        // Connect to the manager, starting the game
        GameController gc = new GameController();

        Utils utils = new Utils(gc);
        
        PlanetMap earth = gc.startingMap(Planet.Earth);
        PlanetMap mars = gc.startingMap(Planet.Mars);
        
        Team myTeam = gc.team();
        int turn = 0;
        
        UnitType[] attackers = new UnitType[] {UnitType.Knight, UnitType.Mage, UnitType.Ranger};
        
        // make sure we can get a rocket
        System.out.println("Queuing rocket research: " + gc.queueResearch(UnitType.Rocket));
        System.out.println("Queuing ranger research: " + gc.queueResearch(UnitType.Ranger));
        System.out.println("Queuing ranger research: " + gc.queueResearch(UnitType.Ranger));
        System.out.println("Queuing ranger research: " + gc.queueResearch(UnitType.Ranger));
        // TODO queue other research
        
        // rocket stuff maybe TODO
        utils.planRocketLaunches();
        System.out.println((int) mars.getWidth());
        MapLocation ml = new MapLocation(Planet.Mars, Utils.randomNum((int) mars.getWidth()), Utils.randomNum((int) mars.getHeight()));
        while(!mars.onMap(ml)) {
            ml = new MapLocation(Planet.Mars, Utils.randomNum((int) mars.getWidth()), Utils.randomNum((int) mars.getHeight()));
        }
        
        boolean hasSomethingQueued = true;
        boolean canRocket = false;
        boolean hasFactory = false;
        
        // Direction is a normal java enum.
        Direction[] ordinals = new Direction[]{Direction.North, Direction.South, Direction.West, Direction.East};
        VecUnit units = gc.myUnits();
        
        MapLocation enemyLoc = null;

        
        if (gc.planet().equals(gc.planet().Earth)) {
            MapLocation startLoc = units.get(0).location().mapLocation();        	
            enemyLoc = invertion(gc, earth, startLoc);
            System.out.println("MADE ENEMY LOC");
        }

        
        while (true) {
            turn += 1;
//            System.out.println("Current round: "+gc.round());
//            System.out.println("Rocket level: " + gc.researchInfo().getLevel(UnitType.Rocket));
//            System.out.println("Ranger level: " + gc.researchInfo().getLevel(UnitType.Ranger));
            if(!canRocket && gc.researchInfo().getLevel(UnitType.Rocket) >= 1) {
                    canRocket = true;
            }
            System.out.println(gc.rocketLandings().toJson());
            
            int numWorkers = 0;
            
            if(hasSomethingQueued && gc.researchInfo().roundsLeft() == 1) {
                    // TODO queue something new
                    hasSomethingQueued = false;
            }
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            units = gc.units();
//            System.out.println("I have " + units.size() + " units! :)");
            //Counter - counts things
            /*
            int realNumWorkers = 0;
            int realNumFactories = 0;
            int realNumRangers = 0;
            for (int i = 0; i < units.size(); i++) {
            	try {
            		Unit unit = units.get(i);
            		int id = unit.id();
            		
            		if (unit.unitType().equals(UnitType.Worker)) {
            			realNumWorkers++;
            		}
            		if (unit.unitType().equals(UnitType.Factory)) {
            			realNumFactories++;
            		}
            		if (unit.unitType().equals(UnitType.Ranger)) {
            			realNumRangers++;
            		}
            		
            	}catch(Exception e) {
            		System.out.println(e);
            	}
            	
            }
            */

            for (int i = 0; i < units.size(); i++) {
                    try {
                    Unit unit = units.get(i);
                    int id = unit.id();
                    UnitType toConstruct = UnitType.Ranger;//Utils.chooseRandom(attackers);
                    
                    if(unit.unitType().equals(UnitType.Worker)) {
                            numWorkers += 1;
                    }
                    
                    if (unit.unitType().equals(UnitType.Rocket)) {
//                          System.out.println("Trying to do something with a rocket..." + unit.health());
//                          unit.x
                            
                        
                            if(gc.canLaunchRocket(unit.id(), ml)) {
                                gc.launchRocket(unit.id(), ml);
                                System.out.println("Launched a rocket to (" + ml.getX() + ", " + ml.getY() + ")");
                                ml = new MapLocation(Planet.Mars, Utils.randomNum((int) mars.getWidth()), Utils.randomNum((int) mars.getHeight()));
                                while(!mars.onMap(ml)) {
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
                        else if (gc.canProduceRobot(unit.id(), toConstruct)) {
                            gc.produceRobot(unit.id(), toConstruct);
                            System.out.println("Produced an attacker!");
                            continue;
                        }
                    }
    
                    else {
                        boolean actedAlready = false;
                        // First, look for nearby blueprints to work on.
                        Location location = unit.location();
                        if (location.isOnMap()) {
                        
                            VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(),    unit.visionRange());
                            for (int j = 0; j < nearby.size(); j++) {
                                Unit other = nearby.get(j);
                                if(!unit.location().isOnMap()) {
                                        continue;
                                }
                                
                                if (unit.unitType().equals(UnitType.Worker) && (gc.canBuild(unit.id(), other.id()))) {
                                    gc.build(unit.id(), other.id());
                                    System.out.println("Built a factory!");
                                    // move onto the next unit;
                                    actedAlready = true;
                                    continue;
                                }
                                if (!(other.team().equals(myTeam)) && (gc.isAttackReady(unit.id())) && gc.canAttack(unit.id(), other.id())) {
                                    System.out.println("Attacked a thing.");
                                    gc.attack(unit.id(), other.id());
                                    actedAlready = true;
                                    continue;
                                }
                            }
                        }
                        
                        if(actedAlready) {
                                break;
                        }
                        
                            // move and replicate
                            if(gc.isMoveReady(id) && Math.random() < 0.85) {
                                try {
                                    	Direction dir = unit.location().mapLocation().directionTo(enemyLoc);
                                    	//System.out.println(dir);
                                    	//if (gc.canMove(id, dir) && unit.unitType().equals(UnitType.Ranger)) {
                                    	if(gc.canMove(id, dir)) {
                                        	gc.moveRobot(id, dir);
                                    	}else {
                                    		utils.moveRobotSpiral(id);
                                    	}
                                
                                		
                                	//	utils.moveRobotSpiral(id);	
                                	
                                    
                                    if(Math.random() < 0.2 && numWorkers < 10) {
                                        utils.replicateSomewhere(id);
                                    }
                                
                                } catch(Exception e) {
                                    System.out.println(e);
                                }
                            } else {
                            	
                                Direction d = Utils.chooseRandom(ordinals);
                                if(canRocket && gc.planet() == Planet.Earth) {
                                    if ((gc.karbonite() > 100) && (gc.canBlueprint(unit.id(), UnitType.Rocket, d))) {
                                    gc.blueprint(unit.id(), UnitType.Rocket, d);
                                    System.out.println("Blueprinted a rocket!");
                                }
                                } else {
                                    if (!hasFactory && (gc.karbonite() > 100) && (gc.canBlueprint(unit.id(), UnitType.Factory, d))) {
                                    gc.blueprint(unit.id(), UnitType.Factory, d);
                                    hasFactory = true;
                                }
                                    
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