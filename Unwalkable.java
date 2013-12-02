import java.awt.Point;


public class Unwalkable extends GameObj{
	
	public static final int WIDTH = 40;
	public static final int HEIGHT = 40;
	
	private int index_i;
	private int index_j;
	
	public Unwalkable(int i, int j){
		super(0, 0, j*Map.TILE_SIZE, i*Map.TILE_SIZE, WIDTH, HEIGHT, 
				Map.COURT_WIDTH, Map.COURT_HEIGHT);
		this.index_i = i;
		this.index_j = j;
	}
	
	public Point getIndex(){
		return new Point (index_i, index_j);
	}
	
	public boolean isWalkingAway(GameObj player){
		int current_dx = Math.abs(getCenter().x - player.getCenter().x);
		int current_dy = Math.abs(getCenter().y - player.getCenter().y);
		double current_distance = Math.sqrt(current_dx * current_dx + current_dy * current_dy );
		
		int next_dx = Math.abs(getCenter().x - 
				(player.getCenter().x + player.v_x));
		int next_dy = Math.abs(getCenter().y - 
				(player.getCenter().y + player.v_y));
		double next_distance = Math.sqrt(next_dx * next_dx + next_dy * next_dy);
	
		return Double.compare(next_distance, current_distance) > 0;
	}

}
