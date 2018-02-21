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
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.XboxController;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot implements PIDOutput {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private DriverStation station;
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	private WPI_VictorSPX leftMaster, leftFront, rightMaster, rightFront;
	private WPI_VictorSPX leftLoader, rightLoader;
	private DifferentialDrive drive, loaderDrive;
	private XboxController gamepad;
	private boolean arcademode = false;
	private AnalogInput ultrasonic;
	private double speed = 0.3D;
	private RobotController autoController;
	PIDController turnController;
	AHRS ahrs;
	static final double kP = 0.03;
    static final double kI = 0.00;
    static final double kD = 0.00;
    static final double kF = 0.00;
    static final double kToleranceDegrees = 2.0f;
	
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
		
		leftMaster  = new WPI_VictorSPX(Constants.DRIVE_MASTER_L);
		leftFront  = new WPI_VictorSPX(Constants.DRIVE_FRONT_L);
		rightMaster  = new WPI_VictorSPX(Constants.DRIVE_MASTER_R);
		rightFront = new WPI_VictorSPX(Constants.DRIVE_FRONT_R);
		
		leftLoader = new WPI_VictorSPX(Constants.LOADER_PORT_L);
		rightLoader = new WPI_VictorSPX(Constants.LOADER_PORT_R);
		
		
		leftMaster.setInverted(true);
		leftFront.setInverted(true);
		rightMaster.setInverted(true);
		rightFront.setInverted(true);
		
		leftLoader.setInverted(false);
		rightLoader.setInverted(false);
		
		
		
		leftMaster.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		leftFront.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		rightMaster.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		rightFront.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		
		leftLoader.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		rightLoader.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		
		leftFront.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, Constants.DRIVE_MASTER_L);
		rightFront.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, Constants.DRIVE_MASTER_R);
		
		drive = new DifferentialDrive(leftMaster,rightMaster);
		loaderDrive = new DifferentialDrive(leftLoader, rightLoader);
		
		try {
            ahrs = new AHRS(SPI.Port.kMXP); 
        } catch (RuntimeException ex ) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
		
		ultrasonic = new AnalogInput(4);
		station = DriverStation.getInstance();
		
		autoController = new RobotController(drive, new ArrayList<RobotController.Task>(), ahrs, ultrasonic);
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
				autoController.update();
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
			if (arcademode) arcademode = false;
			else if (!arcademode) arcademode = true;
		}
		//turbo button
		if (gamepad.getBumperPressed(GenericHID.Hand.kRight)) {
			speed = 1D;
		}
		if (gamepad.getBumperReleased(GenericHID.Hand.kRight)) {
			speed = 0.3D;
		}
		
		if (gamepad.getXButton()) {
			drive.arcadeDrive(0.3, 0);
		}
		if (gamepad.getYButton()) {
			drive.arcadeDrive(-0.3, 0);
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

	@Override
	public void pidWrite(double output) {
		// TODO Auto-generated method stub
		
	}
}
