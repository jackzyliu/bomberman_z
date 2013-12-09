
/**
 * This class specifies the standard to measure the distance between two tiles
 * in this game.
 * The only method simply add the x difference and the y difference.
 * @author Zheyuan Liu
 *
 */
public class ManhattanDistance {
	public int getCost(int start_x, int start_y, int target_x, int target_y){
		int dx = Math.abs(start_x - target_x);
		int dy = Math.abs(start_y - target_y);
		
		return (dx + dy);
	}
}
