package frc.robot;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.DriveTrain;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

/**
 * Class that converts a PathFinder trajectory to robot motion.
 * <p>
 * Operation: Performed autnomously whenever needed. B is to pause, and any
 * stick movement quits it.
 * <p>
 * Notes: A helper class for DriveTrain.
 * 
 * @author Gokul Swaminathan
 */
public class PathConverter {

    // robot's drive train
    private DriveTrain driveTrain;

    // Trajectory from csv
    private Trajectory traj;
    // Modifies center traj for tank setup
    private TankModifier modifier;
    // Wrappers for encoders
    private EncoderFollower lFollower, rFollower;

    private boolean allowDrive;

    // runs a thread with a certain timer interval
    public Notifier notifier;

    public PathConverter(DriveTrain driveTrain, Trajectory traj) {
        allowDrive = false;
        this.driveTrain = driveTrain;
        this.traj = traj;

        // modify to our robot tank width
        modifier = new TankModifier(traj).modify(Constants.WHEELBASE_WIDTH);

        lFollower = new EncoderFollower();
        rFollower = new EncoderFollower();
    }

    /**
     * Sets up followers with default encoder and wheel info
     */
    public void setUpFollowers() {
        setUpFollowers(Constants.ENCODER_REV, Constants.WHEEL_DIAMETER);
    }

    /**
     * Sets up encoder followers
     * 
     * @param ticksInRev encoder ticks in a revolution
     * @param wheelDiam  diameter of the wheel in meters
     */
    public void setUpFollowers(int ticksInRev, double wheelDiam) {
        // pid values
        double p = 1.75, i = 0.6, d = 0;

        // give the followers the trajectories
        lFollower.setTrajectory(modifier.getLeftTrajectory());
        rFollower.setTrajectory(modifier.getRightTrajectory());

        // give the wrappers our encoders, ticks, and wheel diam
        lFollower.configureEncoder((int) driveTrain.getDriveEncoder('l').getPosition(), ticksInRev, wheelDiam);
        rFollower.configureEncoder((int) driveTrain.getDriveEncoder('r').getPosition(), ticksInRev, wheelDiam);

        // configure pidva
        lFollower.configurePIDVA(p, i, d, 1 / Constants.MAX_VELOCITY, 0);
        rFollower.configurePIDVA(p, i, d, 1 / Constants.MAX_VELOCITY, 0);
    }

    /**
     * Runs the robot to follow the path
     */
    public void followPath() {
        allowDrive = false;
        // :: is lambda shortcut
        // pass is method to be used in thread
        notifier = new Notifier(this::followPathHelper);
        // give it the time interval, which is always 0.02
        notifier.startPeriodic(traj.get(0).dt);
    }

    /**
     * Helper method that is run through the notifier thread
     */
    private void followPathHelper() {
        // stop notifier when it is finished, or when movement detected from controllers
        if ((lFollower.isFinished() || rFollower.isFinished()) || checkControllers()) {
            Robot.driveController.setRumble(RumbleType.kLeftRumble, 0.5);
            Robot.driveController.setRumble(RumbleType.kRightRumble, 0.5);
            SmartDashboard.putString("Pathfinder Job", "Finished");
            allowDrive = true;
            notifier.stop();
        } else {
            // pause the pathfinder
            if (Robot.driveController.getBButtonPressed()) {
                long prevTime = System.currentTimeMillis();
                while ((System.currentTimeMillis() - prevTime) <= 4000) {
                    if (Robot.driveController.getBButtonPressed())
                        break;
                }
            }

            double l = 0, r = 0, gyro_heading = 0, desired_heading = 0, angleDifference = 0, turn = 0;
            double kG = 0;
            int constant = 1;

            // calculate the speed for the motors at this moment
            l = constant * lFollower.calculate((int) driveTrain.getDriveEncoder('l').getPosition());
            r = constant * rFollower.calculate((int) driveTrain.getDriveEncoder('r').getPosition());

            // calculate angle heading
            gyro_heading = Robot.gyro.getAngle();
            desired_heading = Pathfinder.r2d(lFollower.getHeading());
            angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyro_heading);
            kG = 5 * (-1.0 / 80.0);
            turn = kG * angleDifference;

            // run values through tankdrive
            driveTrain.getDrive().tankDrive((l - turn), (r + turn));
        }

    }

    /**
     * Checks if the controller sticks have been moved
     * 
     * @return true if the controller has been touched, false if not
     */
    private boolean checkControllers() {
        return (Robot.driveController.getY(Hand.kLeft) > 0.3) || (Robot.driveController.getY(Hand.kLeft) < -0.3)
                || (Robot.driveController.getY(Hand.kRight) > 0.3) || (Robot.driveController.getY(Hand.kRight) < -0.3);
    }

    /**
     * @return the traj
     */
    public Trajectory getTrajectory() {
        return traj;
    }

    /**
     * @return the lFollower
     */
    public EncoderFollower getLeftFollower() {
        return lFollower;
    }

    /**
     * @return the rFollower
     */
    public EncoderFollower getRightFollower() {
        return rFollower;
    }

    /**
     * Method that informs other classes if notifier is done or not
     * 
     * @return true if notifier is finished, false if not
     */
    public boolean isDriveAllowed() {
        return allowDrive;
    }
}