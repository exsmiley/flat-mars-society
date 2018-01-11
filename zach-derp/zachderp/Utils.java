package zachderp;

import bc.*;

public class Utils {
	
	private GameController gc;

	public Utils(GameController gc) {
		this.gc = gc;
	}
	
	/**
	 * Generic method to randomly choose an element from an array
	 * @param arr array to choose an element from
	 * @return some element in arr
	 */
	public static <T> T chooseRandom(T[] arr) {
		return arr[(int) (Math.random()*arr.length)];
	}
	
	public static int randomNum(int big) {
		return (int) (Math.random()*big);
	}
	
	/**
	 * Tries to move the robot North, West, South, East
	 * @param id id of the robot to move
	 */
	public void moveRobotSpiral(int id) {
		if(gc.canMove(id, Direction.North)) {
			gc.moveRobot(id, Direction.North);
		}
		else if(gc.canMove(id, Direction.East)) {
			gc.moveRobot(id, Direction.East);
		}
		else if(gc.canMove(id, Direction.South)) {
			gc.moveRobot(id, Direction.South);
		}
		else if(gc.canMove(id, Direction.West)) {
			gc.moveRobot(id, Direction.West);
		}
	}
	
	/**
	 * Tries to replicate this robot on an adjacent square if it can
	 * @param id id of the robot to replicate
	 */
	public void replicateSomewhere(int id) {
		if(gc.canReplicate(id, Direction.South)) {
			gc.replicate(id, Direction.South);
		}
		else if(gc.canReplicate(id, Direction.East)) {
			gc.replicate(id, Direction.East);
		}
		else if(gc.canReplicate(id, Direction.North)) {
			gc.replicate(id, Direction.North);
		}
		else if(gc.canReplicate(id, Direction.West)) {
			gc.replicate(id, Direction.West);
		}
	}
	
	
	/**
	 * Looks at the orbit pattern to get the optimal rocket launching times (>100) and orders them
	 */
	public void planRocketLaunches() {
		// TODO find optimal stuffs
		OrbitPattern pattern = gc.orbitPattern();
		pattern.getPeriod();
	}
}
