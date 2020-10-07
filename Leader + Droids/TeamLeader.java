package cin;
import robocode.*;
import java.awt.Color;
import java.io.IOException;
// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * TeamLeader - a robot by (your name here)
 */
public class TeamLeader extends TeamRobot {
	/**
	 * run: TeamLeader's default behavior
	 */
	public void run() {
		
		setColors(Color.black, Color.white, Color.black); // body,gun,radar
		setScanColor(Color.black);
        setBulletColor(Color.white);
		
 	 	setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn (true);
		
		while (true) {
			setTurnRadarRight(10000);
			ahead(20);
			back(20);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Don't fire on teammates
		if (isTeammate(e.getName())) {
			return;
		}
		
		double angle = e.getBearing();
		double dist = e.getDistance();
		
		turnRight(angle);
		
	//criar condiçao para só atirar com hp maior que a metade (lider nao deve morrer)
		
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
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
	    setTurnRight(e.getBearing() + 90);
		back(20);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		setTurnLeft(90);
		back(20);
	}	
}
