package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LemonTorch{
    private NetworkTable dataTable; // main table from limelight 
    private NetworkTableEntry tX, tY, tA, tV; // network table entries from the main network table
    private double errorX = 0, errorY = 0 , area = 0, targetFound = 0;

    private boolean gatsby; //green light 

    public LemonTorch(){

        dataTable = NetworkTableInstance.getDefault().getTable("limelight");
        dataTable.getEntry("pipeline").setNumber(0); // set to ball pipeline
		tX = dataTable.getEntry("tx"); // get all the vals
		tY = dataTable.getEntry("ty");
		tA = dataTable.getEntry("ta");
        tV = dataTable.getEntry("tv");
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
        SmartDashboard.putNumber("TX", errorX);
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
        errorX = tX.getDouble(0.0);
        errorY = tY.getDouble(0);
        area = tA.getDouble(0);
        targetFound = tV.getDouble(0);
    }

    /**
     * @return the errorX
     */
    public double getErrorX() {
        return dataTable.getEntry("tx").getDouble(0.0);
    }

    /**
     * @return the errorY
     */
    public double getErrorY() {
        return errorY;
    }

    /**
     * switch pipelines 
     */
    public void switchPipelines(int pipe){
        dataTable.getEntry("pipeline").setNumber(pipe); 
    }
}