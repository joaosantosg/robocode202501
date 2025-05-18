package robocode202501;
import robocode.*;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.Color;

/**
 * robo_02 - A Robocode robot
 * https://gist.github.com/NeonMatrix/4dcc70f43781fb264583de019474c9a6
 */

public class robo_02 extends Robot
{
    /**
     * run: DarthRadar's default behavior
     */
    public void run() {

        // Set colors
        setBodyColor(Color.black);
        setGunColor(Color.red);
        setRadarColor(Color.red);
        setScanColor(Color.red);
        setBulletColor(Color.red);
        // Initialization of the robot should be put here

        // After trying out your robot, try uncommenting the import at the top,
        // and the next line:

        // setColors(Color.red,Color.blue,Color.green); // body,gun,radar

        // Robot main loop
        while(true) {
            // Replace the next 4 lines with any behavior you would like
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        // This ll check if they bot is sentry
/* if(e.isSentryRobot() == true)
    {
     // Do nothing
    }
    else
    {
      // DO code below
    }
  */
        // Distance to EndaBot
        double DisToBot = e.getDistance();
        // Speed Enda is going at
        double velo = e.getVelocity();
        // get angle from us to EndaBot
        double bearing = e.getBearing();
        //The degree whcih enda bot is facing
        double heading = e.getHeading();
        // Our X postion
        double ourX = getX();
        // Our Y position
        double ourY = getY();
        // get EndaBot X postion
        double theirX = (ourX + Math.cos(bearing)) * DisToBot;
        // Get EndaBot Y postion
        double theirY = (ourY + Math.sin(bearing)) * DisToBot;

        // EndaBot distance to point it gets shoot in the ass
        double DisToDoom = (velo * 50);

        // Finding EndaBot's death point X postion
        double newX = (theirX + Math.cos(heading)) * DisToDoom;
        // Finding EndaBot;s death point Y position
        double newY = (theirY + Math.sin(heading)) * DisToDoom;
        // Our distance to EndaBot death point
        double OurDisToDoom =  Math.sqrt(((ourX - newX)*2) + ((ourY - newY)*2));

//        The degree which our Gun is facing, (same as radar)
        double GunHeading = getGunHeading();

        // Temp varible to calucate stuf in cosine rule
        double temp = ((DisToDoom*2) - (OurDisToDoom*2) - (DisToBot*2)) / (-2)*(OurDisToDoom)*(DisToBot);
        // The angle we need to turn, but dont know if it's right or left
        double TurnAngle = (1/Math.cos(temp));


        turnGunLeft(TurnAngle);

        fire(3);

    /*
    	name - the name of the robot your bullet hit
			energy - the remaining energy of the robot that your bullet has hit
			bullet - the bullet that hit the robot
      String name = e.getName();
      double energy = e.getEnergy();
      boolean bullet;
    // function to check when a bullet hits an enemy
		BulletHitEvent()
    {

    }
		*/
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
    }// end main

}
