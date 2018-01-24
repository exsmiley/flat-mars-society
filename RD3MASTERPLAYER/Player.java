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
        
        // Whether or not to keep producing specific units
        boolean produceWorkers = true;
        boolean produceRockets = true;
        boolean produceFactories = true;
        boolean produceRangers = true;
        boolean produceHealers = true;
        boolean produceMages = true;
        boolean produceKnights = true;
        
        // Counter to produce units in mixed order.
        // int counter = 0;
        
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
        ArrayList<MapLocation> kLocs = new ArrayList<MapLocation>();
        if (gc.planet().equals(Planet.Earth)) {
            kLocs = utils.getKLocs(earth, kMap, startingUnits.get(0).location().mapLocation());
        }
        
        // make sure we can get a rocket
        System.out.println("Queuing rocket research: " + gc.queueResearch(UnitType.Rocket)); 
        System.out.println("Queuing ranger research: " + gc.queueResearch(UnitType.Ranger));
        System.out.println("Queuing ranger research: " + gc.queueResearch(UnitType.Ranger));
        System.out.println("Queuing ranger research: " + gc.queueResearch(UnitType.Ranger));
        //System.out.println("Queuing worker research: " + gc.queueResearch(UnitType.Worker));
        //System.out.println("Queuing worker research: " + gc.queueResearch(UnitType.Worker));
        //System.out.println("Queuing worker research: " + gc.queueResearch(UnitType.Worker));
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
            
            // Initialize number of each unit to 0 at beginning of each round.
            int numOfFactories = 0;
            int numOfRockets = 0;
            int numOfWorkers = 0;
            int numOfRangers = 0;
            int numOfHealers = 0;
            int numOfMages = 0;
            int numOfKnights = 0;
            
            if(!canRocket && gc.researchInfo().getLevel(UnitType.Rocket) >= 1) {
                    canRocket = true;
            }
            
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();

            for (int i = 0; i < units.size(); i++) {
                try {
                    Unit unit = units.get(i);
                    int id = unit.id();
                    
                    if (gc.planet().equals(Planet.Earth)) {
                    
                    
                        //WORKER LOGIC
                        if(unit.unitType().equals(UnitType.Worker) && unit.location().mapLocation().getPlanet().equals(Planet.Earth)) {
                            numOfWorkers++;
                            
                            // First, look for nearby blueprints to work on.
                            Location location = unit.location();
                            MapLocation maplocation = unit.location().mapLocation();
                            
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
                                        
                                        else if (gc.canLoad(other.id(), id) && other.unitType().equals(UnitType.Rocket)) {
                                            gc.load(other.id(), id);
                                            continue;
                                        }
                                        
                                        // Walk toward factory/Rocket to work on it.
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
                                if (gc.canReplicate(id, randomDirection) && produceWorkers && unit.abilityHeat() < 10 && !hasActed) {
                                    gc.replicate(id, randomDirection);
                                }
                                
                                // Blueprint a factory                           
                                else if (gc.canBlueprint(id, UnitType.Factory, randomDirection) && produceFactories && !hasActed) {
                                    gc.blueprint(id, UnitType.Factory, randomDirection);
                                }
                                
                                // If not a rocket
                                else if (canRocket) {
                                    if (gc.canBlueprint(id, UnitType.Rocket, randomDirection) && produceRockets && !hasActed) {
                                        gc.blueprint(id, UnitType.Rocket, randomDirection);
                                    }
                                }
                                
                                // Get Karbonite
                                else if (!hasActed) {
                                	//First try adjacent squares
                                	Direction bestDir = utils.bestKarboniteDirection(earth, maplocation);
                                	if (gc.karboniteAt(maplocation.add(bestDir)) > 0) {
                                		if (gc.canHarvest(id, bestDir)) {
                                			gc.harvest(id, bestDir);
                                		}
                                	}
                                	//Then go to the next closest location
                                	else if (gc.isMoveReady(id) && gc.planet().equals(Planet.Earth)) {
    									if (kLocs.size() > 0) {
                                			MapLocation destination = kLocs.get(0);
                                			if (gc.canSenseLocation(destination)) {
                                				int kAmt = (int)gc.karboniteAt(destination);
                                				if (kAmt == 0) {
                                					kLocs.remove(0);
                                				}else {
                                					pathing.moveTo(unit, destination);
                                				}
                                			}
                                		}
                                	}
                                    utils.harvestSomething(id); 
                                }
                                
                                // Move if you haven't already
                                if (!hasMoved && unit.movementHeat() < 10) {
                                    if (gc.canMove(id, randomDirection)) {
                                        gc.moveRobot(id, randomDirection); // TODO: Change path
                                    }
                                }
                                
                            }
                        }
                        
                        //ROCKET LOGIC
                        else if (unit.unitType().equals(UnitType.Rocket)  && unit.location().mapLocation().getPlanet().equals(Planet.Earth)) { 
                            numOfRockets++;
                            if(gc.canLaunchRocket(id, ml) && unit.structureGarrison().size() >= 6) {
                                gc.launchRocket(id, ml);
                                System.out.println("Launched a rocket to (" + ml.getX() + ", " + ml.getY() + ")");
                                ml = new MapLocation(Planet.Mars, Utils.randomNum((int) mars.getWidth()), Utils.randomNum((int) mars.getHeight()));
                                while(!mars.onMap(ml) || mars.isPassableTerrainAt(ml) == 0) {
                                    ml = new MapLocation(Planet.Mars, Utils.randomNum((int) mars.getWidth()), Utils.randomNum((int) mars.getHeight()));
                                }
                            }
                        }
                        
                        //FACTORY LOGIC
                        else if (unit.unitType().equals(UnitType.Factory)  && unit.location().mapLocation().getPlanet().equals(Planet.Earth)) {
                            numOfFactories++;
                            VecUnitID garrison = unit.structureGarrison();
                            if (garrison.size() > 0) {
                                Direction d = Utils.chooseRandom(ordinals);
                                if (gc.canUnload(unit.id(), d)) {
                                    System.out.println("Unloaded a unit!");
                                    gc.unload(unit.id(), d);
                                    continue;
                                }
                            }
                            
                            /*
                            else if (gc.canProduceRobot(unit.id(), UnitType.Healer) && produceHealers) {
                                	gc.produceRobot(unit.id(), UnitType.Healer);
                                	System.out.println("Produced a healer!");
                                	continue;
                            }
                            */
                            /*
                            else if (gc.canProduceRobot(unit.id(), UnitType.Knight) && produceKnights && counter % 3 == 0) {
                                gc.produceRobot(unit.id(), UnitType.Knight);
                                System.out.println("Produced an attacker!");
                                counter++;
                                continue;
                            }
                            */
                            
                            else if (gc.canProduceRobot(unit.id(), UnitType.Ranger) && produceRangers) {
                                gc.produceRobot(unit.id(), UnitType.Ranger);
                                System.out.println("Produced an attacker!");
                                //counter++;
                                continue;
                            }
                            
                            /*
                            else if (gc.canProduceRobot(unit.id(), UnitType.Mage) && produceMages && counter % 3 == 2) {
                                gc.produceRobot(unit.id(), UnitType.Mage);
                                System.out.println("Produced an attacker!");
                                counter++;
                                continue;
                            }
                            */
                            
                            
                        }
                        
                        //RANGER LOGIC
                        else if (unit.unitType().equals(UnitType.Ranger)  && unit.location().mapLocation().getPlanet().equals(Planet.Earth)) {
                            numOfRangers++;
                            boolean hasLoaded = false;
                            boolean hasMoved = false;
                            Location location = unit.location();  
                            if (location.isOnMap()) {  
                                VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(), 50);
                                for (int j = 0; j < nearby.size(); j++) {
                                    Unit other = nearby.get(j);
                                    if (hasLoaded) {
                                        break;
                                    }
                                    
                                    if (!other.team().equals(myTeam)) {
                                        
                                        if (gc.canAttack(id, other.id()) && unit.attackHeat() < 10) {
                                            gc.attack(id, other.id());
                                        }
                                        else if (unit.movementHeat() < 10) {
                                            pathing.moveTo(unit, other.location().mapLocation());
                                            hasMoved = true;
                                        }                                   
                                    }
                                    
                                    if (other.unitType().equals(UnitType.Rocket)) {
                                        if (location.mapLocation().distanceSquaredTo(other.location().mapLocation()) <= 1) {
                                            if (gc.canLoad(other.id(), id)) {
                                                gc.load(other.id(), id);
                                                continue;
                                            }
                                        }
                                        /*
                                        else {
                                            pathing.moveTo(unit, other.location().mapLocation());
                                            hasMoved = true;
                                        }
                                        */
                                    }
                                }
                                
                                if (!hasMoved && unit.movementHeat() < 10 && !hasLoaded) {
                                    pathing.moveTo(unit, enemyLoc);
                                }
                            }
                        }
                        
                        //MAGE LOGIC
                        else if (unit.unitType().equals(UnitType.Mage)  && unit.location().mapLocation().getPlanet().equals(Planet.Earth)) {
                            numOfMages++;
                            boolean hasMoved = false;
                            Location location = unit.location();  
                            if (location.isOnMap()) {  
                                VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(), unit.visionRange());
                                for (int j = 0; j < nearby.size(); j++) {
                                    Unit other = nearby.get(j);
                                    if (!other.team().equals(myTeam)) {
                                        
                                        if (gc.canAttack(id, other.id()) && unit.attackHeat() < 10) {
                                            gc.attack(id, other.id());
                                        }
                                        else if (unit.movementHeat() < 10) {
                                            pathing.moveTo(unit, other.location().mapLocation());
                                            hasMoved = true;
                                        }                                   
                                    }
                                }
                                
                                if (!hasMoved && unit.movementHeat() < 10) {
                                    pathing.moveTo(unit, enemyLoc);
                                }
                            }
                        }
                        
                        //KNIGHT LOGIC
                        else if (unit.unitType().equals(UnitType.Knight)  && unit.location().mapLocation().getPlanet().equals(Planet.Earth)) {
                            numOfRangers++;
                            boolean hasMoved = false;
                            Location location = unit.location();  
                            if (location.isOnMap()) {  
                                VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(), unit.visionRange());
                                for (int j = 0; j < nearby.size(); j++) {
                                    Unit other = nearby.get(j);
                                    if (!other.team().equals(myTeam)) {
                                        
                                        if (gc.canAttack(id, other.id()) && unit.attackHeat() < 10) {
                                            gc.attack(id, other.id());
                                        }
                                        else if (unit.movementHeat() < 10) {
                                            pathing.moveTo(unit, other.location().mapLocation());
                                            hasMoved = true;
                                        }                                   
                                    }
                                }
                                
                                if (!hasMoved && unit.movementHeat() < 10) {
                                    pathing.moveTo(unit, enemyLoc);
                                }
                            }
                        }
                        
                        
                        //HEALER LOGIC
                        else if (unit.unitType().equals(UnitType.Healer)  && unit.location().mapLocation().getPlanet().equals(Planet.Earth)) {
                        	numOfHealers++;
                        	boolean hasMoved = false;
                        	Location location = unit.location();
                        	if (location.isOnMap()) {
                        		//try healing
                        		if (unit.attackHeat() < 10) {
                        			VecUnit nearby = gc.senseNearbyUnitsByTeam(location.mapLocation(), 30, myTeam);
                        			if (nearby.size() > 0) {
    	                    			int bestId = utils.getLowestHealthId(nearby);
    	                    			if (!(bestId == -1)) {
    	                    				if (gc.canHeal(unit.id(), bestId)) {
    	                    					gc.heal(unit.id(), bestId);
    	                    				}
    	                    			}
                        			}else if (unit.movementHeat() < 10 && !hasMoved) {
                        				VecUnit largerNearby = gc.senseNearbyUnitsByTeam(location.mapLocation(), 50, myTeam);
                        				if (largerNearby.size() > 0) {
                        					int bestId = utils.getLowestHealthId(largerNearby);
                        					if (!(bestId == -1)) {
                        						pathing.moveTo(unit, gc.unit(bestId).location().mapLocation() );
                        						if (gc.canHeal(unit.id(), bestId) && unit.attackHeat() < 10) {
                        							gc.heal(unit.id(), bestId);
                        						}
                        						hasMoved = true;
                        					}
                        					
                        				}else {
                        					pathing.moveTo(unit, enemyLoc);
                        					hasMoved = true;
                        				}
                        			}
                        		}
                        		if (!hasMoved && unit.movementHeat() < 10) {
                					pathing.moveTo(unit, enemyLoc);
                					hasMoved = true;
                        		}
                        		
    
                        	    }
                        }
                    }
                    
                    // We on Mars.
                    else {
                    	
                        if (unit.location().mapLocation().getPlanet().equals(Planet.Mars)) {
                        	
                        	//WORKER LOGIC
                            if (unit.unitType().equals(UnitType.Worker)) {
                            	
                            	Direction randomDirection = Utils.chooseRandom(ordinals);
                            	
                                // Replicate yourself
                                if (gc.canReplicate(id, randomDirection) && produceWorkers && unit.abilityHeat() < 10) {
                                    gc.replicate(id, randomDirection);
                                }
                            	
                            	Direction bestDir = utils.bestKarboniteDirection(mars, unit.location().mapLocation());
                            	if (gc.karboniteAt(unit.location().mapLocation().add(bestDir)) > 0) {
                            		if (gc.canHarvest(id, bestDir)) {
                            			gc.harvest(id, bestDir);
                            		}
                            	}
                            	
                                
                                if (gc.canMove(id, randomDirection)) {
                                    gc.moveRobot(id, randomDirection); // TODO: Change path
                                }
                            }
                            
                            else if (unit.unitType().equals(UnitType.Rocket)) {
                                Direction randomDirection = Utils.chooseRandom(ordinals);
                                if (gc.canUnload(id, randomDirection)) {
                                    gc.unload(id, randomDirection);
                                }
                            }
                            
                            else if (unit.unitType().equals(UnitType.Ranger)) {
                                numOfRangers++;
                                boolean hasMoved = false;
                                Location location = unit.location();  
                                if (location.isOnMap()) {  
                                    VecUnit nearby = gc.senseNearbyUnits(location.mapLocation(), 50);
                                    for (int j = 0; j < nearby.size(); j++) {
                                        Unit other = nearby.get(j);
                                        
                                        if (!other.team().equals(myTeam)) {
                                            
                                            if (gc.canAttack(id, other.id()) && unit.attackHeat() < 10) {
                                                gc.attack(id, other.id());
                                            }
                                            else if (unit.movementHeat() < 10) {
                                                pathing.moveTo(unit, other.location().mapLocation());
                                                hasMoved = true;
                                            }                                   
                                        }
                                    }
                                    
                                    if (!hasMoved && unit.movementHeat() < 10) {
                                        Direction randomDirection = Utils.chooseRandom(ordinals);
                                        if (gc.canMove(id, randomDirection)) {
                                            gc.moveRobot(id, randomDirection); // TODO: Change path
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    } catch(Exception e) {
                        System.out.println(e);
                }
                
            }
            
            // Check whether or not to keep producing each unit.
            if (numOfRangers > 25) {
                produceRangers = false;
            }
            else {
                produceRangers = true;
            }
            
            if (numOfMages > 0) {
                produceMages = false;
            }
            else {
                produceMages = true;
            }
            
            if (numOfKnights > 0) {
                produceKnights = false;
            }
            else {
                produceKnights = true;
            }
            
            if (numOfRockets > 3) {
                produceRockets = false;
            }
            else {
                produceRockets = true;
            }
            
            if (numOfFactories > 5) {
                produceFactories = false;
            }
            else {
                produceFactories = true;
            }
            
            if (numOfWorkers > 5 && gc.round() < 750) {
                produceWorkers = false;
            }
            else {
                produceWorkers = true;
            }
            
            if (numOfHealers >= numOfRangers/8) {
            	    produceHealers = false;
            }
            else {
                produceHealers = true;
            }
            
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}