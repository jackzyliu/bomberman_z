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
	private Character character;          // the character, keyboard control
	
	// Update interval for timer in milliseconds 
	public static final int INTERVAL = 5; 

	
	

	
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
			private void keyActions(){
				//if space is pressed && the character walked out of the 
				//previous bubble.
				if(keypressed.contains(KeyEvent.VK_SPACE) ){
					character.dropBubble();
				}
				//if only ONE of the left and right arrow keys are pressed 
				if(keypressed.contains(KeyEvent.VK_LEFT) 
						&& keypressed.contains(KeyEvent.VK_RIGHT)){
					character.resetHor();
				}
				else {
					if (keypressed.contains(KeyEvent.VK_LEFT)){
						character.setDir(Direction.LEFT);
					}
					else if (keypressed.contains(KeyEvent.VK_RIGHT)){
						character.setDir(Direction.RIGHT);
					}
					else{
						character.resetHor();
					}
				}
				//if only ONE of the up and down arrow keys are pressed 
				if(keypressed.contains(KeyEvent.VK_UP)
						&& keypressed.contains(KeyEvent.VK_DOWN)){
					character.resetVer();
				}	
				else{
					if (keypressed.contains(KeyEvent.VK_UP)){
						character.setDir(Direction.UP);
					}
					else if (keypressed.contains(KeyEvent.VK_DOWN)){
						character.setDir(Direction.DOWN);
					}
					else{
						character.resetVer();
					}
				}
				character.interactWithLastBubble();
			}
			public void keyPressed(KeyEvent e){
				keypressed.add(e.getKeyCode());
				keyActions();
			}
			public void keyReleased(KeyEvent e){
				if (!keypressed.isEmpty()){
					keypressed.remove(e.getKeyCode());
				}
				keyActions();
			}
		});
		this.status = status;
		
	}


	/** (Re-)set the state of the game to its initial state.
	 */
	public void reset() {

		character = new Character(map, 1);
		character.bubbles = new ArrayDeque<Bubble>();
		character.last_bubble = null;
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
			character.interactWithLastBubble();
			character.move();
			character.trackBubbles();
			repaint();
		} 
	}

	@Override 
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		map.paint(g);
		character.paintBubbles(g);
		character.draw(g);
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(Map.COURT_WIDTH, Map.COURT_HEIGHT);
	}
}
