import bc.*;

// This is an immutable ADT
public class Twople {
    
    private final int x;
    private final int y;
    private final Planet planet;
    
    /**
     * @param x - x
     * @param y - y
     * @param planet - planet
     */
    public Twople(MapLocation m) {
        this.x = m.getX();
        this.y = m.getY();
        this.planet = m.getPlanet();
    }
    
    /**
     * @returns x
     */
    public int getX() {
        return x;
    }
    
    /**
     * @returns y
     */
    public int getY() {
        return y;
    }
    
    /**
     * @returns the planet
     */
    public Planet getPlanet() {
        return planet;
    }
    
    /**
     * @returns mapLocation
     */
    public MapLocation getMapLocation()
    {
    	return new MapLocation(planet, x, y);
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Twople)) { 
            return false;
        }
        return (((Twople)other).x == x && ((Twople)other).y == y && ((Twople)other).planet.equals(planet)); 
    }
    
    @Override
    public String toString() {
        return "Planet: " + planet + "x: " + x + "y: " + y;
    }
    
    @Override 
    public int hashCode() {
        return x*3+y*2+planet.toString().hashCode();
    }

}
