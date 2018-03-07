/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5787.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team5787.robot.RobotController.TaskType;
import org.usfirst.frc.team5787.robot.Robotmap;
import edu.wpi.first.wpilibj.SpeedController;
import java.util.ArrayList;
import java.util.Arrays;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.VictorSP;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot{
	public static final boolean isPracticerobot = true;
	private enum Automode{
		defaultauto, drivestraight, testauto
	}
	private DriverStation station;
	private boolean clawServoToggle = false;
	private SendableChooser<Boolean> drive_chooser = new SendableChooser<>();
	private SendableChooser<Automode> automode = new SendableChooser<>(); 
	private SpeedController leftBack, leftFront, rightBack, rightFront, leftArm, rightArm, climber, lifter;
	private Servo claw;
	private DifferentialDrive drive;
	private XboxController gamepad;
	private boolean arcademode = false;
	private AnalogInput ultrasonic;
	private double speed = 0.3D;
	private RobotController autoController;
	private AHRS ahrs;
	private enum Upmode{
	me, block
	}
	
	private SpeedControllerGroup leftGroup, rightGroup;
	
	private Upmode currentupmode= Upmode.block;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		drive_chooser.addDefault("Tank Drive", Boolean.FALSE);
		drive_chooser.addObject("Arcade Drive", Boolean.TRUE);
		SmartDashboard.putData("Drive Mode", drive_chooser);
		gamepad = new XboxController(0);
		
		
		
		
		if (isPracticerobot) {
			leftBack   = new VictorSP(Robotmap.PWM_DRIVE_BACK_L);
			leftFront  = new VictorSP(Robotmap.PWM_DRIVE_FRONT_L);
			rightBack  = new VictorSP(Robotmap.PWM_DRIVE_BACK_R);
			rightFront = new VictorSP(Robotmap.PWM_DRIVE_FRONT_R);
			leftArm	   = new VictorSP(Robotmap.PWM_ARM_L);
			rightArm   = new VictorSP(Robotmap.PWM_ARM_R);
			climber    = new VictorSP(Robotmap.PWM_CLIMBER);
			lifter     = new VictorSP(Robotmap.PWM_LIFTER);
			
		} else {
			leftBack   = new WPI_VictorSPX(Robotmap.DRIVE_BACK_L);
			leftFront  = new WPI_VictorSPX(Robotmap.DRIVE_FRONT_L);
			rightBack  = new WPI_VictorSPX(Robotmap.DRIVE_BACK_R);
			rightFront = new WPI_VictorSPX(Robotmap.DRIVE_FRONT_R);
			leftArm = new WPI_VictorSPX(Robotmap.ARM_L);
			rightArm = new WPI_VictorSPX(Robotmap.ARM_R);		
			climber = new WPI_VictorSPX(Robotmap.CLIMBER);
			lifter = new WPI_TalonSRX(Robotmap.LIFTER_SRX);
			((BaseMotorController) leftBack).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Coast);
			((BaseMotorController) leftFront).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Coast);
			((BaseMotorController) rightBack).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Coast);
			((BaseMotorController) rightFront).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Coast);
			((BaseMotorController) leftArm).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
			((BaseMotorController) rightArm).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
			((BaseMotorController) climber).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
			((BaseMotorController) lifter).setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);			
			((WPI_VictorSPX) rightArm).set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, Robotmap.ARM_L);
		}
		leftBack.setInverted(false);
		leftFront.setInverted(false);
		rightBack.setInverted(false);
		rightFront.setInverted(false);

		claw = new Servo(Robotmap.ARM_SERVO);		
		leftArm.setInverted(false);
		rightArm.setInverted(true);
		
		leftGroup = new SpeedControllerGroup(leftBack, leftFront);
		rightGroup = new SpeedControllerGroup(rightBack, rightFront);
		drive = new DifferentialDrive(leftGroup,rightGroup);
		
		ultrasonic = new AnalogInput(4);
		station = DriverStation.getInstance();
		RobotController.Task[] tasks = new RobotController.Task[] {new RobotController.Task(TaskType.MOVE, 3), new RobotController.Task(TaskType.PICKUP, RobotController.PICKUP_STEPS)};
		autoController = new RobotController(drive, leftArm, lifter, claw, new ArrayList<RobotController.Task>(Arrays.asList(tasks)), ahrs, ultrasonic);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		
		autoController.update();
		//drive.tankDrive(1D, 0, false);
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		if (gamepad.getYButtonPressed()) {
			arcademode = !arcademode;
		}
		
		if(gamepad.getXButtonPressed()) {
			if (currentupmode == Upmode.block ) {
				currentupmode = Upmode.me;
				lifter.set(0);
			} 
			else {
				currentupmode = Upmode.block;
				climber.set(0);
			}
			
				
		}
		
		//turbo button
		if (gamepad.getBumper(GenericHID.Hand.kRight)) {
			speed = 1D;
		}
		else {
			speed = 0.3D;
		}
		if (gamepad.getBumperPressed(GenericHID.Hand.kLeft)) {
			claw.setAngle(clawServoToggle ? 0 : 90);
			clawServoToggle = !clawServoToggle;
		}
		
		if (gamepad.getBButton()) {
			leftArm.set(-0.5D);
		}
		
		else if (gamepad.getAButton()) {
			leftArm.set(1);
		}
		else {
			leftArm.set(0);
		}
		
		if(gamepad.getTriggerAxis(GenericHID.Hand.kLeft)>0.55D) {
			if (currentupmode==Upmode.me)climber.set((gamepad.getTriggerAxis(GenericHID.Hand.kLeft)-0.5D)*2D);
			if (currentupmode==Upmode.block)lifter.set((gamepad.getTriggerAxis(GenericHID.Hand.kLeft)-0.5D)*2D);
		}
		else if(gamepad.getTriggerAxis(GenericHID.Hand. kRight)>0.55D) {
			if (currentupmode==Upmode.me)climber.set((gamepad.getTriggerAxis(GenericHID.Hand.kRight)-0.5D)*-2D);
			if (currentupmode==Upmode.block)lifter.set((gamepad.getTriggerAxis(GenericHID.Hand.kRight)-0.5D)*-2D);
		}
		else {
			lifter.set(0);
			climber.set(0);
		}
		
		if (arcademode)
			drive.arcadeDrive(gamepad.getY(GenericHID.Hand.kLeft)*speed*-1D, gamepad.getX(GenericHID.Hand.kLeft)*speed,false);
		else
			drive.tankDrive(gamepad.getY(GenericHID.Hand.kLeft)*speed*-1D, gamepad.getY(GenericHID.Hand.kRight)*speed*-1D,false);
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}

}
