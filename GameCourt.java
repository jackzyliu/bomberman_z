/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;

/**
 * GameCourt
 * 
 * This class holds the primary game logic of how different objects 
 * interact with one another.  Take time to understand how the timer 
 * interacts with the different methods and how it repaints the GUI 
 * on every tick().
 *
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

	// the state of the game logic

	public boolean playing = false;  // whether the game is running
	private JLabel status;       // Current status text (i.e. Running...)
	private HashSet<Integer> keypressed = new HashSet<Integer>();
		//to keep track the keys pressed

	private Map map = new Map();
	private Player player1;          // the character, keyboard control
	private Player player2;
	
	// Update interval for timer in milliseconds 
	public static final int INTERVAL = 10; 

	
	

	
	public GameCourt(JLabel status){//should consider take in a map as an argument
		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
        // The timer is an object which triggers an action periodically
        // with the given INTERVAL. One registers an ActionListener with
        // this timer, whose actionPerformed() method will be called 
        // each time the timer triggers. We define a helper method
        // called tick() that actually does everything that should
        // be done in a single timestep.
		Timer timer = new Timer(INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				tick();
			}
		});
		timer.start(); // MAKE SURE TO START THE TIMER!

		// Enable keyboard focus on the court area
		// When this component has the keyboard focus, key
		// events will be handled by its key listener.
		setFocusable(true);

		// this key listener allows the square to move as long
		// as an arrow key is pressed, by changing the square's
		// velocity accordingly. (The tick method below actually 
		// moves the square.)
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
			}
			/**Helper:
			 * perform a series of actions related to a combination of keys
			 *  pressed
			 */
			private void keyActions_player2(){
				//if space is pressed && the character walked out of the 
				//previous bubble.
				if(keypressed.contains(KeyEvent.VK_SHIFT) ){
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
		this.status = status;
		
	}


	/** (Re-)set the state of the game to its initial state.
	 */
	public void reset() {

		player1 = new Player(map, 1);
		player2 = new Player(map, 2);
		playing = true;
		status.setText("Running...");
		// Make sure that this component has the keyboard focus
		requestFocusInWindow();
	}

    /**
     * This method is called every time the timer defined
     * in the constructor triggers.
     */
	void tick(){
		if (playing) {
			player1.interactWithLastBubble();
			player1.move();
			player1.trackBubbles();
			player2.interactWithLastBubble();
			player2.move();
			player2.trackBubbles();
			map.trackExplosions();
			//playing = !character.isExploded();
			
			repaint();
		} 
		else{
			status.setText("You died.");
		}
	}

	@Override 
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		map.paint(g);
		map.drawAreaExplosionAndItems(g);
		player1.paintBubbles(g);
		player2.paintBubbles(g);
		player1.draw(g);
		player2.draw(g);
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(Map.COURT_WIDTH, Map.COURT_HEIGHT);
	}
}
