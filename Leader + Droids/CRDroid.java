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

private java.awt.geom.Rectangle2D.Double _fieldRect;
	/**
	 * run: CRDroid's default behavior
	 */
	public void run() {
	
		setColors(Color.yellow, Color.green, Color.blue); // body,gun,radar
		setScanColor(Color.white);
        setBulletColor(Color.yellow);
	// _bfWidth and _bfHeight set to battle field width and height
    _fieldRect =
    new java.awt.geom.Rectangle2D.Double(18, 18,
    this.getBattleFieldWidth()-36,this.getBattleFieldHeight()-36);		
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
			
			theta = wallSmoothing(this.getX(), this.getY(),Math.max(dx,dy), theta,1, 1); 
		
			// Turn gun to target
			setTurnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
			
			setTurnRight(normalRelativeAngleDegrees(theta - getHeading()));
			//
			double relativeDistance = Math.hypot(dy,dx);			
		
			ahead(relativeDistance-10);
			
			if(relativeDistance < 100)
				setFire(3);
			else{
/*			   getX()
			   getY()
			   getBattleFieldWidth()
			   getBattleFieldHeight()
			   getHeight()
			   getWidth()
			   
*/				ahead(relativeDistance-100);			
			}
		
		}
	}	

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		
			// Calculate x and y to target
			double dx = this.getX()-10;
			double dy = this.getY()-10;
			// Calculate angle to target
			double theta = Math.toDegrees(Math.atan2(dx, dy));

			setTurnRight(normalRelativeAngleDegrees(theta));

			double relativeDistance = Math.hypot(dy,dx);			
		
			back(relativeDistance);

	}
	
	public void onHitRobot(HitRobotEvent e) {
		if(isTeammate(e.getName()) || isMyTeammate(e.getName())){
		// Replace the next line with any behavior you would like
		
			// Calculate x and y to target
			double dx = this.getX()-50;
			double dy = this.getY()-50;
			// Calculate angle to target
			double theta = Math.toDegrees(Math.atan2(dx, dy));
			theta = wallSmoothing(this.getX(), this.getY(),Math.max(dx,dy), theta,1, 1); 
			setTurnRight(normalRelativeAngleDegrees(theta));
			theta = wallSmoothing(this.getX(), this.getY(),Math.max(dx,dy), theta,1, 1); 
			double relativeDistance = Math.hypot(dy,dx);					

			setBack(relativeDistance);
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
	


// ...
/**
 * x/y = current coordinates
 * startAngle = absolute angle that tank starts off moving - this is the angle
 *   they will be moving at if there is no wall smoothing taking place.
 * orientation = 1 if orbiting enemy clockwise, -1 if orbiting counter-clockwise
 * smoothTowardEnemy = 1 if smooth towards enemy, -1 if smooth away
 * NOTE: this method is designed based on an orbital movement system; these
 *   last 2 arguments could be simplified in any other movement system.
 */
public double wallSmoothing(double x, double y,double WALL_STICK, double startAngle,
    int orientation, int smoothTowardEnemy) {

    double angle = startAngle;

    // in Java, (-3 MOD 4) is not 1, so make sure we have some excess
    // positivity here
    angle += (4*Math.PI);

    double testX = x + (Math.sin(angle)*WALL_STICK);
    double testY = y + (Math.cos(angle)*WALL_STICK);
    double wallDistanceX = Math.min(x - 18, this.getBattleFieldWidth() - x - 18);
    double wallDistanceY = Math.min(y - 18, this.getBattleFieldHeight() - y - 18);
    double testDistanceX = Math.min(testX - 18, this.getBattleFieldWidth() - testX - 18);
    double testDistanceY = Math.min(testY - 18, this.getBattleFieldHeight() - testY - 18);

    double adjacent = 0;
    int g = 0; // because I'm paranoid about potential infinite loops

    while (!_fieldRect.contains(testX, testY) && g++ < 25) {
        if (testDistanceY < 0 && testDistanceY < testDistanceX) {
            // wall smooth North or South wall
            angle = ((int)((angle + (Math.PI/2)) / Math.PI)) * Math.PI;
            adjacent = Math.abs(wallDistanceY);
        } else if (testDistanceX < 0 && testDistanceX <= testDistanceY) {
            // wall smooth East or West wall
            angle = (((int)(angle / Math.PI)) * Math.PI) + (Math.PI/2);
            adjacent = Math.abs(wallDistanceX);
        }

        // use your own equivalent of (1 / POSITIVE_INFINITY) instead of 0.005
        // if you want to stay closer to the wall ;)
        angle += smoothTowardEnemy*orientation*
            (Math.abs(Math.acos(adjacent/WALL_STICK)) + 0.005);

        testX = x + (Math.sin(angle)*WALL_STICK);
        testY = y + (Math.cos(angle)*WALL_STICK);
        testDistanceX = Math.min(testX - 18, this.getBattleFieldWidth() - testX - 18);
        testDistanceY = Math.min(testY - 18, this.getBattleFieldHeight() - testY - 18);

        if (smoothTowardEnemy == -1) {
            // this method ended with tank smoothing away from enemy... you may
            // need to note that globally, or maybe you don't care.
        }
    }

    return angle; // you may want to normalize this
}

}
