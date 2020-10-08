package cin;
import robocode.*;
import java.awt.Color;
import robocode.util.Utils;
import java.io.IOException;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * R10 - a robot by (Gabriel, Júlia e Nico)
 */
public class R10_SMA extends TeamRobot
{
	/**
	 * run: R10's default behavior
	 */

	public void run() {
	
		setColors(Color.green, Color.yellow, Color.blue); // body,gun,radar
		setScanColor(Color.blue);
        setBulletColor(Color.white);
		
 	 	setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn (true);
				
		int counter = 0;
		
		out.println("StartGame");
		
		while(true){
			if (getRadarTurnRemaining() == 0.0)
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
				
			if(counter<16)
				setAhead(20);
			else
				setBack(15);
			
			counter = (counter+1) % 32;
			
			execute();
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
	
		 double dist = e.getDistance();
	
   		 double angleToEnemy = getHeadingRadians() + e.getBearingRadians();

  		 double radarTurn = Utils.normalRelativeAngle( angleToEnemy - getRadarHeadingRadians() );
		 double gunTurn = Utils.normalRelativeAngle (angleToEnemy - getGunHeadingRadians() );

  		 double extraTurn = Math.min( Math.atan( 36.0 / e.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );

  		  if (radarTurn < 0){
     		  radarTurn -= extraTurn;
			 // gunTurn -= extraTurn;
  		  } else {
      		  radarTurn += extraTurn;
			  // gunTurn += extraTurn;
    	  }

	      setTurnGunRightRadians(gunTurn);
		 
	      if(dist >= 300){
		  	setFire(3);
		  } else if (dist > 200 && dist < 300) {
			setFire(2);
		  } else {
			setFire(1);
		  }
	
   		 setTurnRadarRightRadians(radarTurn);
		 
		double enemyX = getX() + e.getDistance() * Math.sin(angleToEnemy);
		double enemyY = getY() + e.getDistance() * Math.cos(angleToEnemy);
		
		try {
			// Send enemy position to teammates
			broadcastMessage(new Point(enemyX, enemyY));
		} catch (IOException ex) {
			out.println("Unable to send order: ");
			ex.printStackTrace(out);
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
			turnGunRight(Utils.normalRelativeAngleDegrees(theta - getGunHeading()));
			// Fire hard!
			fire(3);
		} 

	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		setBack(10);
		setTurnRight(20);
		//turnGunRight(e.getBearing());
	}
	
	public void onHitRobot(HitRobotEvent e) {
		back(50);
		turnRight(20);
		setFire(3);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
