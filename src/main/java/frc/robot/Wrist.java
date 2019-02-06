/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * Add your docs here.
 */
public class Wrist {
    CANSparkMax wrist;
    public Wrist(){
        wrist= new CANSparkMax(Constants.WRIST_PORT, MotorType.kBrushless);

    }
    public void run(){
        if(Robot.operatorController.getY(Hand.kRight)>0.2){
            System.out.println("Going up");
            wrist.set(0.4);
        }
        else if(Robot.operatorController.getY(Hand.kRight)<-0.2){
            System.out.println("Going down");
            wrist.set(-0.4);
        }
        else{
            wrist.set(0);
        }

    }
}
