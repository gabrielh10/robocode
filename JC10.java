package cin;
import robocode.*;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * JC10 - a robot by (your name here)
 */
public class JC10 extends Robot
{
	/**
	 * run: JC10's default behavior
	 */
	private boolean hasTarget = false;

	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		setColors(Color.red, Color.white, Color.red); // body,gun,radar
		turnRight(360);
		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			turnRight(360);
			ahead(100);		
			back(75);
			if(!hasTarget)
				turnRight(360);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		this.hasTarget = true;
		
		double angle = e.getBearing();
		double dist = e.getDistance();
		
		turnRight(angle);
		if(dist >= 300){
			fire(3);
		} else if (dist > 100 && dist < 300) {
			fire(2);
		}else{
			fire(1);
		}
		
	}
	
	public void onHitRobot(HitRobotEvent e) {
		this.hasTarget = true;
		back(50);
		turnRight(e.getBearing());
		fire(3);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		this.hasTarget = true;
		back(10);
		turnRight(e.getBearing());
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// return a little and take a left turn
		back(20);
		turnLeft(90);
	}	
}
