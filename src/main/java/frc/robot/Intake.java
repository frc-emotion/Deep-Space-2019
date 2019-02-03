package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake extends Thread{

    CANSparkMax intakeSparkR, intakeSparkL;
    double deadzone = 0.1;
    public Intake(){
        intakeSparkR = new CANSparkMax(Constants.INTAKE_SPARK_CID_R, MotorType.kBrushless);
        intakeSparkL = new CANSparkMax(Constants.INTAKE_SPARK_CID_L, MotorType.kBrushless);

        intakeSparkR.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        intakeSparkL.setSecondaryCurrentLimit(Constants.MAX_CURRENT);
        updateSmartDashboard();
    }

    @Override
    public void run() {
        updateSmartDashboard();
        if(Robot.operatorController.getTriggerAxis(Hand.kRight) >= deadzone){
            intakeOut(Robot.operatorController.getTriggerAxis(Hand.kRight));
        }
        else if(Robot.operatorController.getTriggerAxis(Hand.kLeft) >= deadzone){
            intakeIn(Robot.operatorController.getTriggerAxis(Hand.kLeft));
        }

    }
    public void updateSmartDashboard(){
        SmartDashboard.putNumber("R MOTOR TEMP", intakeSparkR.getMotorTemperature());
        SmartDashboard.putNumber("L MOTOR TEMP", intakeSparkL.getMotorTemperature());

    }

    public void intakeIn(double speed){
        intakeSparkR.set(-speed);
        intakeSparkL.set(speed);

    }
    public void intakeOut(double speed){
        intakeSparkR.set(speed);
        intakeSparkL.set(-speed);

    }



}
