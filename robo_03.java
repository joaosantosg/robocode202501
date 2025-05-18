package robocode202501;

import robocode.RobotDeathEvent;
import robocode.HitWallEvent;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import robocode.MessageEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.*;
import java.io.IOException;

/**
 * Walls - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
 * <p/>
 * Moves around the outer edge with the gun facing in.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Ludovic David (contributor)
 */
public class robo_03 extends TeamRobot {

    double moveAmount; // How much to move
    RobotData enemy; // Last or current enemy
    double tresholdTime; // Time (in turn) before search for a new opponent.

    /**
     * run: Move around the walls
     */
    public void run() {
        // Set colors
        setBodyColor(Color.black);
        setGunColor(Color.black);
        setRadarColor(Color.orange);
        setBulletColor(Color.cyan);
        setScanColor(Color.cyan);

        // Don't turn the gun with the robot.
        setAdjustGunForRobotTurn(true);

        // Initalize thresholdTime to 10 turn
        tresholdTime = 10;
        // Initialize moveAmount to the maximum possible for this battlefield.
        moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
        // Initialize enemy to impossible.
        enemy = new RobotData();
        enemy.name = "";
        enemy.energy = Double.POSITIVE_INFINITY;
        enemy.elapsedTime = Double.POSITIVE_INFINITY;

        // turnLeft to face a wall.
        // getHeading() % 90 means the remainder of getHeading() divided by 90.
        setTurnLeft(getHeading() % 90);
        while(getTurnRemaining() > 0) { execute(); }

        // Go to the wall
        setAhead(moveAmount);
        while(getDistanceRemaining() > 0) { execute(); }

        setTurnGunRight(360);

        // Turn perpendicularly to the wall
        setTurnRight(90);
        while(getTurnRemaining() > 0) { execute(); }

        while (true) {
            setAhead(moveAmount); // Move up the wall
            while(getDistanceRemaining() > 0) {
                checkAndExecute();
            }

            setTurnRight(90); // Turn to the next wall
            while(getTurnRemaining() > 0) {
                checkAndExecute();
            }
        }
    }

    void checkAndExecute() {
        ++enemy.elapsedTime;
        if (getGunTurnRemaining() <= 0 && enemy.elapsedTime > tresholdTime) {
            setTurnGunRight(360);
        }
        execute();
    }

    /**
     * onHitRobot:  Move away a bit.
     */
    public void onHitRobot(HitRobotEvent e) {
        // If he's in front of us, set back up a bit.
        if (e.getBearing() > -90 && e.getBearing() < 90) {
            setBack(100);
        } else { // else he's in back of us, so set ahead a bit.
            setAhead(100);
        }
    }

    public void onHitWall(HitWallEvent e) {
        out.println("bad news !");
    }

    /**
     * onScannedRobot:  Fire!
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        // Don't fire on teammates
        if (isTeammate(e.getName())) {
            return;
        }

        boolean anotherEnemy = enemy.name != e.getName();
        boolean energyTooHigh = enemy.energy <= e.getEnergy();
        boolean notTimeToChange = enemy.elapsedTime < tresholdTime;

        if (anotherEnemy && energyTooHigh && notTimeToChange) {
            setFire(1000 / e.getDistance()); // it's still an enemy
            return;
        } else {
            enemy.name = e.getName();
            enemy.energy = e.getEnergy();
            enemy.elapsedTime = 0;
        }

        // Calculate enemy bearing
        double enemyBearing = this.getHeading() + e.getBearing();

        // Calculate enemy's position
        double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
        double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));

        try {
            broadcastMessage(new Point(enemyX, enemyY)); // Send enemy position to teammates
        } catch (IOException ex) {
            out.println("Unable to send order: ");
            ex.printStackTrace(out);
        }

        setFire(1000 / e.getDistance());
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
        }
    }

    public void onRobotDeath(RobotDeathEvent e) {
        if (enemy.name == e.getName()) {
            enemy.name = "";
            enemy.energy = Double.POSITIVE_INFINITY;
        }
    }

    // Class used as container for data
    static class RobotData {
        public String name;
        public double energy;
        public double elapsedTime;
    }

    static class Point implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private double x = 0.0;
        private double y = 0.0;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

}
