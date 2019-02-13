package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;
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

    // power and sensitivity for climber
    private double climbPower, climbExponent;

    public RobotFlipper() {
        timer = new Timer();
        // drive power (max 1)
        climbPower = 0.7;
        // deadzone exponent
        climbExponent = 1.1;
        disableScrew = false;
        screwTalon = new WPI_TalonSRX(Constants.SCREW_TALON_CID);

        climbSparkA = new CANSparkMax(Constants.CLIMB_SPARK_CID_A, MotorType.kBrushless);
        climbSparkA.setSmartCurrentLimit(Constants.MAX_CURRENT);
        climbSparkA.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        climbSparkA.setIdleMode(IdleMode.kBrake);

        climbSparkB = new CANSparkMax(Constants.CLIMB_SPARK_CID_B, MotorType.kBrushless);
        climbSparkB.setSmartCurrentLimit(Constants.MAX_CURRENT);
        climbSparkB.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        climbSparkB.setIdleMode(IdleMode.kBrake);
        // follow the other spark, second parameter is to invert
        climbSparkB.follow(climbSparkA, true);
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
        if (Robot.operatorController.getStartButton() && !disableScrew) {
            screwTalon.set(ControlMode.PercentOutput, constant * Constants.SCREW_SPEED);
            timer.start();
            // back button is to push screw out
        } else if (Robot.operatorController.getBackButton()) {
            screwTalon.set(ControlMode.PercentOutput, -constant * Constants.SCREW_SPEED);
            // if its disabled, enable it
            if (disableScrew)
                disableScrew = false;
        } else {
            screwTalon.set(ControlMode.PercentOutput, 0);
        }

        // failsafe to not crush platform. (current coming soon after testing current
        // draw)
        if (timer.get() > 5) {
            timer.stop();
            timer.reset();
            disableScrew = true;
        }
    }

    /**
     * Runs the climber mechanism
     */
    void runClimber() {
        int constant = 1; // for easy direction change
        double stickInput = Robot.climbController.getRawAxis(Constants.CLIMBER_CONTROLLER_AXIS);
        climbPower = SmartDashboard.getNumber("Climb Power", 0.7);
        climbExponent = SmartDashboard.getNumber("Climb Exponent", 1.1);
        // set the motor (which sets the other automatically) to run based on joystick
        climbSparkA.set(constant * climbPower * Math.pow(stickInput, climbExponent));
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
    }
}