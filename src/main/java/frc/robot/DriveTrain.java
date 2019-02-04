package frc.robot;

import java.lang.Thread;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * <h2>DriveTrain:</h2>
 * <p>
 * Handles motors used in the drivetrain.
 * </p>
 * 
 * <p>
 * Bugs:
 * </p>
 * 
 * @author Ryan Chaiyakul
 */
class DriveTrain extends Thread {

    // init empty objects to be used outside of the constructor
    private DifferentialDrive drive; // DifferentialDrive object for tankdrive function
    private CANSparkMax dtrain_1, dtrain_2, dtrain_3, dtrain_4, dtrain_5, dtrain_6; // SparkMax motors (1-3, 4-6) (L, R)
    private SpeedControllerGroup dtrainL, dtrainR; // SpeedControllerGroups for Left and Right side of DifferentialDrive

    /**
     * <h2>Constructor:</h2>
     * <p>
     * Main constructor for DriveTrain class. Initializes all motors and creates a
     * <b>DifferentialDrive object</b> for TankDrive.
     * </p>
     * 
     * <p>
     * Bugs:
     * </p>
     */
    DriveTrain() {

        // init SparkMax
        dtrain_1 = new CANSparkMax(Constants.DTRAIN_PORT_1, MotorType.kBrushless);
        dtrain_2 = new CANSparkMax(Constants.DTRAIN_PORT_2, MotorType.kBrushless);
        dtrain_3 = new CANSparkMax(Constants.DTRAIN_PORT_3, MotorType.kBrushless);
        dtrain_4 = new CANSparkMax(Constants.DTRAIN_PORT_4, MotorType.kBrushless);
        dtrain_5 = new CANSparkMax(Constants.DTRAIN_PORT_5, MotorType.kBrushless);
        dtrain_6 = new CANSparkMax(Constants.DTRAIN_PORT_6, MotorType.kBrushless);

        // add secondary current to SparkMax
        dtrain_1.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        dtrain_2.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        dtrain_3.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        dtrain_4.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        dtrain_5.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        dtrain_6.setSecondaryCurrentLimit(Constants.MAX_CURRENT);

        // init SpeedControllerGroups
        dtrainL = new SpeedControllerGroup(dtrain_1, dtrain_2, dtrain_3);
        dtrainR = new SpeedControllerGroup(dtrain_4, dtrain_5, dtrain_6);

        // init DifferentialDrive
        drive = new DifferentialDrive(dtrainL, dtrainR);
    }

    /**
     * <h2>Override Run:</h2>
     * <p>
     * Uses run method from Thread. Overrides so it runs driveTrankDrive()
     * </p>
     * {@inheritDoc}
     */
    @Override
    public void run() {
        driveTankDrive(Robot.driveController.getY(Hand.kLeft), Robot.driveController.getY(Hand.kRight));
    }

    /**
     * <h2>driveTankDrive:</h2>
     * <p>
     * takes two double inputs(normally from driveController) to control the motors.
     * </p>
     * 
     * @param
     */
    private void driveTankDrive(double leftAxis, double rightAxis) {
        int constR = 1, constL = 1;

        if (rightAxis < 0) {
            constR *= 1;
        } else if (rightAxis > 0) {
            constR *= -1;
        }

        if (leftAxis < 0) {
            constL *= 1;
        } else if (leftAxis > 0) {
            constL *= -1;
        }
        drive.tankDrive(constL * Constants.POWER * Math.pow(Math.abs(leftAxis), Constants.CURVE),
                constR * Constants.POWER * Math.pow(Math.abs(rightAxis), Constants.CURVE));
    }

}