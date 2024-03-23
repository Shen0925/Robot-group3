package robots.first;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.DeathEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class XiaobinRobo3 extends AdvancedRobot {
    private static final double BATTLE_WIDTH = 800.0;
    private static final double BATTLE_HEIGHT = 600.0;
    private static final double POWER = 4.0;
    private static final double BULLET_VELOCITY = 10.0;
    private static final double ESCPAE_ANGLE = 1.8;
    private static ArrayList waves = new ArrayList();
    private static double enemyEnergy;
    private static double HOT_Trick;
    private static Point2D.Double enemyPosition;

    public XiaobinRobo3() {
    }

    public void run() {
        this.setAdjustGunForRobotTurn(true);
        this.setAdjustRadarForGunTurn(true);
        setColors(Color.green, Color.green, Color.green);    //设置机器人的颜色

        while(true) {
            this.turnRadarRightRadians(1.0);
        }
    }

    public void onDeath(DeathEvent event) {
        HOT_Trick = 0.65;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double absBearing;
        double edistance;
        double moveDistance;
        Point2D.Double myPosition;
        enemyPosition = nextPoint(myPosition = new Point2D.Double(this.getX(), this.getY()), absBearing = e.getBearingRadians() + this.getHeadingRadians(), moveDistance = edistance = e.getDistance());
        this.setTurnRadarRightRadians(Math.sin(absBearing - this.getRadarHeadingRadians()));
        double power = Math.min(2.0, enemyEnergy / 5.0);
        if (this.getEnergy() > power) {
            this.setFire(power);
        }

        double moveAngle;
        if (enemyEnergy != (enemyEnergy = e.getEnergy()) && Math.abs(this.getDistanceRemaining()) < 52.0) {
            Point2D.Double nextP;
            while(distanceToWall(nextP = nextPoint(enemyPosition, absBearing + (moveAngle = Math.random() * 1.5 - HOT_Trick), -(moveDistance -= 10.0) / Math.cos(moveAngle))) < 24.0) {
            }

            this.setAhead((double)((moveAngle = Utils.normalRelativeAngle(getAngle(nextP, myPosition) - this.getHeadingRadians())) == (moveDistance = Math.atan(Math.tan(moveAngle))) ? 1 : -1) * myPosition.distance(nextP));
            this.setTurnRightRadians(moveDistance);
        }

        int size = waves.size();
        MicroWave wave;
        waves.add(wave = new MicroWave());
        moveDistance = absBearing;
        wave.absBearing = absBearing;
        wave.compareValue = new Point2D.Double(edistance / 64.0, e.getVelocity() * Math.sin(e.getHeadingRadians() - absBearing));
        wave.startPosition = myPosition;
        this.addCustomEvent(wave);
        if (this.getGunHeat() < 0.4) {
            for(int i = Math.max(71, size - 4500); i < size; ++i) {
                if ((wave = (MicroWave)waves.get(i)).startPosition.x < 10.0) {
                    int j = 0;
                    double div = 0.0;
                    double comVal = 0.0;

                    do {
                        comVal += ((MicroWave)waves.get(size - j)).compareValue.distanceSq(((MicroWave)waves.get(i - j)).compareValue) / (div = div * 2.0 + 1.0);
                        j += 7;
                    } while(j < 71);

                    if (comVal <= edistance && distanceToWall(nextPoint(myPosition, moveAngle = absBearing + Math.asin(Math.sin(wave.startPosition.x) / (1.818182 - power * 0.2727273)), wave.startPosition.y)) > 17.0) {
                        moveDistance = moveAngle;
                        edistance = comVal;
                    }
                }
            }
        }

        this.setTurnGunLeftRadians(Utils.normalRelativeAngle(this.getGunHeadingRadians() - moveDistance));
        this.scan();
    }

    public static Point2D.Double nextPoint(Point2D.Double originPoint, double angle, double distance) {
        return new Point2D.Double(originPoint.x + Math.sin(angle) * distance, originPoint.y + Math.cos(angle) * distance);
    }

    public static double getAngle(Point2D.Double p2, Point2D.Double p1) {
        return Math.atan2(p2.x - p1.x, p2.y - p1.y);
    }

    public static double distanceToWall(Point2D.Double p) {
        return Math.min(Math.min(p.x, 600.0 - p.x), Math.min(p.y, 600.0 - p.y));
    }

    class MicroWave extends Condition {
        Point2D.Double compareValue;
        Point2D.Double startPosition;
        double absBearing;
        double traveledDistance;

        MicroWave() {
        }

        public boolean test() {
            this.traveledDistance += 10.0;
            if (this.traveledDistance > XiaobinRobo3.enemyPosition.distance(this.startPosition) - 18.0) {
                this.startPosition = new Point2D.Double(XiaobinRobo3.getAngle(XiaobinRobo3.enemyPosition, this.startPosition) - this.absBearing, this.traveledDistance);
                XiaobinRobo3.this.removeCustomEvent(this);
            }

            return false;
        }
    }
}
