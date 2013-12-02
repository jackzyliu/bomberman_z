import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class GameTimer extends JPanel{
	
	// Update interval for timer in milliseconds 
	public static final int INTERVAL = 1000; 
	public static final int WIDTH = 100;
	public static final int HEIGHT = 100;
	
	public JLabel time;       // Current status text
	public int time_length;   //in seconds
	
	private Timer timer;
	
	public GameTimer(int timelength, final JLabel time){

		//setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.BLUE);
		timer = new Timer(INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				time_length --;
				time.setText(Integer.toString(time_length));
				repaint();
			}
		});
		
		timer.start(); 
		
		this.time_length = timelength;
		this.time = time;
	}

	@Override
	public Dimension getPreferredSize(){
		return new Dimension(100, 100);
	}
	
	public boolean isOver(){
		return time_length <= 0;
	}
	
	public void stopCounting(){
		this.timer.stop();
	}
}
