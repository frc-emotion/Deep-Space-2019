package frc.robot;

import java.util.ArrayList;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {

    private CANSparkMax rSparkMaxA, rSparkMaxB, rSparkMaxC, lSparkMaxA, lSparkMaxB, lSparkMaxC;
    private ArrayList<CANSparkMax> driveSparkMaxes;

    private DifferentialDrive differentialDrive;
    private SpeedControllerGroup leftControllerGroup, rightControllerGroup;

    SendableChooser<Integer> testChoices;

    public DriveTrain() {
        // initialize the 40 billion spark maxes we have
        rSparkMaxA = new CANSparkMax(Constants.DT_CAN_RA_PORT, MotorType.kBrushless);
        rSparkMaxB = new CANSparkMax(Constants.DT_CAN_RB_PORT, MotorType.kBrushless);
        rSparkMaxC = new CANSparkMax(Constants.DT_CAN_RC_PORT, MotorType.kBrushless);

        lSparkMaxA = new CANSparkMax(Constants.DT_CAN_LA_PORT, MotorType.kBrushless);
        lSparkMaxB = new CANSparkMax(Constants.DT_CAN_LB_PORT, MotorType.kBrushless);
        lSparkMaxB = new CANSparkMax(Constants.DT_CAN_LC_PORT, MotorType.kBrushless);

        // create array of motor controllers to make current limiting easier
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

        for (CANSparkMax spark : driveSparkMaxes) {
            spark.setSmartCurrentLimit(Constants.MAX_CURRENT);
            spark.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        }

        rightControllerGroup = new SpeedControllerGroup(rSparkMaxA, rSparkMaxB, rSparkMaxC);
        leftControllerGroup = new SpeedControllerGroup(lSparkMaxA, lSparkMaxB, lSparkMaxC);

        differentialDrive = new DifferentialDrive(leftControllerGroup, rightControllerGroup);

        initShuffleBoard();
    }

    public void run() {
        updateShuffleBoard();
    }

    void initShuffleBoard() {
        testChoices = new SendableChooser<Integer>();
        testChoices.setDefaultOption("Tank Drive", 0);
        testChoices.addOption("One Revolution", 1);

        SmartDashboard.putData(testChoices);
    }

    void updateShuffleBoard() {
        SmartDashboard.putNumber("Right Encoder", rSparkMaxA.getEncoder().getPosition() * Constants.GEAR_RATIO);
        SmartDashboard.putNumber("Right Velocity", rSparkMaxA.getEncoder().getVelocity() * Constants.GEAR_RATIO);
        SmartDashboard.putNumber("Left Encoder", lSparkMaxA.getEncoder().getPosition() * Constants.GEAR_RATIO);
        SmartDashboard.putNumber("Left Velocity", lSparkMaxA.getEncoder().getVelocity() * Constants.GEAR_RATIO);
    }

    public void testTrain() {

        int choice = testChoices.getSelected();
        switch (choice) {
        case 1:
            double currentValue = rSparkMaxA.getEncoder().getPosition(); // get current spark max value
            while ((rSparkMaxA.getEncoder().getPosition() - currentValue) <= ((double) Constants.ENCODER_REV * Constants.GEAR_RATIO)) {
                differentialDrive.tankDrive(0.4, 0.4, false);
            }
            differentialDrive.tankDrive(0, 0);
            break;
        default:
            differentialDrive.tankDrive(Robot.driveController.getY(Hand.kLeft),
                    Robot.driveController.getY(Hand.kRight));
            break;

        }

    }

}