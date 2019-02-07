package frc.robot;


public class Constants{

    public static final int DRIVE_CONTROLLER_PORT = 0;
    public static final int OPER_CONTROLLER_PORT = 1;


    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
	/*********************CAN BUS IDs**********************/
	//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
    // Arm
    public static final int ARM_SPARK_CID = 0;
    // Wrist joint
    public static final int WRIST_SPARK_CID = 0;

    // Intake motors
    public static final int INTAKE_SPARK_CID_R = 0;
    public static final int INTAKE_SPARK_CID_L = 0;
    
    /* Drivetrain */
    public static final int DT_CAN_RA_PORT = 1;
    public static final int DT_CAN_RB_PORT = 2;
    public static final int DT_CAN_RC_PORT = 3;
    public static final int DT_CAN_LA_PORT = 4;
    public static final int DT_CAN_LB_PORT = 5;
    public static final int DT_CAN_LC_PORT = 6;


    //=======================================================



    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//
	/*********************OTHER**********************/
	//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//
    
    /* Max Currents that can be sent to Neos in Amps */
    public static final int MAX_CURRENT = 35;
    public static final int MAX_WRIST_CURRENT = 20;


    //================================================




    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\
	/*********************PATHFINDER**********************/
	//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\
    
    /* Number of encoder ticks per Neo revolution */
    public static final int ENCODER_REV = 42; 


    /* Constants for PathFinder in metric units */
    public static double WHEELBASE_WIDTH = 0.6898513;
    public static double WHEEL_DIAMETER = 0.1524;
    public static double MAX_VELOCITY = 2;

    //======================================================


}