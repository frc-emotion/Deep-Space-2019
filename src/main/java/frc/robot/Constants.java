package frc.robot;

public class Constants {

    public static final int DRIVE_CONTROLLER_PORT = 0;
    public static final int OPER_CONTROLLER_PORT = 1;
    public static final int CLIMB_CONTROLLER_PORT = 3;

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
    /********************* CAN BUS IDs **********************/
    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
    // Arm
    public static final int ARM_SPARK_CID = 8;
    // Wrist joint
    public static final int WRIST_SPARK_CID = 7;

    // Intake motors
    public static final int INTAKE_SPARK_CID_R = 1;
    public static final int INTAKE_SPARK_CID_L = 2;                                                                                             
      

    /* Drivetrain */
    public static final int DT_CAN_RA_PORT = 11;
    public static final int DT_CAN_RB_PORT = 10;
    public static final int DT_CAN_RC_PORT = 9;
    public static final int DT_CAN_LA_PORT = 5;
    public static final int DT_CAN_LB_PORT = 4;
    public static final int DT_CAN_LC_PORT = 3;

    // Climb Motors
    public static final int CLIMB_SPARK_CID_A = 14; 
    public static final int CLIMB_SPARK_CID_B = 6; 
    public static final int CLIMBER_CONTROLLER_AXIS = 1;// temporary

    // Screw Motor
    public static final int SCREW_TALON_CID = 15;
    public static final double SCREW_SPEED = 0.4;

    //vaccumm motor
    public static final int VACC_TALON_CID = 16;

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//
    /********************* PORTS **********************/
    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//
    // Intake pneumatics ports from PCM. Double solenoids, so 2 ports per side
    public static final int INTAKE_SOL_FWD = 0;
    public static final int INTAKE_SOL_BKWD = 1;


    //vaccumm release solenoid
    public static final int VACCUMM_SOL_PORT = 2;

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//
    /********************* OTHER **********************/
    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//

    /* Max Currents that can be sent to Neos in Amps */
    public static final int MAX_CURRENT = 45;
    public static final int MAX_DT_CURRENT = 35;
    public static final int MAX_WRIST_CURRENT = 20;

    public static final double WRIST_PWR_SCALE = 0.5;
    public static final double ARM_PWR_SCALE = 0.6;

    public static final double TURBO_SPEED = 0.999;
    public static final double REGULAR_SPEED = 0.7;
    public static final double SLOW_SPEED = 0.4;

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\
    /********************* PATHFINDER **********************/
    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\

    /* Number of encoder ticks per Neo revolution */
    // NEED TO CHANGE THIS
    public static final int ENCODER_REV = 42;// temporary

    /* Constants for PathFinder in metric units */
    public static double WHEELBASE_WIDTH = 0.6898513;
    public static double WHEEL_DIAMETER = 0.1524;
    public static double MAX_VELOCITY = 2;

}