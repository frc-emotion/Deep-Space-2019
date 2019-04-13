package frc.robot;

import java.util.HashMap;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pivot extends Thread {
    private CANSparkMax sparkMax; // Spark Max controller from REV Robotics. Controls NEO motor
    private CANEncoder encoder; // pivot encoder, built into the NEO
    private PIDControl pidControl;
    private boolean holdEnabled = false, macroEnabled = false; // checks to see if the pivot should hold its position or
                                                               // perform a
    // macro
    private HashMap<Character, Boolean> macroOverrides = new HashMap<>(); 
    private double holdPos = 0; // store which position pivot should hold
    private int[] macroCheck = new int[4]; // this list tracks which macro is enabled. A switch statement in the run
                                           // loop
    // checks which macro is running
    private double[] macroPosList = new double[4]; // store which positions pivot needs to go to for macros.
    private double startEncoderVal = 0; // stores initial encoder value if pivot encoder doesnt reset.

    private String name = "PIVOT";
    private double curve = 2.0, scale = 0.5;

    private GenericHID.Hand hand;

    public Pivot(String name, int canbus, MotorType motorType, int maxCurrent, GenericHID.Hand control) {
        this.name = name;
        hand = control;
        sparkMax = new CANSparkMax(canbus, motorType);
        sparkMax.setSmartCurrentLimit(maxCurrent);
        sparkMax.setSecondaryCurrentLimit(maxCurrent);

        encoder = sparkMax.getEncoder();
        startEncoderVal = encoder.getPosition();

        pidControl = new PIDControl(0, 0, 0);

        // load all the macro values
        macroPosList[0] = startEncoderVal;
        macroPosList[1] = startEncoderVal;
        macroPosList[2] = startEncoderVal;
        macroPosList[3] = startEncoderVal;

        macroOverrides.put('a', false);
        macroOverrides.put('b', false);
        macroOverrides.put('x', false);
        macroOverrides.put('y', false);
        //updateSmartDashboard();
    }

    /**
     * Method that runs in the teleop periodic loop
     * <p>
     * Logic for manual pivot control, holding, and macros
     * 
     * @return void
     */
    @Override
    public void run() {
        //updateSmartDashboard();
        // if controller is under a threshold and not in hold mode, then the operator
        // has probably let go of the jstick
        // so activate the hold mode
        if (Math.abs(Robot.operatorController.getY(hand)) < 0.2 && !holdEnabled) {
            holdPos = encoder.getPosition(); // get last position of the pivot to hold at
            sparkMax.set(0); // send a stop signal
            holdEnabled = true;
        }

        int toggled = getToggled();
        if (Math.abs(Robot.operatorController.getY(hand)) > 0.2) { // if joysick axis is above a threshold
                                                                   // activate
                                                                   // manual mode and diable pid
            // disable anything related to macros of pid hold so that it doesnt interfere
            // with the operator control
            holdEnabled = false;
            macroEnabled = false;   
            disableMacros();
            pidControl.cleanup();

            manualMove();
        } 
        else if (toggled == -1) { // see if any macro is enabled. If not, start checking button presses If yes skip over this logic
            if (!macroOverrides.get('a') && Robot.operatorController.getAButton()) {
                toggleMacro(0);
            } else if (!macroOverrides.get('b') && Robot.operatorController.getBButton()) {
                toggleMacro(1);
            } else if (!macroOverrides.get('x') && Robot.operatorController.getXButton()) {
                toggleMacro(2);
            } else if (!macroOverrides.get('y') && Robot.operatorController.getYButton()) {
                toggleMacro(3);
            }
        }
        // else if(Robot.operatorController.getStartButtonReleased()){
        // startEncoderVal = myEncoder.getPosition();
        // }
        //else { // if no input is being passed in
        if (holdEnabled && !macroEnabled) { // if hold mode is activated use pid to go the the last recorded encoder
                                            // position
            sparkMax.set(pidControl.getValue(holdPos, encoder.getPosition()));
        }
        if (toggled != -1 && macroEnabled) {
            sparkMax.set(pidControl.getValue(macroPosList[toggled], encoder.getPosition()));
        }
           


        

    }
    public void overrideMacro(char btn){
        macroOverrides.put(btn, true);
    }
    /**
     * @return the pidControl
     */
    public PIDControl getPidControl() {
        return pidControl;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * @param curve the curve to set
     */
    public void setCurve(double curve) {
        this.curve = curve;
    }

    /**
     * Move the pivot manully using the operator xbox controller.
     * 
     * @return void
     */
    public void manualMove() {
        double inputVal = Robot.operatorController.getY(hand);
        int direction = inputVal > 0 ? 1 : -1;
        sparkMax.set(Math.pow(Math.abs(inputVal), curve) * scale * direction); // scale pivot speed down
                                                                               // in both directions

    }

    /**
     * Enable a specific macro to run using an index value
     * 
     * @param index - position of macro
     * @return void
     */
    public void toggleMacro(int index) {
        if (macroCheck[index] != 1) {
            disableMacros();
            macroCheck[index] = 1;
            macroEnabled = true;
        }
        // disableMacros();
        // macroEnabled = true;
        // if (macroCheck[index] == 0) { // toggle macros if not active
        // macroCheck[index] = 1;
        // } else {
        // macroCheck[index] = 0;
        // }
    }

    /**
     * Reset all the macro values in the macro controller list
     * 
     * @return void
     */
    public void disableMacros() {
        for (int i = 0; i < macroCheck.length; i++) { // disable all othermacros
            macroCheck[i] = 0;
        }
    }

    /**
     * Get which macro is currently enabled for the pivot.
     * 
     * @return int - which macro index is selected
     */
    public int getToggled() {
        for (int i = 0; i < macroCheck.length; i++) { // get macro
            if (macroCheck[i] == 1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * set a macro with a keybind
     * 
     * @return void
     */
    public void setMacro(char key, double pos) {
        int index = 0;
        switch (key) {
        case 'a':
            index = 0;
            break;
        case 'b':
            index = 1;
            break;
        case 'x':
            index = 2;
            break;
        case 'y':
            index = 3;
        }
        macroPosList[index] = startEncoderVal + pos;
    }

    /**
     * Publish relevant values to the smartdashboard. Make sure this isnt called at
     * comp.
     * 
     * @return void
     */
    public void updateSmartDashboard() {
        SmartDashboard.putNumber(name + " ENCODER", encoder.getPosition());
        SmartDashboard.putNumber(name + " HOLD POS", holdPos);
        SmartDashboard.putBoolean(name + " HOLD?", holdEnabled);
        SmartDashboard.putBoolean(name + " Macro enabled?", macroEnabled);
        SmartDashboard.putNumber(name + " MOTOR TEMP", sparkMax.getMotorTemperature());
    }
}