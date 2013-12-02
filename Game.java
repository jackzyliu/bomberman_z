/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** 
 * Game
 * Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
	

	private final static JFrame frame = new JFrame("Bubble/Bomb Fight");
    
	
	public static void switchPane(GameState state){
		if(state == GameState.TWOP){
			
			/*
			frame.validate();
    		frame.repaint();
*/
			//frame.getComponent(0).setVisible(false);
			
			/*
			// Status panel
			final JPanel status_panel = new JPanel();
			frame.add(status_panel, BorderLayout.SOUTH);
			status_panel.add(status);
			 */        
			
			final JLabel timer_label = new JLabel("1000");
    		final GameTimer timer = new GameTimer(1000, timer_label);
    		timer.add(timer_label);
    		frame.add(timer, BorderLayout.EAST);
    		// Main playing area
    		final GameCourt court = new GameCourt_TwoPlayer(timer);
    		frame.add(court, BorderLayout.CENTER);
    		court.reset();
    		
		}
	}
	
	
	
    public void run(){
        // NOTE : recall that the 'final' keyword notes inmutability
		  // even for local variables. 

        // Top-level frame in which game components live
		// Be sure to change "TOP LEVEL FRAME" to the name of your game
    	frame.setLocation(0,0);
        frame.setResizable(false);
      
        final GameMenu game_menu = new GameMenu();

        frame.add(game_menu);
        
        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    /*
     * Main method run to start and run the game
     * Initializes the GUI elements specified in Game and runs it
     * NOTE: Do NOT delete! You MUST include this in the final submission of your game.
     */
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Game());
    }

}
