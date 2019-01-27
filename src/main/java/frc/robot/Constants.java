package frc.robot;
public class Constants{
    //Can bus ports for drivetrain neo motors. Right(ABC) and Left(ABC)
    public static final int DT_CAN_RA_PORT = 1;
    public static final int DT_CAN_RB_PORT = 2;
    public static final int DT_CAN_RC_PORT = 3;
    public static final int DT_CAN_LA_PORT = 4;
    public static final int DT_CAN_LB_PORT = 5;
    public static final int DT_CAN_LC_PORT = 6;


    public static final int DRIVE_CONTROLLER_PORT = 0;

    public static final int MAX_CURRENT = 35; // max current that can be sent to sparks in amps
    public static final double GEAR_RATIO = ((double) 50 / 14) * ((double) 54 / 20); //gear box ratio to use for encoders

    public static final double ENCODER_REV = 42; //number of ticks per motor rev

    
}