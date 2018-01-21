package ronderp;

import java.util.*;
import bc.*;

public class SimplePathing {
    public GameController gc;
    public PlanetMap earth, mars;
    public Map<Unit, List<MapLocation>> paths = new HashMap<Unit, List<MapLocation>>();
    
    public SimplePathing(PlanetMap mars, PlanetMap earth, GameController gc)
    {
        this.gc = gc;
        this.earth = earth;
        this.mars = mars;
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
        
        Map<MapLocation, MapLocation> visited = new HashMap<>();
        visited.put(start, null);
        List<MapLocation> queue = new ArrayList<>(Arrays.asList(start));
        boolean done = false;
        while(queue.size() != 0) {
            MapLocation node = queue.get(0);
            queue = queue.subList(1, queue.size());
            for (Direction d: directions) {
                MapLocation next = node.add(d);
                if (next.equals(end)) {
                    visited.put(next, node);
                    done = true;
                    break;
                }

                if (!visited.containsKey(next) && earth.onMap(next) && earth.isPassableTerrainAt(next) == 1) { 
                    System.out.println(next);
                    System.out.println(visited.keySet());
                    System.out.println(visited.containsKey(next));
                    visited.put(next, node);
                    queue.add(next);
                }
            }
            if (done) {
                break;
            }          
        }
        
        if (visited.containsKey(end)) {
            List<MapLocation> finalPath = reconstructPath(start, end, visited);
            System.out.println(finalPath);
            paths.put(unit, finalPath);
            return true;
        }       
        return false;
    }
    
    public List<MapLocation> reconstructPath(MapLocation start, MapLocation end, Map<MapLocation, MapLocation> visited) {
        List<MapLocation> finalPath = new ArrayList<>(Arrays.asList(end));
        MapLocation next = visited.get(end);
        while (!next.equals(start)) {
            finalPath.add(next);
            next = visited.get(next);
        }
        finalPath.add(start);
        Collections.reverse(finalPath);
        return finalPath;
    }
}
