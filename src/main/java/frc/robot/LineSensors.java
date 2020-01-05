package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LineSensors{
    AnalogInput lLineFollower;
    AnalogInput rLineFollower;
    //AnalogInput mLineFollower;
    public LineSensors(){
        lLineFollower = new AnalogInput(1);
        rLineFollower = new AnalogInput(0);
        //mLineFollower = new AnalogInput(2);

        rLineFollower.setOversampleBits(8);
        lLineFollower.setOversampleBits(8);
        //mLineFollower.setOversampleBits(8);
        
    }
    public void update(){
        SmartDashboard.putNumber("Line Follower R", rLineFollower.getValue());
        SmartDashboard.putNumber("Line Follower L", lLineFollower.getValue());

        //SmartDashboard.putNumber("Line Follower M", lLineFollower.getValue());

    }
    public double getTurnValue(boolean flip){
        double turnVal = 0.0;
        int constant = flip ? -1 : 1;
        if(rLineFollower.getValue() < 3800){
            turnVal = -0.4*constant;
        }
        else if(lLineFollower.getValue() < 3800){
            turnVal = 0.4*constant;
        }
        else {
            turnVal = 0.0;
        }
        return turnVal;


    }
}