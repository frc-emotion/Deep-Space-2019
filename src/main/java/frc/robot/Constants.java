package frc.robot;


public class Constants{
    public static final int DRIVE_CONTROLLER_PORT = 0;
    public static final int OPER_CONTROLLER_PORT = 1;
    public static final int MAX_CURRENT = 35; // max current that can be sent to sparks in amps
    public static final double ENCODER_REV = 42; //number of ticks per motor rev
    
    // DriveTrain constants
    public static final int DTRAIN_PORT_1 = 0, DTRAIN_PORT_2 = 0, DTRAIN_PORT_3 = 0, // Ports for left side of driveTrain
    public static final int DTRAIN_PORT_4 = 0, DTRAIN_PORT_5 = 0, DTRAIN_PORT_6 = 0; // Ports for right side of driveTrain
    public static final double CURVE = 1.5; // for tankDrive as a param
    public static final double POWER = 1; // for tankDrive as a param
}