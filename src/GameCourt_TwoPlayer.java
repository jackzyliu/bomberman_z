import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class GameCourt_TwoPlayer extends GameCourt{
	private boolean playing;

	private GameTimer game_timer;
	private Player player1;         
	private Player player2;
	private HashSet<Integer> keypressed = new HashSet<Integer>();
	//to keep track the keys pressed
	private Map map;
	
	public GameCourt_TwoPlayer
		(GameTimer game_timer, String map_file) throws IOException{
		//super(status, game_timer);
		Timer timer = new Timer(INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				tick();
			}
		});
		
		timer.start(); // MAKE SURE TO START THE TIMER!
		setFocusable(true);
		
		addKeyListener(new KeyAdapter(){
			
			/**Helper:
			 * perform a series of actions related to a combination of keys
			 *  pressed
			 */
			private void keyActions_player1(){
				//if space is pressed && the character walked out of the 
				//previous bubble.
				if(keypressed.contains(KeyEvent.VK_ENTER) ){
					
					player1.dropBubble();
				}
				//if only ONE of the left and right arrow keys are pressed 
				if(keypressed.contains(KeyEvent.VK_LEFT) 
						&& keypressed.contains(KeyEvent.VK_RIGHT)){
					player1.resetHor();
				}
				else {
					if (keypressed.contains(KeyEvent.VK_LEFT)){
						player1.setDir(Direction.LEFT);
					}
					else if (keypressed.contains(KeyEvent.VK_RIGHT)){
						player1.setDir(Direction.RIGHT);
					}
					else{
						player1.resetHor();
					}
				}
				//if only ONE of the up and down arrow keys are pressed 
				if(keypressed.contains(KeyEvent.VK_UP)
						&& keypressed.contains(KeyEvent.VK_DOWN)){
					player1.resetVer();
				}	
				else{
					if (keypressed.contains(KeyEvent.VK_UP)){
						player1.setDir(Direction.UP);
					}
					else if (keypressed.contains(KeyEvent.VK_DOWN)){
						player1.setDir(Direction.DOWN);
					}
					else{
						player1.resetVer();
					}
				}
				player1.collectItems();
				player1.interactWithLastBubble();
				map.interactWithUnwalkables(player1);

				
			}
			/**Helper:
			 * perform a series of actions related to a combination of keys
			 *  pressed
			 */
			private void keyActions_player2(){

				
				
				//if space is pressed && the character walked out of the 
				//previous bubble.
				if(keypressed.contains(KeyEvent.VK_Z) ){
					
					player2.dropBubble();
				}
				//if only ONE of the left and right arrow keys are pressed 
				if(keypressed.contains(KeyEvent.VK_A) 
						&& keypressed.contains(KeyEvent.VK_D)){
					player2.resetHor();
				}
				else {
					if (keypressed.contains(KeyEvent.VK_A)){
						player2.setDir(Direction.LEFT);
					}
					else if (keypressed.contains(KeyEvent.VK_D)){
						player2.setDir(Direction.RIGHT);
					}
					else{
						player2.resetHor();
					}
				}
				//if only ONE of the up and down arrow keys are pressed 
				if(keypressed.contains(KeyEvent.VK_W)
						&& keypressed.contains(KeyEvent.VK_S)){
					player2.resetVer();
				}	
				else{
					if (keypressed.contains(KeyEvent.VK_W)){
						player2.setDir(Direction.UP);
					}
					else if (keypressed.contains(KeyEvent.VK_S)){
						player2.setDir(Direction.DOWN);
					}
					else{
						player2.resetVer();
					}
				}
				player2.collectItems();
				player2.interactWithLastBubble();
				map.interactWithUnwalkables(player2);
			}
			
			
			
			public void keyPressed(KeyEvent e){
				keypressed.add(e.getKeyCode());
				keyActions_player1();
				keyActions_player2();
			}
			public void keyReleased(KeyEvent e){
				if (!keypressed.isEmpty()){
					keypressed.remove(e.getKeyCode());
				}
				keyActions_player1();
				keyActions_player2();
			}
		});
		
		this.game_timer = game_timer;
		this.map = new Map(map_file);
	}
	/** (Re-)set the state of the game to its initial state.
	 */
	public void reset() {

		player1 = new Player(map, 1);
		player2 = new Player(map, 2);
		playing = true;
		// Make sure that this component has the keyboard focus
		requestFocusInWindow();
	}

    /**
     * This method is called every time the timer defined
     * in the constructor triggers.
     */
	@Override
	void tick(){
		if (playing) {
			player1.interactWithLastBubble();
			player1.move();
			player1.trackBubbles();
			player2.interactWithLastBubble();
			player2.move();
			player2.trackBubbles();
			
			map.trackExplosions();
			map.interactWithUnwalkables(player1);
			map.interactWithUnwalkables(player2);
			
			playing = (!player1.isExploded() &&!player2.isExploded() && !game_timer.isOver());
			repaint();
		} 
		else{

			game_timer.stopCounting();
		}
	}

	@Override 
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		map.paint(g);
		map.drawAreaExplosionAndItems(g);
		player1.paintBubbles(g);
		player2.paintBubbles(g);
		//draw the person in the front first
		//TODO make this a separate method while developing multi-layer mode
		
		if(player1.pos_y >= player2.pos_y){
			player1.draw(g);
			player2.draw(g);
		}
		else{
			player2.draw(g);
			player1.draw(g);		
		}
		
	}
}
