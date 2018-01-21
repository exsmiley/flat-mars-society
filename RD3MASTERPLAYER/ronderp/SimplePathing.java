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
            System.out.println(finalPath);
            paths.put(unit, finalPath);
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
        finalPath.add(new Twople(start));
        Collections.reverse(finalPath);
        List<MapLocation> newestFinalPath = new ArrayList<>();
        for (Twople t: finalPath) {
            newestFinalPath.add(new MapLocation(t.getPlanet(), t.getX(), t.getY()));
        }
        return newestFinalPath;
    }
}
