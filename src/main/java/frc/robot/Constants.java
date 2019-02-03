package frc.robot;


public class Constants{
    /* Controller Ports for DS */
    public static final int DRIVE_CONTROLLER_PORT = 0;
    public static final int OP_CONTROLLER_PORT = 1;

    /* Max Current that can be sent to Neos in Amps */
    public static final int MAX_CURRENT = 35;
    
    /* Number of encoder ticks per Neo revolution */
    public static final double ENCODER_REV = 42; 

    /* Port numbers for drivetrain Neos */
    public static final int DT_CAN_RA_PORT = 1;
    public static final int DT_CAN_RB_PORT = 2;
    public static final int DT_CAN_RC_PORT = 3;
    public static final int DT_CAN_LA_PORT = 4;
    public static final int DT_CAN_LB_PORT = 5;
    public static final int DT_CAN_LC_PORT = 6;
}