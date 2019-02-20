/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  public static XboxController driveController;
  public static XboxController operatorController;
  public static Joystick climbController;
  public static LemonTorch lemonTorch;

  // Mechs
  // Arm armMech;
  // Wrist wristMech;
  Pivot armMech;
  Pivot wristMech;
  Intake intakeMech;
  DriveTrain driveTrain;

  RobotFlipper flipMech;

  // Nav-X Gyro
  public static AHRS gyro;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    driveController = new XboxController(Constants.DRIVE_CONTROLLER_PORT);
    operatorController = new XboxController(Constants.OPER_CONTROLLER_PORT);
    climbController = new Joystick(Constants.CLIMB_CONTROLLER_PORT);

    // armMech = new Arm();
    // wristMech = new Wrist();
    armMech = new Pivot("ARM", Constants.ARM_SPARK_CID, MotorType.kBrushless, Constants.MAX_CURRENT, Hand.kLeft);
    armMech.getPidControl().setPID(0.014f, 0f, 0f);
    armMech.getPidControl().setMaxSpeed(0.5);

    armMech.setMacro('a', 17.64); //cargo lvl2
    armMech.setMacro('b', 52.4); //bottom hatch placement;
    armMech.setMacro('x', 47.5); //cargo bottom;
    armMech.setMacro('y', 53); //hatch floor pickup

    armMech.setScale(Constants.ARM_PWR_SCALE);


    wristMech = new Pivot("WRIST", Constants.WRIST_SPARK_CID, MotorType.kBrushless, Constants.MAX_WRIST_CURRENT, Hand.kRight);
    wristMech.getPidControl().setPID(.014f, 0.0001f, 0f);
    wristMech.getPidControl().setMaxSpeed(0.5);

    wristMech.setMacro('a', 4.07); //cargo lvl2
    wristMech.setMacro('b', -5.7); //bottom hatch placement;
    wristMech.setMacro('x', 18.95); //cargo bottom;
    wristMech.setMacro('y', 14.95); //hatch floor pickup

    wristMech.setCurve(2.2);
    wristMech.setScale(Constants.WRIST_PWR_SCALE);

    Thread t = new Thread(() -> {
      while (!Thread.interrupted()) {
        armMech.run();
      }
    });
    t.start();


    intakeMech = new Intake();
    driveTrain = new DriveTrain();
    lemonTorch = new LemonTorch();
    flipMech = new RobotFlipper();

    gyro = new AHRS(Port.kUSB);

    updateSmartDashboard();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    super.teleopInit();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    intakeMech.run();
    wristMech.run();
    lemonTorch.update();
    driveTrain.run();

    flipMech.run();
    updateSmartDashboard();
  }
  
  /** 
   * Update smartdashboard values of static robot objs
   * 
   * @return void
   */
  public void updateSmartDashboard() {
    SmartDashboard.putNumber("Nav-X Angle", gyro.getAngle());
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
