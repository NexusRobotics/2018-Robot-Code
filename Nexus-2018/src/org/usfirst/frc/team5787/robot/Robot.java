/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5787.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.XboxController;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	private WPI_VictorSPX leftMaster, leftFront, rightMaster, rightFront;
	private DifferentialDrive drive;
	private XboxController gamepad;
	private boolean arcademode = false;
	private double speed = 0.3D;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		gamepad = new XboxController(0);
		
		leftMaster  = new WPI_VictorSPX(0);
		leftFront  = new WPI_VictorSPX(1);
		rightMaster  = new WPI_VictorSPX(2);
		rightFront = new WPI_VictorSPX(3);
		
		
		leftMaster.setInverted(true);
		leftFront.setInverted(true);
		rightMaster.setInverted(true);
		rightFront.setInverted(true);
		
		leftMaster.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		leftFront.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		rightMaster.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		rightFront.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		
		leftFront.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, 0);
		rightFront.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, 2);
		
		drive = new DifferentialDrive(leftMaster,rightMaster);
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
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				// Put default auto code here
				break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		if (gamepad.getYButtonPressed()) {
			if (arcademode)
				arcademode = false;
			else if (!arcademode)
				arcademode = true;
		}
		//turbo button
		if (gamepad.getBumperPressed(GenericHID.Hand.kRight)) {
			speed = 1D;
		}
		if (gamepad.getBumperReleased(GenericHID.Hand.kRight)) {
			speed = 0.3D;
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
