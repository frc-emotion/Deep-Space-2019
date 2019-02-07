package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
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
    DoubleSolenoid solR, solL; // hatch pushing solenoids
    double deadzone = 0.1; // joystick trigger threshold for when to activate intake. This is done so intake doesnt activate accidentally.

    /**
     * Constructor for the intake. Inits the sparks and sets a current limit
     *
     */
    public Intake(){
        intakeSparkR = new CANSparkMax(Constants.INTAKE_SPARK_CID_R, MotorType.kBrushless);
        intakeSparkL = new CANSparkMax(Constants.INTAKE_SPARK_CID_L, MotorType.kBrushless);

        intakeSparkR.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        intakeSparkL.setSecondaryCurrentLimit(Constants.MAX_CURRENT);

        solR = new DoubleSolenoid(Constants.INTAKE_SOL_R_FWD, Constants.INTAKE_SOL_R_BWD);
        solL = new DoubleSolenoid(Constants.INTAKE_SOL_L_FWD, Constants.INTAKE_SOL_L_BWD);
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
        if(Robot.operatorController.getTriggerAxis(Hand.kRight) >= deadzone){ // check if trigger is pressed over deadzone
            intakeOut(Robot.operatorController.getTriggerAxis(Hand.kRight));
        }
        else if(Robot.operatorController.getTriggerAxis(Hand.kLeft) >= deadzone){
            intakeIn(Robot.operatorController.getTriggerAxis(Hand.kLeft));
        }
        else{
            stopIntake();
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
        intakeSparkR.set(-speed);
        intakeSparkL.set(speed);

    }

    /**
     * Shoot out cargo by running motors in an outward direction
     * @param speed - how fast to run the motors 0 <= speed <= 1
     * @return void
     */
    public void intakeOut(double speed){
        intakeSparkR.set(speed);
        intakeSparkL.set(-speed);

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

    }

}
