/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

/** 
 * Game
 * Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
	

	private final static JFrame frame = new JFrame("Bubble/Bomb Fight");
    private static GameState state = GameState.MENU;
    private static String map_file;
	
	public static void switchPane(boolean toSelectMap) throws IOException{
		if(toSelectMap){
			final GameMapOpt map_select = new GameMapOpt();
			frame.add(map_select);
		}
		else{
			switch(state){
			case TWOP:
				final GameDashboard dashboard_2 = new GameDashboard(GameState.TWOP);
				frame.add(dashboard_2, BorderLayout.EAST);
				// Main playing area
				final GameCourt court_2p = new GameCourt_TwoPlayer(dashboard_2, map_file);
				frame.add(court_2p, BorderLayout.CENTER);
				court_2p.reset();
				break;
			case MENU:
				final GameMenu game_menu = new GameMenu();
				frame.add(game_menu);
				break;
			case ONEP:
				final GameDashboard dashboard_1 = new GameDashboard(GameState.ONEP);
				frame.add(dashboard_1, BorderLayout.EAST);
				// Main playing area
				final GameCourt court_1p = new GameCourt_OnePlayer(dashboard_1, map_file);
				frame.add(court_1p, BorderLayout.CENTER);
				court_1p.reset();
				break;
			case HELP:
				//TODO CONTROL HELP
				break;
			}
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



	public static GameState getState() {
		return state;
	}

	public static void setState(GameState state) {
		Game.state = state;
	}

	/**
	 * @param map_file the map_file to set
	 */
	public static void setMap_file(String map_file) {
		Game.map_file = map_file;
	}
	
	

}
