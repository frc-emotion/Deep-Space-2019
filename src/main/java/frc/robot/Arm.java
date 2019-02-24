package frc.robot;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Arm extends Pivot {

    public Arm() {
        super("ARM", Constants.ARM_SPARK_CID, MotorType.kBrushless, Constants.MAX_CURRENT, Hand.kLeft);
        configurePid();
        configureMacros();
    }

    private void configurePid() {
        getPidControl().setPID(0.014f, 0f, 0f);
        getPidControl().setMaxSpeed(Constants.PIVOT_MAX_SPEED);
        setScale(Constants.ARM_PWR_SCALE);
    }

    private void configureMacros() {
        setMacro('a', 17.64); // cargo lvl2
        setMacro('b', 52.4); // bottom hatch placement;
        setMacro('x', 47.5); // cargo bottom;
        setMacro('y', 53); // hatch floor pickup
    }

}