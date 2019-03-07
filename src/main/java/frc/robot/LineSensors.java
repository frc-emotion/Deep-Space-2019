package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LineSensors{
    AnalogInput lLineFollower;
    AnalogInput rLineFollower;
    //AnalogInput mLineFollower;
    public LineSensors(){
        lLineFollower = new AnalogInput(0);
        rLineFollower = new AnalogInput(1);
        //mLineFollower = new AnalogInput(2);

        rLineFollower.setOversampleBits(8);
        lLineFollower.setOversampleBits(8);
        //mLineFollower.setOversampleBits(8);
        
    }
    public void run(){
        SmartDashboard.putNumber("Line Follower R", rLineFollower.getValue());
        SmartDashboard.putNumber("Line Follower L", lLineFollower.getValue());

        //SmartDashboard.putNumber("Line Follower M", lLineFollower.getValue());

    }
    public double getTurnValue(){
        double turnVal = 0.0;
        if(rLineFollower.getValue() < 3000){
            turnVal = -0.55;
        }
        else if(lLineFollower.getValue() < 3000){
            turnVal = 0.55;
        }
        else {
            turnVal = 0.0;
        }
        return turnVal;


    }
}