package frc.robot;

import java.io.File;
import java.util.ArrayList;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

/**
 * Class that runs the Drive Train
 * <p>
 * Operation: Both joysticks on the driver contorller.
 * <p>
 * Notes: Current limit is 35 amps for drive neos.
 * 
 * @author Gokul Swaminathan
 */
public class DriveTrain {

    // All the drivetrain spark maxes
    private CANSparkMax lSparkMaxA, lSparkMaxB, lSparkMaxC, rSparkMaxA, rSparkMaxB, rSparkMaxC;
    private CANEncoder lEncoder, rEncoder;
    // An arraylist to hold the spark maxes
    private ArrayList<CANSparkMax> driveSparkMaxes;

    // the differential that holds the drive methods
    private DifferentialDrive drive;
    // group the sparks
    private SpeedControllerGroup lSpeedGroup, rSpeedGroup;

    // choices for dropdown menu on shuffleboard
    private SendableChooser<Integer> driveChoices, pathChoices;

    private double drivePower, driveExponent;

    // pathfinder helper object
    private PathConverter pathConverter;
    private boolean pathDone;

    public DriveTrain() {
        pathDone = false;
        // drive power (max 1)
        drivePower = 0.7;
        // deadzone exponent
        driveExponent = 1.5;

        // init all the spark maxes for drivetrain with brushless settings (Neos are
        // brushless)
        rSparkMaxA = new CANSparkMax(Constants.DT_CAN_RA_PORT, MotorType.kBrushless);
        rSparkMaxB = new CANSparkMax(Constants.DT_CAN_RB_PORT, MotorType.kBrushless);
        rSparkMaxC = new CANSparkMax(Constants.DT_CAN_RC_PORT, MotorType.kBrushless);
        lSparkMaxA = new CANSparkMax(Constants.DT_CAN_LA_PORT, MotorType.kBrushless);
        lSparkMaxB = new CANSparkMax(Constants.DT_CAN_LB_PORT, MotorType.kBrushless);
        lSparkMaxB = new CANSparkMax(Constants.DT_CAN_LC_PORT, MotorType.kBrushless);

        // grab encoders from sparkmax
        lEncoder = lSparkMaxA.getEncoder();
        rEncoder = rSparkMaxA.getEncoder();

        // create arraylist of motor controllers to make current limiting easier
        driveSparkMaxes = new ArrayList<>() {
            {
                add(rSparkMaxA);
                add(rSparkMaxB);
                add(rSparkMaxC);
                add(lSparkMaxA);
                add(lSparkMaxB);
                add(lSparkMaxC);
            }
        };

        // set spark currents
        for (CANSparkMax spark : driveSparkMaxes) {
            // Has shown to produce some bugs, we do not know yet
            spark.setSmartCurrentLimit(Constants.MAX_CURRENT);
            spark.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
            spark.setIdleMode(IdleMode.kBrake);
        }

        rSpeedGroup = new SpeedControllerGroup(rSparkMaxA, rSparkMaxB, rSparkMaxC);
        lSpeedGroup = new SpeedControllerGroup(lSparkMaxA, lSparkMaxB, lSparkMaxC);
        drive = new DifferentialDrive(lSpeedGroup, rSpeedGroup);

        initShuffleBoard();
    }

    /**
     * Method that will be called in teleop
     */
    public void run() {
        workShuffleBoard();

        int driveChoice = driveChoices.getSelected();
        switch (driveChoice) {
        case 0:
            runPathFinderChoices();
            break;
        default:
            runTankDrive();
            break;
        }
    }

    /**
     * Contains the logic that it will only do pathfinder once, and will only go to
     * manual drive after pathfinder.
     */
    private void runPathFinderChoices() {
        if (!pathDone) {
            runPathFinder();
            pathDone = true;
        }
        if (pathConverter.isDriveAllowed())
            runTankDrive();
    }

