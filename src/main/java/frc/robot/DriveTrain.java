package frc.robot;

import java.util.ArrayList;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



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

    private double drivePower, driveExponent;

    // pid controls
    private PIDControl lemonPidControl;
    private PIDControl gyroPidControl;

    private double holdHeading = 0.0;

    private double constant = 1;
    public DriveTrain() {
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
        lSparkMaxC = new CANSparkMax(Constants.DT_CAN_LC_PORT, MotorType.kBrushless);

        // grab encoders from sparkmax
        lEncoder = lSparkMaxA.getEncoder();
        rEncoder = rSparkMaxA.getEncoder();

        // create arraylist of motor controllers to make current limiting easier
        driveSparkMaxes = new ArrayList<CANSparkMax>() {
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
            spark.setSmartCurrentLimit(Constants.MAX_DT_CURRENT);
            spark.setSecondaryCurrentLimit(Constants.MAX_DT_CURRENT);
            spark.setIdleMode(IdleMode.kBrake);
        }

        rSpeedGroup = new SpeedControllerGroup(rSparkMaxA, rSparkMaxB, rSparkMaxC);
        lSpeedGroup = new SpeedControllerGroup(lSparkMaxA, lSparkMaxB, lSparkMaxC);
        drive = new DifferentialDrive(lSpeedGroup, rSpeedGroup);

        // add pid controllers from sensors
        lemonPidControl = new PIDControl(0.022f, 0.0004f, 0);
        lemonPidControl.setMaxSpeed(0.4);
        // lemonPidControl.setScale(.01);

        gyroPidControl = new PIDControl(0.4f, 0f, 0f);
        gyroPidControl.setMaxSpeed(0.5);
        // initShuffleBoard();
    }



    boolean kidsDisabled = false;
    double childScale  = 0.6, childExp = 2;


    public void runKidsMode(){

        SmartDashboard.putBoolean("Kids Mode", !kidsDisabled);

        if (!kidsDisabled)
            runForzaDrive();
        else
            runTankDrive();

        if (Robot.driveController.getBumper(Hand.kRight))
            kidsDisabled = true;
        else
            kidsDisabled = false;

    }

    private void runForzaDrive() {
        int constX = -1, constY = 1;

        double xAxis = Robot.operatorController.getX(Hand.kRight);
        double yAxis = Robot.operatorController.getY(Hand.kLeft);

        if (yAxis < 0) {
            constY *= 1;
        } else if (yAxis > 0) {
            constY *= -1;
        }

        if (xAxis < 0) {
            constX *= 1;
        } else if (xAxis > 0) {
            constX *= -1;
        }

        childScale = SmartDashboard.getNumber("Child Scale", 0.6);
        childExp = SmartDashboard.getNumber("Child Exponent", 2);

        double driveY = constY * childScale * Math.pow(Math.abs(yAxis), childExp);
        double driveX = constX * childScale * Math.pow(Math.abs(xAxis), childExp);

        drive.arcadeDrive(driveY, driveX);
    }


    /**
     * Method that will be called in teleop
     */
    public void run() {
        SmartDashboard.putNumber("Constnat", 0.5);
        // workShuffleBoard();

        // drive overrides
        if (Robot.driveController.getAButton()) {
            // Robot.lemonTorch.update();
            // System.out.println(Robot.lemonTorch.getErrorXBall());
            Robot.lemonTorch.switchPipelines(0);
            drive.arcadeDrive(0.5, lemonPidControl.getValue(0, -Robot.lemonTorch.getErrorX()));
        } else if (Robot.driveController.getXButton()) {
            
            if(DriverStation.getInstance().isAutonomous()){
                constant = SmartDashboard.getNumber("Constnat", 0.5);
            }
            else{
                constant = 1;
            }
            drive.arcadeDrive(-Robot.driveController.getY(Hand.kLeft)*constant,
                    gyroPidControl.getValue(holdHeading, Robot.gyro.getAngle()));
        } //else if (Robot.driveController.getStickButton(Hand.kRight)) {
        //     //Robot.lemonTorch.switchPipelines(1);
        //     double forwardVal = -Robot.driveController.getY(Hand.kLeft);
        //     if(forwardVal > 0){
        //         drive.arcadeDrive(0.5*forwardVal, Robot.lineSensors.getTurnValue(true));
        //     }
        //     else{
        //         drive.arcadeDrive(0.5*forwardVal, Robot.lineSensors.getTurnValue(true));
        //     }
        // } 
        else {
            runTankDrive();
            //drive.arcadeDrive(-Robot.driveController.getY(Hand.kLeft), Robot.driveController.getX(Hand.kRight), true);
        }

        if (Robot.driveController.getXButtonPressed()) {
            holdHeading = Robot.gyro.getAngle();
        }
        if (Robot.driveController.getAButtonReleased()) {
            lemonPidControl.cleanup();
        }
        if (Robot.driveController.getBButtonReleased()) {
            lemonPidControl.cleanup();
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
        int constR = 1, constL = 1;

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

        if (Robot.driveController.getBumper(Hand.kLeft))
            drivePower = Constants.SLOW_SPEED;
        //else if (Robot.driveController.getBumper(Hand.kRight))
        //    drivePower = Constants.TURBO_SPEED;
        else
            drivePower = Constants.REGULAR_SPEED;

        driveExponent = 1.8;//SmartDashboard.getNumber("Drive Exponent", 1.5);

        // curves
        double xL = Math.abs(lAxis);
        double xR = Math.abs(rAxis);
        double driveL = constL * drivePower * Math.pow(xL, driveExponent);// ( (1.35/3.0) * Math.tanh(1.38*3*xL-1.7) + (1.4/3.0) );
        double driveR = constR * drivePower * Math.pow(xR, driveExponent);//( (1.35/3.0) * Math.tanh(1.38*3*xR-1.7) + (1.4/3.0) );

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
        SmartDashboard.putNumber("Drive Power", 0.7);
        SmartDashboard.putNumber("Drive Exponent", 1.5);
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