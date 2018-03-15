package org.usfirst.frc.team5787.robot.subsystems;

import org.usfirst.frc.team5787.robot.Robotmap;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Climber extends Subsystem {
	public WPI_VictorSPX climber;
	private Preferences prefs = Preferences.getInstance();
	public Climber() {
		climber = new WPI_VictorSPX(prefs.getInt("CLIMBER", Robotmap.CLIMBER));
		climber.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
	}
	
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

