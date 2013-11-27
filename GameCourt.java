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
	private Character character;          // the character, keyboard control
	public boolean playing = false;  // whether the game is running
	private JLabel status;       // Current status text (i.e. Running...)
	private HashSet<Integer> keypressed = new HashSet<Integer>();
		//to keep track the keys pressed
	private Deque<Bubble> bubbles = new ArrayDeque<Bubble>();
		//to keep track of the bubbles dropped
	private Iterator<Bubble> itr;    // the iterator to iterator over bubbles
	private Bubble last_bubble;		 // the last bubble dropped;
	private Point[][] grid = new Point[10][10];
	
	
	// Game constants
	public static final int COURT_WIDTH = 400;
	public static final int COURT_HEIGHT = 400;
	// Update interval for timer in milliseconds 
	public static final int INTERVAL = 5; 

	
	
	
	/**
	 * Initialize the grid by indexing the center of each square
	 */
	private void initGrid(){
		for (int i = 0 ; i < 10; i ++){
			for (int j = 0 ; j < 10; j ++){
				int width = COURT_WIDTH/10;
				int height = COURT_HEIGHT/10;
				int x = width * j + width/2;
				int y = height* i + height/2;
				grid[i][j] = new Point(x, y);
			}
		}
	}
	
	
	public GameCourt(JLabel status){//should consider take in a map as an argument
		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JPanel squares[][] = new JPanel[10][10];
        setLayout(new GridLayout(10, 10));
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                squares[i][j] = new JPanel();
                squares[i][j].setBorder
                	(BorderFactory.createLineBorder(Color.BLACK));;
                squares[i][j].setOpaque(false);
                add(squares[i][j]);
            }
        }
        
        initGrid();

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
				if(keypressed.contains(KeyEvent.VK_SPACE) 
						&& !character.onBubble){
					last_bubble = 
							character.dropBubble(COURT_HEIGHT,COURT_WIDTH,grid);
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
				character.interactWithBubbles(bubbles, last_bubble);
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

		character = new Character(COURT_WIDTH, COURT_HEIGHT);
		bubbles = new ArrayDeque<Bubble>();
		last_bubble = null;
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
			character.move();
			character.interactWithBubbles(bubbles, last_bubble);
			itr = bubbles.iterator();
			while (itr.hasNext()){
				Bubble bubble = itr.next(); 
				bubble.countdown();
			}
			if (last_bubble != null){
				last_bubble.countdown();
				//System.out.println(last_bubble.duration);
				if (last_bubble.duration ==0){
					last_bubble = null;
					character.onBubble = false;
				}
			}
			if (!bubbles.isEmpty()){
				System.out.println(bubbles.getFirst().duration);
				if(bubbles.getFirst().duration <= 0){
					bubbles.removeFirst();
				}
			}
			repaint();
		} 
	}

	@Override 
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		//draw bubbles first
		itr = bubbles.iterator();
		while (itr.hasNext()){
			Bubble bubble_tmp = itr.next();
			bubble_tmp.draw(g);
		}
		//do not draw twice
		if(last_bubble != null && !bubbles.contains(last_bubble)){
			last_bubble.draw(g);
		}
		character.draw(g);
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(COURT_WIDTH,COURT_HEIGHT);
	}
}
