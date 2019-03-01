package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Class that operates the Intake
 * <p> Controls: Left trigger takes cargo in, Right Trigger shoots it out
 * <p> Notes: Max speed can be set
 *
 * <p>Bugs: (a list of bugs and other problems)
 *
 * @author Neal
 */

public class Intake extends Thread{

    CANSparkMax intakeSparkR, intakeSparkL; // intake wheels
    DoubleSolenoid hatchSolenoid; // hatch pushing solenoids
    double deadzone = 0.1; // joystick trigger threshold for when to activate intake. This is done so intake doesnt activate accidentally.

    /**
     * Constructor for the intake. Inits the sparks and sets a current limit
     *
     */
    public Intake(){
        intakeSparkR = new CANSparkMax(Constants.INTAKE_SPARK_CID_R, MotorType.kBrushless);
        intakeSparkL = new CANSparkMax(Constants.INTAKE_SPARK_CID_L, MotorType.kBrushless);

        intakeSparkL.setSmartCurrentLimit(50);
        intakeSparkR.setSmartCurrentLimit(50);
        intakeSparkR.setSecondaryCurrentLimit(50);
        intakeSparkL.setSecondaryCurrentLimit(50);
        intakeSparkL.setIdleMode(IdleMode.kCoast);
        intakeSparkR.setIdleMode(IdleMode.kCoast);

        hatchSolenoid = new DoubleSolenoid(0, 1);
        // solL = new DoubleSolenoid(Constants.INTAKE_SOL_L_FWD, Constants.INTAKE_SOL_L_BWD);
        updateSmartDashboard();
    }

    /**
     * Method that runs in the teleop periodic loop
     * <p> Logic for controlling intake
     * @return void
     */
    @Override
    public void run() {
        updateSmartDashboard();
        if(Robot.operatorController.getTriggerAxis(Hand.kLeft) >= deadzone){ // check if trigger is pressed over deadzone
           intakeIn(Robot.operatorController.getTriggerAxis(Hand.kLeft) * 0.5);
        
        }
        else if(Robot.operatorController.getTriggerAxis(Hand.kRight) >= deadzone){
            intakeOut(Robot.operatorController.getTriggerAxis(Hand.kRight));    
        }
        else{
            stopIntake();
        }

        if(Robot.operatorController.getBumperPressed(Hand.kRight)){
            releaseHatch();
        }
        else if(Robot.operatorController.getBumperReleased(Hand.kRight)){
            resetSolenoid();
        }

    }

    /**
     * Publish relevant values to the smartdashboard. Make sure this isnt called at comp.
     * @return void
     */
    public void updateSmartDashboard(){
        SmartDashboard.putNumber("R MOTOR TEMP", intakeSparkR.getMotorTemperature());
        SmartDashboard.putNumber("L MOTOR TEMP", intakeSparkL.getMotorTemperature());

    }

    /**
     * Take in cargo by running motors in an inward direction
     * @param speed - how fast to run the motors 0 <= speed <= 1
     * @return void
     */
    public void intakeIn(double speed){
        intakeSparkR.set(speed);
        intakeSparkL.set(-speed);

    }

    /**
     * Shoot out cargo by running motors in an outward direction
     * @param speed - how fast to run the motors 0 <= speed <= 1
     * @return void
     */
    public void intakeOut(double speed){
        intakeSparkR.set(-speed);
        intakeSparkL.set(speed);

    }

    /**
     * Disables all power to intake spark maxes.
     *  
     * @return void
     */
    public void stopIntake(){
        intakeSparkR.set(0);
        intakeSparkL.set(0);
    }


    /**
     * Releases hatch mechanism by activating pnuematics.
     *  
     * @return void
     */
    public void releaseHatch(){
        hatchSolenoid.set(Value.kReverse);
    }

    /**
     * Releases hatch mechanism by activating pnuematics.
     *  
     * @return void
     */
    public void resetSolenoid(){
        hatchSolenoid.set(Value.kForward);
    }



}
