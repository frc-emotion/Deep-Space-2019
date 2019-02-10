package frc.robot;

public class Constants {

    public static final int DRIVE_CONTROLLER_PORT = 0;
    public static final int OPER_CONTROLLER_PORT = 1;

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
    /********************* CAN BUS IDs **********************/
    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
    // Arm
    public static final int ARM_SPARK_CID = 8;
    // Wrist joint
    public static final int WRIST_SPARK_CID = 7;

    // Intake motors
    public static final int INTAKE_SPARK_CID_R = 2;
    public static final int INTAKE_SPARK_CID_L = 1;

    /* Drivetrain */
    public static final int DT_CAN_RA_PORT = 11;
    public static final int DT_CAN_RB_PORT = 10;
    public static final int DT_CAN_RC_PORT = 9;
    public static final int DT_CAN_LA_PORT = 5;
    public static final int DT_CAN_LB_PORT = 4;
    public static final int DT_CAN_LC_PORT = 3;

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//
    /********************* PORTS **********************/
    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//
    // Intake pneumatics ports from PCM. Double solenoids, so 2 ports per side
    public static final int INTAKE_SOL_R_FWD = 0;
    public static final int INTAKE_SOL_R_BWD = 1;
    public static final int INTAKE_SOL_L_FWD = 2;
    public static final int INTAKE_SOL_L_BWD = 3;

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//
    /********************* OTHER **********************/
    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//

    /* Max Currents that can be sent to Neos in Amps */
    public static final int MAX_CURRENT = 35;
    public static final int MAX_WRIST_CURRENT = 20;

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\
    /********************* PATHFINDER **********************/
    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\

    /* Number of encoder ticks per Neo revolution */
    public static final int ENCODER_REV = 42;

    /* Constants for PathFinder in metric units */
    public static double WHEELBASE_WIDTH = 0.6898513;
    public static double WHEEL_DIAMETER = 0.1524;
    public static double MAX_VELOCITY = 2;

}