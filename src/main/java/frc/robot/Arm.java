package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Arm extends Thread{
    CANSparkMax armSparkMax;
    CANEncoder armEncoder;
    PIDControl armPidControl;
    boolean holdEnabled = false, macroEnabled = false;
    double holdPos = 0;
    int[] macroCheck = new int[6]; // macro toggle checker 

    public Arm(){
        armSparkMax = new CANSparkMax(Constants.ARM_SPARK_CID, MotorType.kBrushless);
        armSparkMax.setSecondaryCurrentLimit(Constants.MAX_CURRENT);

        armEncoder = new CANEncoder(armSparkMax);

        armPidControl = new PIDControl(0f, 0f, 0f);
        armPidControl.setScale(1.0/200.0);
        armPidControl.setMaxSpeed(0.5);



    }

    @Override
    public void run() {
        if(Math.abs(Robot.operatorController.getY(Hand.kLeft)) < 0.2 && !holdEnabled){
            holdPos = armEncoder.getPosition();
            armSparkMax.set(0);
            holdEnabled = true;
        }
        
        if(Math.abs(Robot.operatorController.getY(Hand.kLeft)) > 0.2){ // if joysick axis is above a threshold activate manual mode and diable pid
            holdEnabled = false;
            macroEnabled = false;
            disableMacros();
            armPidControl.cleanup();
       
            manualMove();
          }
        //   else if(Robot.operatorController.getAButtonPressed()){ // macro to go to lvl1
        //     toggleMacro(0); // toggle floor macro
       
            
        //   }
        //   else if(Robot.operatorController.getXButtonPressed()){
        //     toggleMacro(1);
        //   }
          else{ // if no input is being passed in 
            if(holdEnabled && !macroEnabled){ // if hold mode is activated use pid to go the the last recorded encoder position
              armSparkMax.set(armPidControl.getValue(holdPos, armEncoder.getPosition()));
            }
          }
          
       
          // cleanup for lvl macros.
        //   if(Robot.operatorController.getBButtonReleased()){
        //     armPidControl.cleanup();
        //   }
        //   if(Robot.operatorController.getXButtonReleased()){
        //     armPidControl.cleanup();
        //  }
       
       
        //   int toggled = getToggled();
        //   switch(toggled){
       
        //    case 0: // bottom
        //    armSparkMax.set(armPidControl.getValue(0, armEncoder.getPosition()));
        //      break;
        //    case 1:
        //    armSparkMax.set(armPidControl.getValue(0, armEncoder.getPosition()));
        //      break;
        //    case 2:
        //    armSparkMax.set(armPidControl.getValue(0, armEncoder.getPosition()));
        //      break;
        //   }

    }

    public void manualMove(){
        double inputVal = Robot.operatorController.getY(Hand.kLeft);
        if(Robot.operatorController.getY(Hand.kLeft) > 0){
            inputVal *= 0.5f;
        }

        armSparkMax.set(inputVal);
        
    }
    public void disableMacros(){
        for(int i = 0; i < macroCheck.length; i++){ // disable all othermacros
          macroCheck[i] = 0;
        }
      }
    
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
    
      public int getToggled(){
        for(int i = 0; i < macroCheck.length; i++){ // get macro
          if(macroCheck[i] == 1){
            return i;
          }
        }
        return -1;
      }
    public void setupSmartDashboard(){
        SmartDashboard.putNumber("ARM ENCODER", armEncoder.getPosition());
        SmartDashboard.putNumber("ARM HOLD POS", holdPos);
        SmartDashboard.putBoolean("HOLD?", holdEnabled);

    }
}