package frc.robot;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Wrist extends Pivot {

    public Wrist() {
        super("WRIST", Constants.WRIST_SPARK_CID, MotorType.kBrushless, Constants.MAX_WRIST_CURRENT,
        Hand.kRight);
        configurePid();
        configureMacros();
    }

    private void configurePid() {
        getPidControl().setPID(.014f, 0.0001f, 0f);
        getPidControl().setMaxSpeed(Constants.PIVOT_MAX_SPEED);
        setCurve(Constants.WRIST_CURVE);
        setScale(Constants.WRIST_PWR_SCALE);
    }

    private void configureMacros() {
        setMacro('a', 4.07); // cargo lvl2
        setMacro('b', -5.7); // bottom hatch placement;
        setMacro('x', 18.95); // cargo bottom;
        setMacro('y', 14.95); // hatch floor pickup
    }

}