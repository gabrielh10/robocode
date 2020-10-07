package cin;
import robocode.*;
import java.awt.Color;
import java.io.IOException;
import robocode.util.Utils;
// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * TeamLeader - a robot by (your name here)
 */
public class TeamLeader extends TeamRobot {
	/**
	 * run: TeamLeader's default behavior
	 */
	
	private boolean hasTarget;	

	public void run() {
		
		setColors(Color.black, Color.white, Color.black); // body,gun,radar
		setScanColor(Color.black);
        setBulletColor(Color.white);
		
 	 	setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn (true);
		
		int counter = 0;
		
		while (true) {
			if(getRadarTurnRemaining() == 0.0)
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
		if (isTeammate(e.getName()) || isMyTeammate(e.getName())) {
			//out.println("not atacking: "+e.getName());
			return;
		}
		//out.println("atacking:"+e.getName());
		double dist = e.getDistance();
				
		 double enemyBearing = getHeading() + e.getBearing();
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
		

		// Calculate enemy's position
		double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
		double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
		

		setTurnGunRightRadians(gunTurn);
		//Don't fire if the energy is low or the enemy hasMore HP
		if(getEnergy() > 40 || getEnergy() >= e.getEnergy()){
			if(dist >= 300){
				fire(3);
			} else if (dist > 100 && dist < 300) {
				fire(2);
			}else{
				fire(1);
			}
		}
		setTurnRadarRightRadians(radarTurn);	

		//out.println(enemyX);
		//out.println(enemyY);
		out.println(e.getName());

		try {
			// Send enemy position to teammates
			broadcastMessage(new Point(enemyX, enemyY));
			//broadcastMessage('a');
		} catch (IOException ex) {
			out.println("Unable to send order: ");
			ex.printStackTrace(out);
		}
	}
	
	public void onRobotDeath(RobotDeathEvent e) {
		hasTarget = false;
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
	    setTurnRight(e.getBearing() + 90);
		back(20);
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
		setTurnLeft(90);
		back(20);
	}	
	
	public boolean isMyTeammate(String robotName){
		if(robotName.contains("cin.CRDroid"))
			return true;
		else
			return false;
	}
}
