import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;


/**
 * This method is a path finder that uses A* algorithm
 * Note: all the variables used for specifying locations are indices (i and j)
 * @author Zheyuan Liu
 *
 */
public class PathFinder {
	
	private int movement = 1; //any moving obj can only move one step at a time;

	private ArrayList<Node> discarded = new ArrayList<Node>();
	private ArrayList<Node> unchecked = new ArrayList<Node>();
	
	private Map map;
	private int max_depth = 10;  //stop searching if we have looked 10 steps
	
	private Node[][] nodes;
	private ManhattanDistance hueristic = new ManhattanDistance();
	
	//private boolean[][] visited;   debugging tool
	
	/**
	 * Constructor that defines the map and initializes nodes[][];
	 * @param map
	 */
	public PathFinder(Map map){
		this.map = map;
		this.nodes = new Node[Map.HEIGHT][Map.WIDTH];
		//this.visited = new boolean[Map.HEIGHT][Map.WIDTH];
		for (int i = 0 ; i < Map.HEIGHT; i++) {
			for (int j = 0 ; j < Map.WIDTH; j++) {
				nodes[i][j] = new Node(i,j);
				//visited[i][j] = false;
			}
		}
	}
	
	public Path findPath(Player e, int t_i, int t_j){
		if(map.isBlocked(t_i, t_j, e)) {
			return null;
		}
		
		//initialize the path finding process
		Point start = map.toIndex(e.getCenter().x, e.getCenter().y);
		nodes[start.x][start.y].path_cost = 0;
		nodes[start.x][start.y].depth =0;
		discarded.clear();
		unchecked.clear();
		unchecked.add(nodes[start.x][start.y]);
		
		nodes[t_i][t_j].parent = null;
		
		//the body of search
		int max_depth = 0;
		while( max_depth < this.max_depth && unchecked.size() != 0 ){

			//if we are already on the target node
			Node current = unchecked.get(0);
			if( current == nodes[t_i][t_j]){
				break;
			}
			//if we are not
			unchecked.remove(current);
			discarded.add(current);
			
			for(int i = map.clip(current.i -1, 'h'); 
					i <= map.clip(current.i + 1 , 'h');
					i++){
				for(int j = map.clip(current.j -1, 'w'); 
						j <= map.clip(current.j +1, 'w'); 
						j++){
					
					if((i == current.i && j == current.j) ||
					   (i != current.i && j != current.j)){
					//can be the current position or the corner nodes 
					//since diagonal movement is not allowed.
						continue;
					}
					
					
					if(!map.isBlocked(i, j, e) && (i != start.x || j!= start.y)){

						int next_cost = current.path_cost + this.movement;
						Node neighbor = nodes[i][j];
						//this.visited[i][j] = true;
						
						//if the next_cost is lower than the cost of this neighbor
						//determined previously, this node needs to be reevaluated
						if(next_cost < neighbor.path_cost){
							if(unchecked.contains(neighbor)){
								unchecked.remove(neighbor);
							}
							if(discarded.contains(neighbor)){
								discarded.remove(neighbor);
							}
						}
						
						//if this neighbor node hasnt be processed or discarded,
						//set its cost to the the cost we just got and add it as
						//a next possible step
						if (!unchecked.contains(neighbor) && 
						    !discarded.contains(neighbor)){
							neighbor.path_cost = next_cost;
							neighbor.distance_to_target = 
									hueristic.getCost(i, j, t_i, t_j);
							max_depth = Math.max(max_depth, 
									             neighbor.setParent(current));
							unchecked.add(neighbor);
							Collections.sort(unchecked);
							unchecked.trimToSize();
						}
					}
				}
			}
		}
		
		//now we have finished the search 
		//if the target node has no parent then we return null path
		if(nodes[t_i][t_j].parent == null){

			return null;
		}
		else{

			//we build up a path by back tracing the nodes.
			Path path = new Path();
			Node target = nodes[t_i][t_j];
			while(target != nodes[start.x][start.y]){
				path.prependStep(new Point(target.i, target.j));
				target = target.parent;
			}
			path.prependStep(new Point(start.x, start.y));
			
			return path;
		}
	}

	/**
	 * A NODE class used during the search process
	 * @author Zheyuan Liu
	 *
	 */
	private class Node implements Comparable<Object>{

		private int i;
		private int j;
		private int path_cost;
		private Node parent;
		private int distance_to_target;
		private int depth;
		
		/**
		 * A new node
		 * @param x
		 * @param y
		 */
		public Node(int i, int j){
			this.i = i;
			this.j = j;
		}
		
		/**
		 * This method sets the parent node of the CURRENT NODE 
		 * @param parent
		 * @return the depth of the search
		 */
		public int setParent(Node parent){
			depth = parent.depth + 1;
			this.parent = parent;
			
			return depth;
		}
		
		/**
		 * This comparison only takes the distance to target into account;
		 */
		@Override
		public int compareTo(Object other) {
			Node o = (Node) other;
			
			int d = distance_to_target + path_cost;
			int o_d = o.distance_to_target + o.path_cost;
			
			if(d < o_d){
				return -1;
			}
			else if (d > o_d){
				return 1;
			}
			else{
				return 0;
			}
		}
		
	}
	
}
