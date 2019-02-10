package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Wrist mechanism controller class. 
 * <p>Operation: Right joystick on operator controller.
 * <p>Notes: Wrist is limited to 30A current and is limited to half speed for now. Macro logic is commented out.
 *
 * <p>Bugs: Hopefully none!
 *
 * @author Neal
 */
public class Wrist extends Thread{
    CANSparkMax wristSparkMax; // Spark Max controller from REV Robotics. Controls NEO motor
    CANEncoder wristEncoder; // wrist encoder, built into the NEO
    PIDControl wristPidControl; 
    boolean holdEnabled = false, macroEnabled = false; // checks to see if the wrist should hold its position or perform a macro
    double holdPos = 0; // store which position wrist should hold
    int[] macroCheck = new int[6]; // this list tracks which macro is enabled. A switch statement in the run loop checks which macro is running
    double[] macroPosList = new double[6]; // store which positions wrist needs to go to for macros.
    double startEncoderVal = 0; // stores initial encoder value if wrist encoder doesnt reset.
    double wristSpeedScale = 0.5;

    public Wrist(){
        wristSparkMax = new CANSparkMax(Constants.WRIST_SPARK_CID, MotorType.kBrushless);
        wristSparkMax.setSmartCurrentLimit(Constants.MAX_WRIST_CURRENT);
        wristSparkMax.setSecondaryCurrentLimit(Constants.MAX_WRIST_CURRENT); // set a current limit
        wristSparkMax.setIdleMode(IdleMode.kBrake);

        wristEncoder = wristSparkMax.getEncoder();
        
        wristPidControl = new PIDControl(1f, 0f, 0f); // configure wrist pid tuning values
        wristPidControl.setScale(1.0/200.0); // scale down values 
        wristPidControl.setMaxSpeed(0.5); // set max speed while performing pid

        //load all the macro values
        macroPosList[0] = startEncoderVal; //hatch from ground pos
        macroPosList[1] = startEncoderVal + 10.0; //bottom hatch placement
        macroPosList[2] = startEncoderVal + 20.0; // cargo from the back;
        macroPosList[3] = startEncoderVal + 30.0; // cargo into top rocket




        updateSmartDashboard();
    }

    /**
     * Method that runs in the teleop periodic loop
     * <p> Logic for manual wrist control, holding, and macros
     * @return void
     */
    @Override
    public void run() {
        updateSmartDashboard();
         // if controller is under a threshold and not in hold mode, then the operator has probably let go of the jstick
        // so activate the hold mode
        if(Math.abs(Robot.operatorController.getY(Hand.kRight)) < 0.2 && !holdEnabled){ 
            holdPos = wristEncoder.getPosition(); // get last position of the wrist to hold at
            wristSparkMax.set(0); // send a stop signal
            holdEnabled = true; 
        }
        
        if(Math.abs(Robot.operatorController.getY(Hand.kRight)) > 0.2){ // if joysick axis is above a threshold activate manual mode and diable pid
            //disable anything related to macros of pid hold so that it doesnt interfere with the operator control
            holdEnabled = false;
            macroEnabled = false;
            disableMacros();
            wristPidControl.cleanup();
            manualMove();
          }
          else if(Robot.operatorController.getAButtonPressed()){ // macro pick up hatch from the ground
            toggleMacro(0); 
          }
          else if(Robot.operatorController.getBButtonPressed()){ // Bottom hatch placement
            toggleMacro(1);
          }
          else if(Robot.operatorController.getXButtonPressed()){ // macro for picking up cargo from loading zone
            toggleMacro(2);
          }
          else if(Robot.operatorController.getYButtonPressed()){ // macro for shooting ball in top rocket
            toggleMacro(3);
          }
          else if(Robot.operatorController.getBumperPressed(Hand.kRight)){ // macro to release hatch.

          }
          else{ // if no input is being passed in 
            if(holdEnabled && !macroEnabled){ // if hold mode is activated use pid to go the the last recorded encoder position
              //wristSparkMax.set(wristPidControl.getValue(holdPos, wristEncoder.getPosition())); 
            }
          }
          
          //get which macro is currently enabled
          // int toggled = getToggled();
          // if(toggled != -1 && macroEnabled){
          //     wristSparkMax.set(wristPidControl.getValue(macroPosList[toggled], wristEncoder.getPosition()));
          // }
    }
    /**
     * Move the wrist manully using the operator xbox controller.
     * @return void
     */
    public void manualMove(){
        double inputVal = Robot.operatorController.getY(Hand.kRight);
        // if(Robot.operatorController.getY(Hand.kRight) > 0){ // if the wrist is moving up, scale down inputs 
        //     inputVal *= 0.5f;
        // }

        wristSparkMax.set(inputVal*wristSpeedScale);
        
    }

    /**
     * Reset all the macro values in the macro controller list
     * @return void
     */
    public void disableMacros(){
        for(int i = 0; i < macroCheck.length; i++){ // disable all othermacros
          macroCheck[i] = 0;
        }
      }
    /**
     * Enable a specific macro to run using an index value
     *  
     * @param index - position of macro: 0-floor, 1- , 2-, 3- 4-
     * @return void
     */
      public void toggleMacro(int index){
        disableMacros();
        macroEnabled=true;
        if(macroCheck[index] == 0){ // toggle macros if not active
          macroCheck[index] = 1;
        }
        else{
          macroCheck[index] = 0;
        }
      }

    /**
     * Get which macro is currently enabled for the wrist.
     * @return int - which macro index is selected
     */
      public int getToggled(){
        for(int i = 0; i < macroCheck.length; i++){ // get macro
          if(macroCheck[i] == 1){
            return i;
          }
        }
        return -1;
      }
    /**
     * Publish relevant values to the smartdashboard. Make sure this isnt called at comp.
     * @return void
     */
    public void updateSmartDashboard(){
        SmartDashboard.putNumber("WRIST ENCODER", wristEncoder.getPosition());
        SmartDashboard.putNumber("WRIST HOLD POS", holdPos);
        SmartDashboard.putBoolean("HOLD WRIST?", holdEnabled);
        SmartDashboard.putNumber("WRIST MOTOR TEMP", wristSparkMax.getMotorTemperature());
    }
}