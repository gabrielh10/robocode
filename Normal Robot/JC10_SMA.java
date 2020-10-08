package cin;
import robocode.*;

import java.awt.Color;
import robocode.util.Utils;
import java.io.IOException;
// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * JC10 - a robot by (your name here)
 */
public class JC10_SMA extends TeamRobot
{
	/**
	 * run: JC10's default behavior
	 */
	private boolean hasTarget = false;

	public void run() {
		// Initialization of the robot should be put here



		// Set the color of this robot containing the RobotColors
		setBodyColor(Color.pink);
		setGunColor(Color.pink);
		setRadarColor(Color.black);
		setScanColor(Color.yellow);
		setBulletColor(Color.darkGray);

		//setColors(Color.red, Color.white, Color.red); // body,gun,radar
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
	
		// Don't fire on teammates
		if (isTeammate(e.getName()) || isMyTeammate(e.getName())) {
			return;
		}
		  
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
		
		// Calculate enemy bearing
		double enemyBearing = this.getHeading() + e.getBearing();
		// Calculate enemy's position
		double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
		double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));

		try {
			// Send enemy position to teammates
			broadcastMessage(new Point(enemyX, enemyY));
		} catch (IOException ex) {
			out.println("Unable to send order: ");
			ex.printStackTrace(out);
		}
		
	}
	
	/**
	 *Processa a mensagem enviada por broadcast 
	 *
	 * onMessageReceived:  What to do when our leader sends a message
	 */
	public void onMessageReceived(MessageEvent e) {
		// Fire at a point
		if (e.getMessage() instanceof Point) {
			Point p = (Point) e.getMessage();
			// Calculate x and y to target
			double dx = p.getX() - this.getX();
			double dy = p.getY() - this.getY();
			// Calculate angle to target
			double theta = Math.toDegrees(Math.atan2(dx, dy));

			// Turn gun to target
			turnGunRight(Utils.normalRelativeAngleDegrees(theta - getGunHeading()));
			// Fire hard!
			fire(3);
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
	
	public boolean isMyTeammate(String robotName){
		if(robotName.contains("JC10_SMA"))
			return true;
		else
			return false;
	}
}
