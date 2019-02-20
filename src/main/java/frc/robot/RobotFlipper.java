package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Class that runs the climbing mechanism + screw
 * <p>
 * Operation: op controller and climber joystick
 * <p>
 * Notes: doesnt put arm in rest position yet
 * 
 * @author Gokul Swaminathan
 */
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

    DifferentialDrive climbDrive;

    // power and sensitivity for climber
    private double climbPower, climbExponent;

    public RobotFlipper() {
        timer = new Timer();
        // drive power (max 1)
        climbPower = 0.5;
        // deadzone exponent
        climbExponent = 1.1;
        disableScrew = false;
        screwTalon = new WPI_TalonSRX(Constants.SCREW_TALON_CID);

        climbSparkA = new CANSparkMax(Constants.CLIMB_SPARK_CID_A, MotorType.kBrushless);
        climbSparkA.setSmartCurrentLimit(70);
        climbSparkA.setSecondaryCurrentLimit(70);
        climbSparkA.setIdleMode(IdleMode.kBrake);

        climbSparkB = new CANSparkMax(Constants.CLIMB_SPARK_CID_B, MotorType.kBrushless);
        climbSparkB.setSmartCurrentLimit(70);
        climbSparkB.setSecondaryCurrentLimit(70);
        climbSparkB.setIdleMode(IdleMode.kBrake);
        // follow the other spark, second parameter is to invert

        climbDrive = new DifferentialDrive(climbSparkB, climbSparkA);

        climbEncoder = climbSparkA.getEncoder();
        initShuffleBoard();
    }

    /**
     * Method that will be called in teleop
     */
    public void run() {
        runScrew();
        runClimber();
        workShuffleBoard();
    }

    /**
     * Runs the screw mechanism based on button push
     */
    void runScrew() {
        int constant = 1; // for easy direction change
        // start button is to bring screw in

        if (Robot.operatorController.getStartButton()) {
            screwTalon.set(ControlMode.PercentOutput, constant * Constants.SCREW_SPEED);
            // timer.start();
            // back button is to push screw out
        } else if (Robot.operatorController.getBackButton()) {
            screwTalon.set(ControlMode.PercentOutput, -constant * Constants.SCREW_SPEED);
            // if its disabled, enable it
            // if (disableScrew)
            // disableScrew = false;
        } else {
            screwTalon.set(ControlMode.PercentOutput, 0);
        }

        if (screwTalon.getOutputCurrent() >= 14) {
            disableScrew = true;
        }
    }

    /**
     * Runs the climber mechanism
     */
    void runClimber() {
        int constantA = 1, constantB = -1; // for easy direction change
        double stickInput = Robot.climbController.getRawAxis(Constants.CLIMBER_CONTROLLER_AXIS);
        climbPower = 0.95;
        climbExponent = SmartDashboard.getNumber("Climb Exponent", 1.1);
        // if (stickInput > 0.2) {
        // constant = 1;
        // } else if (stickInput < -0.2) {
        // constant = -1;
        // }

        if (Math.abs(stickInput) > 0.2) {
            // set the motor (which sets the other automatically) to run based on joystick
            // climbSparkA.set(constantA * climbPower * stickInput);
            // climbSparkB.set(constantB * climbPower * stickInput);

            double speed = constantB * climbPower * stickInput;

            climbDrive.tankDrive(speed, speed, true);
        } else {
            climbDrive.tankDrive(0, 0);
        }
    }

    /**
     * Initializes shuffleboard values and drop downs
     */
    void initShuffleBoard() {
        SmartDashboard.putNumber("Climb Power", climbPower);
        SmartDashboard.putNumber("Climb Exponent", climbExponent);
    }

    /**
     * Keeps updating shuffleboard values
     */
    void workShuffleBoard() {
        SmartDashboard.putNumber("Screw Current", screwTalon.getOutputCurrent());
        SmartDashboard.putBoolean("Screw", !disableScrew);
        SmartDashboard.putNumber("Climber Encoder", climbEncoder.getPosition());
        SmartDashboard.putNumber("Climb A Current", climbSparkA.getOutputCurrent());
        SmartDashboard.putNumber("Climb B Current", climbSparkB.getOutputCurrent());
    }
}