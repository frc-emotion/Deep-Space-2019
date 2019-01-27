package frc.robot;

import java.util.ArrayList;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain{
    private CANSparkMax rSparkMaxA, rSparkMaxB, rSparkMaxC, 
                lSparkMaxA, lSparkMaxB, lSparkMaxC; 
    private ArrayList<CANSparkMax> driveSparkMaxes;
    private final int MAX_CURRENT = 35; // max current that can be sent to sparks in amps
    private DifferentialDrive differentialDrive;
    private SpeedControllerGroup leftControllerGroup, rightControllerGroup;
    public DriveTrain(){
        //initialize the 40 billion spark maxes we have
        rSparkMaxA = new CANSparkMax(Constants.DT_CAN_RA_PORT, MotorType.kBrushless);
        rSparkMaxB = new CANSparkMax(Constants.DT_CAN_RB_PORT, MotorType.kBrushless);
        rSparkMaxC = new CANSparkMax(Constants.DT_CAN_RC_PORT, MotorType.kBrushless);

        lSparkMaxA = new CANSparkMax(Constants.DT_CAN_LA_PORT, MotorType.kBrushless);
        lSparkMaxB = new CANSparkMax(Constants.DT_CAN_LB_PORT, MotorType.kBrushless);
        lSparkMaxB = new CANSparkMax(Constants.DT_CAN_LC_PORT, MotorType.kBrushless);


        //create array of motor controllers to make current limiting easier
        driveSparkMaxes = new ArrayList<>(){{
            add(rSparkMaxA);
            add(rSparkMaxB);
            add(rSparkMaxC);

            add(lSparkMaxA);
            add(lSparkMaxB);
            add(lSparkMaxC);

        }};

        for(CANSparkMax spark : driveSparkMaxes){
            spark.setSmartCurrentLimit(MAX_CURRENT);
            spark.setSecondaryCurrentLimit(MAX_CURRENT);
        }
        rightControllerGroup = new SpeedControllerGroup(rSparkMaxA, rSparkMaxB, rSparkMaxC);
        leftControllerGroup = new SpeedControllerGroup(lSparkMaxA, lSparkMaxB, lSparkMaxC);

        differentialDrive = new DifferentialDrive(leftControllerGroup, rightControllerGroup);



    }
    public void run(){

    }
    


}