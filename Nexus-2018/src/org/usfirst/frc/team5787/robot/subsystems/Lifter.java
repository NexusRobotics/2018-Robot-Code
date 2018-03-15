package org.usfirst.frc.team5787.robot.subsystems;

import org.usfirst.frc.team5787.robot.Robotmap;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Lifter extends Subsystem {
	private Preferences prefs = Preferences.getInstance();
	public WPI_TalonSRX lifter;
	public Lifter() {
		lifter = new WPI_TalonSRX(prefs.getInt("SRX_LIFTER", Robotmap.LIFTER_SRX));new WPI_TalonSRX(prefs.getInt("LIFTER_SRX", Robotmap.LIFTER_SRX));
		lifter.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);	
	}
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