    /**
     * Imports csv and converts it to trajectory, then passing in the trajectory to
     * the PathConverter object.
     */
    public void runPathFinder() {
        int pathChoice = pathChoices.getSelected().intValue();
        String pathName = "";

        switch (pathChoice) {
        case 0:
            pathName = "righthab";
            break;
        case 1:
            pathName = "straighthab";
            break;
        default:
            // do nothing
            break;
        }

        if (!pathName.equals("")) {
            String dir = Filesystem.getDeployDirectory().toString();
            String fileName = pathName + ".pf1.csv";

            File trajFile = new File(dir + "/" + fileName);

            Trajectory traj = Pathfinder.readFromCSV(trajFile);

            pathConverter = new PathConverter(this, traj);
            pathConverter.setUpFollowers();

            pathConverter.followPath();
        }
    }

    /**
     * Runs arcade drive on the left xbox stick. Also has square inputs so its more
     * sensitive at slower speeds.
     */
    void runArcadeDrive() {
        // arcade drive (one stick) with square inputs
        drive.arcadeDrive(Robot.driveController.getY(Hand.kLeft), Robot.driveController.getX(Hand.kLeft), true);
    }

    /**
     * Runs tank drive. We make sure that the stick goes below or above zero. The
     * deadzone curve is a simple exponential curve.
     */
    void runTankDrive() {
        // constants to easily configure if drive is opposite
        int constR = -1, constL = -1;

        double rAxis = Robot.driveController.getY(Hand.kRight);
        double lAxis = Robot.driveController.getY(Hand.kLeft);

        if (rAxis < 0) {
            constR *= 1;
        } else if (rAxis > 0) {
            constR *= -1;
        }

        if (lAxis < 0) {
            constL *= 1;
        } else if (lAxis > 0) {
            constL *= -1;
        }

        drivePower = SmartDashboard.getNumber("Drive Power", 0.7);
        driveExponent = SmartDashboard.getNumber("Drive Exponent", 1.5);

        // curves
        double driveL = constL * drivePower * Math.pow(Math.abs(lAxis), driveExponent);
        double driveR = constR * drivePower * Math.pow(Math.abs(rAxis), driveExponent);

        drive.tankDrive(driveL, driveR);
    }

    /**
     * Method to return the encoder object of SparkMax
     * 
     * @param side the side of the drive train
     * @return the encoder of that side
     */
    public CANEncoder getDriveEncoder(char side) {
        if (side == 'r')
            return rEncoder;
        else if (side == 'l')
            return lEncoder;
        else
            return null;
    }

    /**
     * Initializes shuffleboard values and drop downs
     */
    private void initShuffleBoard() {
        SmartDashboard.putNumber("Drive Power", drivePower);
        SmartDashboard.putNumber("Drive Exponent", driveExponent);

        SmartDashboard.putString("Pathfinder Job", "NotFinished");

        driveChoices = new SendableChooser<Integer>();
        driveChoices.setDefaultOption("Tank Drive", -1);
        driveChoices.addOption("PathFinder + Tank Drive", 0);
        SmartDashboard.putData("Drive Choices", driveChoices);

        pathChoices = new SendableChooser<Integer>();
        pathChoices.setDefaultOption("No Path", -1);
        // this is just a place holder till we get the real paths
        pathChoices.addOption("Sample Right Hab", 0);
        pathChoices.addOption("Sample Straight Hab", 1);
        SmartDashboard.putData("PathFinder Choices", pathChoices);
    }

    /**
     * Keeps updating shuffleboard values
     */
    private void workShuffleBoard() {
        SmartDashboard.putNumber("Right Drive Encoder Position", rEncoder.getPosition());
        SmartDashboard.putNumber("Right Drive Encoder Velocity (rpm)", rEncoder.getVelocity());

        SmartDashboard.putNumber("Left Drive Encoder Position", lEncoder.getPosition());
        SmartDashboard.putNumber("Left Drive Encoder Velocity (rpm)", lEncoder.getVelocity());

        // list motor temperatures and update them
        for (CANSparkMax spark : driveSparkMaxes) {
            String label = "Spark Max" + spark.getDeviceId() + "Temp. (C)";
            SmartDashboard.putNumber(label, spark.getMotorTemperature());
        }
    }

    /**
     * @return the robot's differential drive
     */
    public DifferentialDrive getDrive() {
        return drive;
    }

}