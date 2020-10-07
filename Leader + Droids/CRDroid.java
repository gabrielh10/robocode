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
		
 	// 	setAdjustRadarForGunTurn(true);
	//	setAdjustRadarForRobotTurn (true);

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
		
			ahead(relativeDistance-10);
			
			if(relativeDistance < 100)
				setFire(3);
			else
				ahead(relativeDistance-100);			
		
		}
	}	

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	public void onHitRobot(HitRobotEvent e) {
		if(isTeammate(e.getName()) || isMyTeammate(e.getName())){
			setBack(50);
		} else {	
			double absBearing = e.getBearing() + getHeading();
			setTurnGunRight((absBearing - getGunHeading()) % 360);
			setFire(3);
		}
	}
	
	public boolean isMyTeammate(String robotName){
		if(robotName.contains("cin.CRDroid") || robotName.contains("cin.TeamLeader"))
			return true;
		else
			return false;
	}	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
