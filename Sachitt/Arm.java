package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;


import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm {
    CANSparkMax arm;
    private double armSpeed = 0.5;
    public Arm() {
        //init arm
        arm = new CANSparkMax(Constants.ARM_PORT, MotorType.kBrushless);
    
    }

    public void run() {
        if(Robot.operatorController.getY(Hand.kLeft) >= 0.1 || Robot.operatorController.getY(Hand.kLeft) <= -0.1) {
            //dampen speed by dividing two
            arm.set(Robot.operatorController.getY(Hand.kLeft) * armSpeed);
        } else {
            //reset speed to 0 when controller is not being used
            arm.set(0);
        }

        //SmartDashboard.putNumber("Arm", armEncoder.get());
    }

}