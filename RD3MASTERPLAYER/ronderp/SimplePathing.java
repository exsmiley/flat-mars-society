package ronderp;

import java.util.*;
import bc.*;

public class SimplePathing {
    public GameController gc;
    public PlanetMap earth, mars;
    public Map<Integer, List<MapLocation>> paths = new HashMap<Integer, List<MapLocation>>();
    public Utils utils;
    public Direction[] ordinals = new Direction[]{Direction.North, Direction.South, Direction.West, Direction.East};
    
    public SimplePathing(PlanetMap mars, PlanetMap earth, GameController gc)
    {
        this.gc = gc;
        this.earth = earth;
        this.mars = mars;
        this.utils = new Utils(gc);
    }
    
    
    public boolean findPath(Unit unit, MapLocation end) {
        MapLocation start = unit.location().mapLocation();
        if (!start.getPlanet().equals(end.getPlanet())) {
            return false;
        }
        else if (start.getPlanet().equals(Planet.Earth)) {
            if (!earth.onMap(start) || !earth.onMap(end) || earth.isPassableTerrainAt(start) == 0 || earth.isPassableTerrainAt(end) == 0) {
                return false;
            }
        }
        else if (start.getPlanet().equals(Planet.Mars)) {
            if (!mars.onMap(start) || !mars.onMap(end) || mars.isPassableTerrainAt(start) == 0 || mars.isPassableTerrainAt(end) == 0) {
                return false;
            }
        }
        else if (start.equals(end)) {
            return false;
        }
        
        Direction[] directions = new Direction[8];
        int count = 0;
        for (Direction d: Direction.values()) {
            if (!d.equals(Direction.Center)) {
                directions[count] = d;
                count++;
            }
        }
        
        Map<Twople, Twople> visited = new HashMap<>();
        visited.put(new Twople(start), null);
        List<MapLocation> queue = new ArrayList<>(Arrays.asList(start));
        boolean done = false;
        while(queue.size() != 0) {
            MapLocation node = queue.get(0);
            queue = queue.subList(1, queue.size());
            for (Direction d: directions) {
                MapLocation next = node.add(d);
                if (next.equals(end)) {
                    visited.put(new Twople(next), new Twople(node));
                    done = true;
                    break;
                }

                if (!visited.containsKey(new Twople(next)) && earth.onMap(next) && earth.isPassableTerrainAt(next) == 1) { 
                    visited.put(new Twople(next), new Twople(node));
                    queue.add(next);
                }
            }
            if (done) {
                break;
            }          
        }
        
        if (visited.containsKey(new Twople(end))) {
            List<MapLocation> finalPath = reconstructPath(start, end, visited);
            paths.put(unit.id(), finalPath);
            return true;
        }       
        return false;
    }
    
    public List<MapLocation> reconstructPath(MapLocation start, MapLocation end, Map<Twople, Twople> visited) {
        List<Twople> finalPath = new ArrayList<>(Arrays.asList(new Twople(end)));
        Twople next = visited.get(new Twople(end));
        while (!next.equals(new Twople(start))) {
            finalPath.add(next);
            next = visited.get(next);
        }
        //finalPath.add(new Twople(start));
        Collections.reverse(finalPath);
        List<MapLocation> newestFinalPath = new ArrayList<>();
        for (Twople t: finalPath) {
            newestFinalPath.add(new MapLocation(t.getPlanet(), t.getX(), t.getY()));
        }
        return newestFinalPath;
    }
    
    public void moveTo(Unit unit, MapLocation end) {
        int id = unit.id();   
        
        if (unit.location().mapLocation().equals(end)) {
            // Do nothing, you have arrived!
        }
        
        // If path doesn't already exist, find a new path, use it.        
        else if (!paths.containsKey(id)) {
            boolean foundPath = findPath(unit, end);
            if (foundPath) {
                moveTo(unit, end);
            }
            else {
                Direction randomDirection = Utils.chooseRandom(ordinals);
                if (gc.canMove(id, randomDirection)) {
                    gc.moveRobot(id, randomDirection);
                }
            }
        }
        
        else if(!(paths.get(id).get(paths.get(id).size()-1).equals(end))) {
            boolean foundPath = findPath(unit, end);
            if (foundPath) {
                moveTo(unit, end);
            }
            else {
                Direction randomDirection = Utils.chooseRandom(ordinals);
                if (gc.canMove(id, randomDirection)) {
                    gc.moveRobot(id, randomDirection);
                }
            }
        }
        
        else {
            List<MapLocation> locations = paths.get(id);
            MapLocation loc = paths.get(id).get(0);
            locations = locations.subList(1, locations.size());
            Direction dir = unit.location().mapLocation().directionTo(loc);
            if (gc.canMove(id, dir) && unit.movementHeat() < 10) {
                gc.moveRobot(id, dir);
                paths.put(id, locations);              
            }
            
            else if (unit.movementHeat() < 10) {
                utils.moveRobotSpiral(id);
                findPath(unit, end);
            }
            else {
                findPath(unit, end);
            }
        }
    }
}
