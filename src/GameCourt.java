/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;

import javax.swing.*;



/**
 * GameCourt
 * 
 * This class holds the primary game logic of how different objects 
 * interact with one another.  Take time to understand how the timer 
 * interacts with the different methods and how it repaints the GUI 
 * on every tick().
 *
 */



	//TODO fix the sensitive nature of the algorithm for "explode"
	//TODO change the map
	//TODO non-random item
	//TODO walk side way

@SuppressWarnings("serial")
public class GameCourt extends JPanel {

	
	// Update interval for timer in milliseconds 
	public static final int INTERVAL = 10; 

	public enum GameResult{
		PLAYER1,
		PLAYER2,
		CLEAR,
		LOSE,
		DRAW,
		NONE;
	}


	/** (Re-)set the state of the game to its initial state.
	 * To be Overridden
	 */
	public void reset() {

	}

    /**
     * This method is called every time the timer defined
     * in the constructor triggers.
     * To be Overridden
     */
	void tick(){
		
	}

	@Override
	public Dimension getPreferredSize(){
		return new Dimension(Map.COURT_WIDTH, Map.COURT_HEIGHT);
	}
}
