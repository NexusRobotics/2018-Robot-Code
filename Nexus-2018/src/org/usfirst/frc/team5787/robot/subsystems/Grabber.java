package org.usfirst.frc.team5787.robot.subsystems;

import org.usfirst.frc.team5787.robot.Robotmap;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Grabber extends Subsystem {

	public SpeedController leftArm, rightArm;
	public Servo claw;
	private Preferences prefs = Preferences.getInstance();
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	public Grabber(boolean practicebot) {
		if (practicebot) {
			leftArm	   = new VictorSP(prefs.getInt("PWM_ARM_L", Robotmap.PWM_ARM_L));
			rightArm   = new VictorSP(prefs.getInt("PWM_ARM_R", Robotmap.PWM_ARM_R));
		} else {
			leftArm = new WPI_VictorSPX(prefs.getInt("ARM_L", Robotmap.ARM_L));
			rightArm = new WPI_VictorSPX(prefs.getInt("ARM_R", Robotmap.ARM_R));
			((BaseMotorController) leftArm).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
			((BaseMotorController) rightArm).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
			((WPI_VictorSPX) rightArm).set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, Robotmap.ARM_L);
		}
		claw = new Servo(prefs.getInt("PWM_ARM_SERVO", Robotmap.PWM_ARM_SERVO));		
		leftArm.setInverted(true);
		rightArm.setInverted(false);
	}

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

