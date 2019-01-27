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
    private final int MAX_CURRENT = 35; // max current that can be sent to sparks in amps
    private final double GEAR_RATIO = ((double) 50 / 14) * ((double) 54 / 20);

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
            spark.setSmartCurrentLimit(MAX_CURRENT);
            spark.setSecondaryCurrentLimit(MAX_CURRENT);
        }

        rightControllerGroup = new SpeedControllerGroup(rSparkMaxA, rSparkMaxB, rSparkMaxC);
        leftControllerGroup = new SpeedControllerGroup(lSparkMaxA, lSparkMaxB, lSparkMaxC);

        differentialDrive = new DifferentialDrive(leftControllerGroup, rightControllerGroup);

        dashBoardInit();
    }

    public void run() {
        dashBoardUpdate();
    }

    void dashBoardInit() {
        testChoices = new SendableChooser<Integer>();
        testChoices.setDefaultOption("Simple Tank", -1);
        testChoices.addOption("One Revolution", 0);
    }

    void dashBoardUpdate() {
        SmartDashboard.putNumber("Right Encoder", rSparkMaxA.getEncoder().getPosition());
        SmartDashboard.putNumber("Right Velocity", rSparkMaxA.getEncoder().getVelocity());
        SmartDashboard.putNumber("Left Encoder", lSparkMaxA.getEncoder().getPosition());
        SmartDashboard.putNumber("Left Velocity", lSparkMaxA.getEncoder().getVelocity());
    }

    public void testTrain() {

        int choice = testChoices.getSelected();
        switch (choice) {
        case 0:
            double currentValue = rSparkMaxA.getEncoder().getPosition();
            while ((rSparkMaxA.getEncoder().getPosition() - currentValue) <= ((double) 42 * GEAR_RATIO))
                differentialDrive.tankDrive(0.4, 0.4, false);
            differentialDrive.tankDrive(0, 0);
            break;
        default:
            differentialDrive.tankDrive(Robot.driveController.getY(Hand.kLeft),
                    Robot.driveController.getY(Hand.kRight));
            break;
        }

        dashBoardUpdate();
    }

}