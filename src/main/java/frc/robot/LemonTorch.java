package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LemonTorch{
    private NetworkTable dataTable; // main table from limelight 
    private NetworkTableEntry tXBall, tYBall, tABall, tVBall; // network table entries from the main network table
    private double errorXBall = 0, errorYBall = 0 , areaBall = 0, ballFound = 0;

    private boolean gatsby; //green light 

    public LemonTorch(){

        dataTable = NetworkTableInstance.getDefault().getTable("limelight");
		tXBall = dataTable.getEntry("tx"); // get all the vals
		tYBall = dataTable.getEntry("ty");
		tABall = dataTable.getEntry("ta");
        tVBall = dataTable.getEntry("tv");
        dataTable.getEntry("ledMode").setNumber(3); // set green light to be off
        
    }

    public void toggleLight(){ // turn limelight led on or off
        if(dataTable.getEntry("ledMode").getDouble(0) == 3){ // if off
            dataTable.getEntry("ledMode").setNumber(1); //turn on
            gatsby = true;
        }
        else{ //if on
            dataTable.getEntry("ledMode").setNumber(3); //turn off
            gatsby = false;
        }
    }

    public void updateSmartDashboard(){
        SmartDashboard.putNumber("TX", errorXBall);
    }

    /**
	 * update all values (sd + network tables)
	 * @return void
	 */
    public void update(){
        updateTableValues();
        updateSmartDashboard();
    }

    
    /**
	 * update local values from limelight network tables 
	 * x, y, area, v
	 * @return void
	 */
    private void updateTableValues(){
        errorXBall = tXBall.getDouble(0.0);
        errorYBall = tYBall.getDouble(0);
        areaBall = tABall.getDouble(0);
        ballFound = tVBall.getDouble(0);
    }

    /**
     * @return the errorXBall
     */
    public double getErrorXBall() {
        return dataTable.getEntry("tx").getDouble(0.0);
    }
}