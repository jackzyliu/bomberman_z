import java.awt.image.BufferedImage;

/**
 * This method is accredited to Youtuber ForeignGuyMike.
 * Here is the link to his channel: http://www.youtube.com/user/ForeignGuyMike
 * 
 * @author Zheyuan Liu
 *
 */
public class Animation {
	private BufferedImage[] frames;
	private int currentFrame;
	
	private long startTime;
	private long delay;
	
	public Animation(){
		
	}
	
	public void setFrames(BufferedImage[] images){
		frames = images;
		if(currentFrame >=frames.length){
			currentFrame = 0;
		}
	}
	
	public void setDelay(long d){
		delay = d;
	}
	
	public void update(){
		if (delay == -1) return;
		long elapsed = (System.nanoTime() - startTime) / 1000000;
		if (elapsed > delay){
			currentFrame++;
			startTime = System.nanoTime();
		}
		if(currentFrame == frames.length){
			currentFrame = 0;
		}
	}
	
	public BufferedImage getImage(){
		return frames[currentFrame];
	}
}
