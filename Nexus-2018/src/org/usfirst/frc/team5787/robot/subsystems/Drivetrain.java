package org.usfirst.frc.team5787.robot.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import org.usfirst.frc.team5787.robot.Robotmap;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
/**
 *
 */
public class Drivetrain extends Subsystem implements PIDOutput{
	private SpeedController leftBack, leftFront, rightBack, rightFront;
	private SpeedControllerGroup leftGroup, rightGroup;
	private double offset;
	public double autospeed = 0.4;
	Preferences prefs = Preferences.getInstance();
	public DifferentialDrive drive;

	public Drivetrain(boolean practicebot) {
		if (practicebot) {
			leftFront = new VictorSP(Robotmap.PWM_DRIVE_FRONT_L);
			leftBack = new VictorSP(Robotmap.PWM_DRIVE_BACK_L);
			rightFront = new VictorSP(Robotmap.PWM_DRIVE_FRONT_R);
			rightBack = new VictorSP(Robotmap.PWM_DRIVE_BACK_R);
		} else {
			leftBack   = new WPI_VictorSPX(prefs.getInt("DRIVE_BACK_L", Robotmap.DRIVE_BACK_L));
			leftFront  = new WPI_VictorSPX(prefs.getInt("DRIVE_FRONT_L", Robotmap.DRIVE_FRONT_L));
			rightBack  = new WPI_VictorSPX(prefs.getInt("DRIVE_BACK_R", Robotmap.DRIVE_BACK_R));
			rightFront = new WPI_VictorSPX(prefs.getInt("DRIVE_FRONT_R", Robotmap.DRIVE_FRONT_R));
			((BaseMotorController) leftBack).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Coast);
			((BaseMotorController) leftFront).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Coast);
			((BaseMotorController) rightBack).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Coast);
			((BaseMotorController) rightFront).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Coast);
		}
		leftBack.setInverted(false);
		leftFront.setInverted(false);
		rightBack.setInverted(false);
		rightFront.setInverted(false);
		
		leftGroup = new SpeedControllerGroup(leftBack, leftFront);
		rightGroup = new SpeedControllerGroup(rightBack, rightFront);
		drive = new DifferentialDrive(leftGroup,rightGroup);
		
	}
	public void tank (double leftSpeed, double rightSpeed) {
		leftGroup.set(leftSpeed);
		rightGroup.set(rightSpeed);
	}
	public void drive(double speed) {
		rightGroup.set(speed);
		leftGroup.set(speed);
	}
	public void drive(double speed, double rotation) {
		drive.arcadeDrive(speed, rotation, false);
	}
	public void stop(){
		rightGroup.stopMotor();
		leftGroup.stopMotor();
	}
	public void clear() {
		rightGroup.set(0);
		leftGroup.set(0);
	}
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
	@Override
	public void pidWrite(double output) {
		offset = output;
		drive(autospeed,offset);
	}
}

