package zachderp;
import bc.*;
import java.util.*;


public class Squad {
    private final HashMap<Integer, Unit> units = new HashMap<>();
    private MapLocation ranger1Location;
    private MapLocation ranger2Location;
    private MapLocation healer1Location;
    private MapLocation healer2Location;
    private MapLocation healer3Location;
    private MapLocation destination;
    private boolean isDeployed = false;
    private Set<Integer> squadIDs = new HashSet<>();
    private SimplePathing pathing;
    private boolean hasFoundEnemies = false;
    
    public Squad(SimplePathing pathing) {
        this.pathing = pathing;
    }
    
    public void addSquadMember(Unit unit) {
        if (unit.unitType().equals(UnitType.Ranger) && unit.movementHeat() < 10) {
            if (units.get(1) == null) {
                squadIDs.add(unit.id());
                units.put(1, unit);
                ranger1Location = unit.location().mapLocation();
            }
            else if (units.get(2) == null) {
                squadIDs.add(unit.id());
                units.put(2, unit);
                ranger2Location = unit.location().mapLocation();
            }
        }
        else if (unit.unitType().equals(UnitType.Healer) && unit.movementHeat() < 10) {
            if (units.get(3) == null) {
                squadIDs.add(unit.id());
                units.put(3, unit);
                healer1Location = unit.location().mapLocation();
            }
            else if (units.get(4) == null) {
                squadIDs.add(unit.id());
                units.put(4, unit);
                healer2Location = unit.location().mapLocation();
            }
            else if (units.get(5) == null) {
                squadIDs.add(unit.id());
                units.put(5, unit);
                healer3Location = unit.location().mapLocation();
            }
            else if (units.get(6) == null) {
                squadIDs.add(unit.id());
                units.put(6, unit);
            }
        }
    }
    
    //TODO CHANGE TO 6?
    public boolean isFull() {
        return units.size() == 4;
    }
    
    public boolean isDeployed() {
        return isDeployed;
    }
    
    public boolean hasRangers() {
        return (units.get(1) != null && units.get(2) != null);
    }
    
    public boolean hasHealers() {
        return (units.get(3) != null && units.get(4) != null);//&& units.get(5) != null && units.get(6) != null);
    }
    
    public boolean isInSquad(Unit unit) {
        if (squadIDs.contains(unit.id())) {
            return true;
        }
        return false;
    }
    
    public boolean foundEnemies() {
        return hasFoundEnemies;
    }
    
    public void continueHeading() {
        pathing.moveTo(units.get(1), destination);
        pathing.moveTo(units.get(2), ranger1Location);
        pathing.moveTo(units.get(3), ranger2Location);
        pathing.moveTo(units.get(4), healer1Location);
        //pathing.moveTo(units.get(5), healer2Location);
        //pathing.moveTo(units.get(6), healer3Location);
        ranger1Location = units.get(1).location().mapLocation();
        ranger2Location = units.get(2).location().mapLocation();
        healer1Location = units.get(3).location().mapLocation();
        //healer2Location = units.get(4).location().mapLocation();
        //healer3Location = units.get(5).location().mapLocation(); 
    }
    
    public void moveTo(MapLocation l) {
        isDeployed = true;
        destination = l;
        pathing.moveTo(units.get(1), l);
        pathing.moveTo(units.get(2), ranger1Location);
        pathing.moveTo(units.get(3), ranger2Location);
        pathing.moveTo(units.get(4), healer1Location);
        //pathing.moveTo(units.get(5), healer2Location);
        //pathing.moveTo(units.get(6), healer3Location);
        ranger1Location = units.get(1).location().mapLocation();
        ranger2Location = units.get(2).location().mapLocation();
        healer1Location = units.get(3).location().mapLocation();
        //healer2Location = units.get(4).location().mapLocation();
        //healer3Location = units.get(5).location().mapLocation();
    }
}
