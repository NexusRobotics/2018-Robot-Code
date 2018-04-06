package org.usfirst.frc.team5787.robot.subsystems;

import org.usfirst.frc.team5787.robot.Robot;
import org.usfirst.frc.team5787.robot.Robotmap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Lifter extends Subsystem {
	Grabber grabber;
	private Preferences prefs = Preferences.getInstance();
	public WPI_TalonSRX lifter;
	private int switchheight = 1635000;
	private int scaleheight;
	private boolean sensorphase = true;
	public Lifter() {
		lifter = new WPI_TalonSRX(prefs.getInt("SRX_LIFTER", Robotmap.LIFTER_SRX));new WPI_TalonSRX(prefs.getInt("LIFTER_SRX", Robotmap.LIFTER_SRX));
		lifter.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);	
		lifter.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		/* choose the sensor and sensor direction */
		lifter.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);

		/* choose to ensure sensor is positive when output is positive */
		lifter.setSensorPhase(sensorphase);

		/* choose based on what direction you want forward/positive to be.
		 * This does not affect sensor phase. */ 

		/* set the peak and nominal outputs, 12V means full */
		lifter.configNominalOutputForward(0, 10);
		lifter.configNominalOutputReverse(0, 10);
		lifter.configPeakOutputForward(1, 10);
		lifter.configPeakOutputReverse(-1, 10);
		/*
		 * set the allowable closed-loop error, Closed-Loop output will be
		 * neutral within this range. See Table in Section 17.2.1 for native
		 * units per rotation.
		 */
		lifter.configAllowableClosedloopError(0, 40000, 10);

		/* set closed loop gains in slot0, typically kF stays zero. */
		lifter.config_kF(0, 0.0, 10);
		lifter.config_kP(0, 0.001, 10);
		lifter.config_kI(0, 0.0, 10);
		lifter.config_kD(0, 0.1, 10);

		/*
		 * lets grab the 360 degree position of the MagEncoder's absolute
		 * position, and intitally set the relative sensor to match.
		 */
		int absolutePosition = lifter.getSensorCollection().getPulseWidthPosition();
		/* mask out overflows, keep bottom 12 bits */
		absolutePosition &= 0xFFF;
		if (sensorphase)
			absolutePosition *= -1;
		/* set the quadrature (relative) sensor to match absolute */
		lifter.setSelectedSensorPosition(absolutePosition, 0, 10);
	}
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	public void raiseToSwitch() {
		lifter.set(ControlMode.Position, switchheight);
		Robot.getInstance().grabber.intake(-0.1);
	}
	public boolean atTarget() {
		if (Math.abs(lifter.getSelectedSensorPosition(0)-switchheight)<150000) {
			return true;
		} else 
			return false;
	}
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

