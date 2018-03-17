/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5787.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import org.usfirst.frc.team5787.robot.RobotController.TaskType;
import org.usfirst.frc.team5787.robot.Robotmap;
import org.usfirst.frc.team5787.robot.subsystems.*;
import java.util.ArrayList;
import java.util.Arrays;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;




/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot implements PIDOutput{
	public static final boolean IS_PRACTICE_ROBOT = false;
	private enum Automode{
		defaultauto, testauto
	}
	
	private static Robot instance;
	
	private Preferences prefs = Preferences.getInstance();
	//private SerialPort rs232;
	private double autorotaterate;
	private double[] displacement = {0,0,0};
	private DriverStation station;
	UsbCamera camera;
	private Timer timer = new Timer();
	private boolean clawServoOpen = true;
	private boolean clawiskill = false;
	private int clawrevivecount = 0;
	private SendableChooser<Boolean> drive_chooser = new SendableChooser<>();
	private SendableChooser<Automode> automode = new SendableChooser<>(); 
	private SendableChooser<String> startposition = new SendableChooser<String>();
	private XboxController driverxbox, manipxbox;
	private boolean arcademode = false;
	private AnalogInput ultrasonic;
	private double speed = 0.3D;
	private RobotController autoController;
	private AHRS ahrs = new AHRS(SPI.Port.kMXP);;
	public final Drivetrain drivetrain = new Drivetrain(prefs.getBoolean("IS_PRACTICE_ROBOT", IS_PRACTICE_ROBOT));
	public final Grabber grabber = new Grabber(prefs.getBoolean("IS_PRACTICE_ROBOT", IS_PRACTICE_ROBOT));
	public final Climber climber = new Climber();
	public final Lifter lifter = new Lifter();
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
		
		ahrs.resetDisplacement();
		ahrs.reset();
		ahrs.getYaw();
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(270, 203);
		camera.setFPS(15);
		drive_chooser.addDefault("Tank Drive", Boolean.FALSE);
		drive_chooser.addObject("Arcade Drive", Boolean.TRUE);
		
		automode.addDefault("default", Automode.defaultauto);
		automode.addObject("Experimental", Automode.testauto);
		
		startposition.addDefault("Switch Align", "switch");
		startposition.addObject("Left", "left");
		startposition.addObject("Center", "center");
		startposition.addObject("Right", "right");
		SmartDashboard.putData(startposition);
		SmartDashboard.putData("Drive Mode", drive_chooser);
		SmartDashboard.putData(automode);
		driverxbox = new XboxController(0);
		manipxbox = new XboxController(1);
		//rs232 = new SerialPort(115200, SerialPort.Port.kOnboard);
		
		/*RobotController.Task[] tasks;
		if (DriverStation.getInstance().getLocation() == 2) {
			if (DriverStation.getInstance().getGameSpecificMessage().charAt(0) == 'L') {
				tasks = new RobotController.Task[] {new RobotController.Task(RobotController.TaskType.MOVE, 98), new RobotController.Task(RobotController.TaskType.ROTATE_L, 90), new RobotController.Task(RobotController.TaskType.MOVE, 48), new RobotController.Task(RobotController.TaskType.ROTATE_R, 90), new RobotController.Task(RobotController.TaskType.MOVE, 35), new RobotController.Task(RobotController.TaskType.PLACE, RobotController.PLACE_STEPS)};
			}
			else {
				tasks = new RobotController.Task[] {new RobotController.Task(RobotController.TaskType.MOVE, 98), new RobotController.Task(RobotController.TaskType.ROTATE_R, 90), new RobotController.Task(RobotController.TaskType.MOVE, 48), new RobotController.Task(RobotController.TaskType.ROTATE_L, 90), new RobotController.Task(RobotController.TaskType.MOVE, 35), new RobotController.Task(RobotController.TaskType.PLACE, RobotController.PLACE_STEPS)};
			}
			
		}
		if ((DriverStation.getInstance().getGameSpecificMessage().charAt(0) == 'L') == (DriverStation.getInstance().getLocation() == 1)) {
			if (DriverStation.getInstance().getGameSpecificMessage().charAt(0) == 'L') {
				tasks = new RobotController.Task[] {new RobotController.Task(RobotController.TaskType.MOVE, 209), new RobotController.Task(RobotController.TaskType.ROTATE_R, 90), new RobotController.Task(RobotController.TaskType.MOVE, 20), new RobotController.Task(RobotController.TaskType.ROTATE_R, 90), new RobotController.Task(RobotController.TaskType.PLACE, RobotController.PLACE_STEPS), new RobotController.Task(RobotController.TaskType.MOVE, 6), new RobotController.Task(RobotController.TaskType.PICKUP, RobotController.PICKUP_STEPS)};
			}
			else {
				tasks = new RobotController.Task[] {new RobotController.Task(RobotController.TaskType.MOVE, 209), new RobotController.Task(RobotController.TaskType.ROTATE_L, 90), new RobotController.Task(RobotController.TaskType.MOVE, 20), new RobotController.Task(RobotController.TaskType.ROTATE_L, 90), new RobotController.Task(RobotController.TaskType.PLACE, RobotController.PLACE_STEPS), new RobotController.Task(RobotController.TaskType.MOVE, 6), new RobotController.Task(RobotController.TaskType.PICKUP, RobotController.PICKUP_STEPS)};
			}
		}
		else {
			if (DriverStation.getInstance().getGameSpecificMessage().charAt(0) == 'L') {
				tasks = new RobotController.Task[] {new RobotController.Task(RobotController.TaskType.MOVE, 209), new RobotController.Task(RobotController.TaskType.ROTATE_R, 90), new RobotController.Task(RobotController.TaskType.MOVE, 133), new RobotController.Task(RobotController.TaskType.ROTATE_R, 90), new RobotController.Task(RobotController.TaskType.PLACE, RobotController.PLACE_STEPS), new RobotController.Task(RobotController.TaskType.MOVE, 6), new RobotController.Task(RobotController.TaskType.PICKUP, RobotController.PICKUP_STEPS)};
			}
			else {
				tasks = new RobotController.Task[] {new RobotController.Task(RobotController.TaskType.MOVE, 209), new RobotController.Task(RobotController.TaskType.ROTATE_L, 90), new RobotController.Task(RobotController.TaskType.MOVE, 133), new RobotController.Task(RobotController.TaskType.ROTATE_L, 90), new RobotController.Task(RobotController.TaskType.PLACE, RobotController.PLACE_STEPS), new RobotController.Task(RobotController.TaskType.MOVE, 6), new RobotController.Task(RobotController.TaskType.PICKUP, RobotController.PICKUP_STEPS)};
			}
		}*/

		
		
		station = DriverStation.getInstance();
		//RobotController.Task[] tasks = new RobotController.Task[] {new RobotController.Task(TaskType.MOVE, 3), new RobotController.Task(TaskType.PICKUP, RobotController.PICKUP_STEPS)};
		
		//autoController.ControllerInit();
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
		timer.reset();
		timer.start();
		SmartDashboard.putData(startposition);
		ahrs.reset();
		ahrs.resetDisplacement();
		ahrs.zeroYaw();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		
		switch (prefs.getString("START", "switch")) {
		case "left":
			if (timer.get()<prefs.getDouble("AUTO_TIME_SIDE", 3)) {
				drivetrain.drive.tankDrive(0.35, 0.36,false);
			}
			break;
		case "center":
			if (timer.get()<prefs.getDouble("AUTO_TIME", 2)) {
				drivetrain.drive.tankDrive(0.35, 0.36,false);
			}
			break;
		case "switch":
			if(prefs.getBoolean("TEST_AUTO", false)) {
				if ((station.getGameSpecificMessage().charAt(0)=='R')) {
					if (timer.get()<prefs.getDouble("AUTO_TIME", 2)) {
						drivetrain.drive.tankDrive(0.35, 0.36,false);
					} else if (station.getGameSpecificMessage().charAt(0)=='R') {
						grabber.leftArm.set(0.4);
						grabber.leftArm.set(0.4);
					}
				} else {
					double autospeed, autorotation;
					/*
					 * put the cool stuff here
					 * 
					 * */
					
					
					//  ]drivetrain.drive.arcadeDrive(autospeed, autorotation, false);
				}
			}else {
				if (timer.get()<prefs.getDouble("AUTO_TIME", 2)) {
					drivetrain.drive.tankDrive(0.37, 0.38,false);
				} else if (station.getGameSpecificMessage().charAt(0)=='R') {
					grabber.leftArm.set(0.4);
					grabber.leftArm.set(0.4);
				}
			}
			break;
		case "right":
			if (timer.get()<prefs.getDouble("AUTO_TIME_SIDE", 3)) {
				drivetrain.drive.tankDrive(0.35, 0.36,false);
			}
			break;
		}
		
		
		/*if (timer.get()<prefs.getDouble("AUTO_TIME", 2)) {
			drivetrain.drive.tankDrive(0.35, 0.36,false);
		} else if((startposition.getSelected() == "switch")&&(station.getGameSpecificMessage().charAt(0)=='R')) {
			grabber.leftArm.set(0.4);
			grabber.leftArm.set(0.4);
		}*/
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopInit() {  
		SmartDashboard.putNumber("SET_SERVO_please?", 0.6);
		ahrs.reset();
		ahrs.resetDisplacement();
		ahrs.zeroYaw();
	}
	@Override
	public void teleopPeriodic() {
		
		if(driverxbox.getBumperPressed(GenericHID.Hand.kLeft)) {
			if (currentupmode == Upmode.block ) {
				currentupmode = Upmode.me;
				lifter.lifter.set(0);
			} 
			else {
				currentupmode = Upmode.block;
				climber.climber.set(0);
			}
		}
		
		//turbo button
		if (driverxbox.getBumper(GenericHID.Hand.kRight)) {
			speed = prefs.getDouble("DRIVE_SPEED_FAST", 0.5);
		}
		else {
			speed = prefs.getDouble("DRIVE_SPEED_SLOW", 0.5);
		}
		/*if (manipxbox.getBumperPressed(GenericHID.Hand.kLeft)) {
			clawiskill = true;
			grabber.claw.free();
		} 
		if (manipxbox.getBumperPressed(GenericHID.Hand.kRight)) {
			grabber.claw.set(clawServoOpen ? prefs.getDouble("ARM_SERVO_OPEN", 1) : prefs.getDouble("ARM_SERVO_CLOSE", 0.6));
			clawServoOpen = !clawServoOpen;
		}*/
		//grabber.claw.set(manipxbox.getY(Generic));
		
		
		
		
		
		if (driverxbox.getBumper(GenericHID.Hand.kRight)||manipxbox.getBButton()) {
			grabber.leftArm.set(prefs.getDouble("ARM_SPEED_PULL", -0.5D));
			grabber.rightArm.set(prefs.getDouble("ARM_SPEED_PULL", -0.5D));
		}
		
		else if (manipxbox.getAButton()) {
			grabber.leftArm.set(prefs.getDouble("ARM_SPEED_SHOOT", 0.5));
			grabber.rightArm.set(prefs.getDouble("ARM_SPEED_SHOOT", 0.5));
		} else {
			grabber.leftArm.set(manipxbox.getY(GenericHID.Hand.kLeft)*-0.45);
			grabber.rightArm.set(manipxbox.getY(GenericHID.Hand.kRight)*-0.45);
		}
		
		
		
		
	
		
		if (currentupmode==Upmode.me) {
			
				climber.climber.set(driverxbox.getY(GenericHID.Hand.kRight));
			
		}
		//lifter code triggers on manipulating xbox
		if (manipxbox.getTriggerAxis(GenericHID.Hand.kRight)>0.55) {
			lifter.lifter.set((manipxbox.getTriggerAxis(GenericHID.Hand.kRight)-0.5D)*2D);
		}else if (manipxbox.getTriggerAxis(GenericHID.Hand.kLeft)>0.55) {
			lifter.lifter.set((manipxbox.getTriggerAxis(GenericHID.Hand.kLeft)-0.5D)*-2D);
		} else {
			lifter.lifter.set(0);
		}
	
		if(driverxbox.getYButtonPressed()) {
			arcademode = !arcademode;
		}
		if (currentupmode == Upmode.block) {
			if (arcademode)
				drivetrain.drive.arcadeDrive(driverxbox.getY(GenericHID.Hand.kLeft)*speed*-1D, driverxbox.getX(GenericHID.Hand.kLeft)*speed,false);
			else
				drivetrain.drive.tankDrive(driverxbox.getY(GenericHID.Hand.kLeft)*speed*-1D, driverxbox.getY(GenericHID.Hand.kRight)*speed*-1D,false);
		} else {
			drivetrain.drive.arcadeDrive(driverxbox.getY(GenericHID.Hand.kLeft)*speed*-1D, driverxbox.getX(GenericHID.Hand.kLeft)*speed,false);
		}
		SmartDashboard.putData("Drive", drivetrain.drive);
		SmartDashboard.putNumber("exper", drivetrain.drive.getExpiration());
		
		grabber.claw.set(SmartDashboard.getNumber("SET_SERVO_please?", 0.5));
		SmartDashboard.putNumber("servopos", grabber.claw.getPosition());
		SmartDashboard.putNumber("servo", grabber.claw.get());
		SmartDashboard.putNumber("yaw", ahrs.getYaw());
		
		SmartDashboard.putNumber("disx", ahrs.getDisplacementX());
		SmartDashboard.putNumber("disy", ahrs.getDisplacementY());
		SmartDashboard.putNumber("dizz", ahrs.getDisplacementZ());
	}
	public static Robot getInstance() {
		return instance;
	}

	/**
	 * This function is called periodically during test mode.
	 */
	
	@Override
	public void testPeriodic() {
		//SmartDashboard.putString("rs232",rs232.readString());
		grabber.claw.set(SmartDashboard.getNumber("SET_SERVO", 0.5));
		SmartDashboard.putNumber("servopos", grabber.claw.getPosition());
		SmartDashboard.putNumber("servo", grabber.claw.get());
	}
	@Override
	public void pidWrite(double pid) {
		autorotaterate = pid;
	}

}
