package cin;
import robocode.*;
import java.awt.Color;
import robocode.util.Utils;
import static robocode.util.Utils.normalRelativeAngleDegrees;
// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * CRDroid - a robot by (your name here)
 */
public class CRDroid extends TeamRobot implements Droid {

	/**
	 * run: CRDroid's default behavior
	 */
	public void run() {
	
		setColors(Color.yellow, Color.green, Color.blue); // body,gun,radar
		setScanColor(Color.white);
        setBulletColor(Color.yellow);
		
 	 	setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn (true);

		while(true) {
			// Replace the next 4 lines with any behavior you would like
			setAhead(20);

			setBack(20);
			execute();
		}
	}

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
			setTurnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
			
			setTurnRight(normalRelativeAngleDegrees(theta - getHeading()));
			//
			double relativeDistance = Math.hypot(dy,dx);			
			ahead(relativeDistance);

			// Fire hard!
			setFire(3);
		} // Set our colors
	}	

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
