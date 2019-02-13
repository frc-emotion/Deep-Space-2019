package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotFlipper {

    // Motor controller for screw (Talon SRX)
    WPI_TalonSRX screwTalon;
    // Motor controllers for climbers (SparkMax)
    CANSparkMax climbSparkA, climbSparkB;
    // Neo Encoder
    CANEncoder climbEncoder;

    // Failsafe for screw
    Timer timer;

    // Boolean for disabling screw
    boolean disableScrew;

    // power and sensitivity for climber
    private double climbPower, climbExponent;

    public RobotFlipper() {
        timer = new Timer();
        // drive power (max 1)
        climbPower = 0.7;
        // deadzone exponent
        climbExponent = 1.5;
        disableScrew = false;
        screwTalon = new WPI_TalonSRX(Constants.SCREW_TALON_CID);
        climbSparkA = new CANSparkMax(Constants.CLIMB_SPARK_CID_A, MotorType.kBrushless);
        climbSparkB = new CANSparkMax(Constants.CLIMB_SPARK_CID_B, MotorType.kBrushless);
        // follow the other spark, second parameter is to invert
        climbSparkB.follow(climbSparkA, true);
        climbEncoder = climbSparkA.getEncoder();
    }

    public void run() {
        runScrew();
        runClimber();
        workShuffleBoard();
    }

    void runScrew() {
        int constant = 1;
        if (Robot.operatorController.getStartButton() && !disableScrew) {
            screwTalon.set(ControlMode.PercentOutput, constant * Constants.SCREW_SPEED);
            timer.start();
        } else if (Robot.operatorController.getBackButton()) {
            screwTalon.set(ControlMode.PercentOutput, -constant * Constants.SCREW_SPEED);
            if (disableScrew)
                disableScrew = false;
        } else {
            screwTalon.set(ControlMode.PercentOutput, 0);
        }

        if (timer.get() > 5) {
            timer.stop();
            timer.reset();
            disableScrew = true;
        }
    }

    void runClimber() {
        int constant = 1;
        double stickInput = Robot.climbController.getRawAxis(Constants.CLIMBER_CONTROLLER_AXIS);
        climbPower = SmartDashboard.getNumber("Climb Power", 0.7);
        climbExponent = SmartDashboard.getNumber("Climb Exponent", 1.5);
        climbSparkA.set(constant * climbPower * Math.pow(stickInput, climbExponent));
    }

    void initShuffleBoard() {
        SmartDashboard.putNumber("Climb Power", climbPower);
        SmartDashboard.putNumber("Climb Exponent", climbExponent);
    }

    void workShuffleBoard() {
        SmartDashboard.putNumber("Screw Current", screwTalon.getOutputCurrent());
        SmartDashboard.putBoolean("Screw", !disableScrew);
        SmartDashboard.putNumber("Climber Encoder", climbEncoder.getPosition());
    }
}