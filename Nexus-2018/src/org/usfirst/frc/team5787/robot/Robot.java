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
import org.usfirst.frc.team5787.robot.Robotmap;
import org.usfirst.frc.team5787.robot.subsystems.*;
import java.util.ArrayList;
import java.util.Arrays;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import org.usfirst.frc.team5787.robot.Behaviours.*;





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
	
	public static Robot instance;
	
	private Preferences prefs = Preferences.getInstance();
	//private SerialPort rs232;
	private double autorotaterate;
	private DriverStation station;
	UsbCamera camera;
	private Timer timer = new Timer();
	private SendableChooser<String> startposition = new SendableChooser<String>();
	private XboxController driverxbox, manipxbox;
	private boolean arcademode = true;
	private double speed = 0.3D;
	private AHRS ahrs = new AHRS(SPI.Port.kMXP);;
	public final Drivetrain drivetrain = new Drivetrain(prefs.getBoolean("IS_PRACTICE_ROBOT", IS_PRACTICE_ROBOT));
	public final Grabber grabber = new Grabber(prefs.getBoolean("IS_PRACTICE_ROBOT", IS_PRACTICE_ROBOT));
	public final Climber climber = new Climber();
	public final Lifter lifter = new Lifter();
	private enum Upmode{me, block}
	enum Behave{Roam, Drop, Align, Break, Raise}
	double autospeed = 0.4, autorotation = 0;
	private SpeedControllerGroup leftGroup, rightGroup;
	private Behaviour currentBehaviour;
	private Upmode currentupmode= Upmode.block;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		instance = this;
		ahrs.resetDisplacement();
		ahrs.reset();
		ahrs.getYaw();
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(247, 180);
		camera.setFPS(15);
		currentBehaviour = new Lift(new Goal(0),new Sensor(ahrs));
		
		driverxbox = new XboxController(0);
		manipxbox = new XboxController(1);
		//rs232 = new SerialPort(115200, SerialPort.Port.kOnboard);
		
		
		station = DriverStation.getInstance();
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
		ahrs.reset();
		ahrs.resetDisplacement();
		ahrs.zeroYaw();
		if (prefs.getString("START", "switch") == "switch") {
			currentBehaviour = new VicStyle(new Goal(0),new Sensor(ahrs));
		}
		if (prefs.getString("START", "switch") == "rotate") {
			currentBehaviour = new Rotate(new Goal(90),new Sensor(ahrs));
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		
		switch (prefs.getString("START", "switch")) {
		case "gravy":
			currentBehaviour = currentBehaviour.nextBehaviour();
			if (currentBehaviour != null) {
				currentBehaviour.run();
				SmartDashboard.putString("behaviour", currentBehaviour.toString());
			} else {
				SmartDashboard.putString("behaviour", "null");
			}
			SmartDashboard.putBoolean("attarget?", lifter.atTarget());
		case "lift":
			lifter.raiseToSwitch();
			break;
		case "test":
			currentBehaviour = currentBehaviour.nextBehaviour();
			if (currentBehaviour != null) {
				currentBehaviour.run();
				SmartDashboard.putString("behaviour", currentBehaviour.toString());
			} else {
				SmartDashboard.putString("behaviour", "null");
			}
			SmartDashboard.putBoolean("attarget?", lifter.atTarget());
			break;
		case "left":
			if (timer.get()<prefs.getDouble("AUTO_TIME_SIDE", 3)) {
				drivetrain.drive.tankDrive(0.35, 0.36,false);
			}
			break;
		case "center":
			if(prefs.getBoolean("TEST_AUTO", false)) {
				Behave job = Behave.Raise;
				
				switch (job) {
				case Raise:
					lifter.raiseToSwitch();
					if (lifter.atTarget()) {
						job = Behave.Roam;
					}
					break;
				case Roam:
					drivetrain.drive(autospeed);
					if ((timer.get()>0.5)&&(timer.get()<1))
						job = Behave.Break;
				case Align:
					if (Math.abs(ahrs.getYaw())<10) {
						drivetrain.autospeed = 0;
						if (station.getGameSpecificMessage().charAt(0)=='R') {
							drivetrain.drive(0, autorotation );
						}
					} else if (Math.abs(ahrs.getYaw())>38&&Math.abs(ahrs.getYaw())<52) {
						
					}
					break;
				case Drop:
					break;
				case Break:
					drivetrain.stop();
					job = Behave.Align;
					break;
				}
			}
			else if (timer.get()<prefs.getDouble("AUTO_TIME", 2)) {
				drivetrain.drive.tankDrive(0.35, 0.36,false);
			}
			break;
		case "switch":
			currentBehaviour = currentBehaviour.nextBehaviour();
			if (currentBehaviour != null) {
				currentBehaviour.run();
				SmartDashboard.putString("behaviour", currentBehaviour.toString());
			} else {
				SmartDashboard.putString("behaviour", "null");
			}
			SmartDashboard.putBoolean("attarget?", lifter.atTarget());
			
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
				//lifter.lifter.set(0);
			} 
			else {
				currentupmode = Upmode.block;
				//climber.climber.set(0);
			}
		}
		
		//turbo button
		if (driverxbox.getBumper(GenericHID.Hand.kRight)) {
			speed = prefs.getDouble("DRIVE_SPEED_FAST", 0.5);
		}
		else {
			speed = prefs.getDouble("DRIVE_SPEED_SLOW", 0.5);
		}
		
		
		
		
		if (driverxbox.getBumper(GenericHID.Hand.kRight)||manipxbox.getBButton()) {
			grabber.leftArm.set(prefs.getDouble("ARM_SPEED_PULL", -0.5D));
			grabber.rightArm.set(prefs.getDouble("ARM_SPEED_PULL", -0.5D));
		}
		
		else if (manipxbox.getAButton()) {
			grabber.leftArm.set(prefs.getDouble("ARM_SPEED_SHOOT", 0.5));
			grabber.rightArm.set(prefs.getDouble("ARM_SPEED_SHOOT", 0.5));
		} else {
			grabber.leftArm.set(manipxbox.getY(GenericHID.Hand.kLeft)*-0.6);
			grabber.rightArm.set(manipxbox.getY(GenericHID.Hand.kRight)*-0.6);
		}
		
		
		
		
	
		
		if (currentupmode==Upmode.me) {
			
				climber.climber.set(driverxbox.getY(GenericHID.Hand.kRight));
			
		}
		//lifter code triggers on manipulating xbox
		if (manipxbox.getTriggerAxis(GenericHID.Hand.kRight)>0.55) {
			lifter.lifter.set((manipxbox.getTriggerAxis(GenericHID.Hand.kRight)-0.5D)*2D);
		}else if (manipxbox.getTriggerAxis(GenericHID.Hand.kLeft)>0.55) {
			lifter.lifter.set((manipxbox.getTriggerAxis(GenericHID.Hand.kLeft)-0.5D)*-2D);
		} else if (driverxbox.getTriggerAxis(GenericHID.Hand.kRight)>0.55) {
			lifter.lifter.set((driverxbox.getTriggerAxis(GenericHID.Hand.kRight)-0.5D)*2D);
		}else if (driverxbox.getTriggerAxis(GenericHID.Hand.kLeft)>0.55) {
			lifter.lifter.set((driverxbox.getTriggerAxis(GenericHID.Hand.kLeft)-0.5D)*-2D);
		} else 
		{
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
			drivetrain.drive.arcadeDrive(driverxbox.getY(GenericHID.Hand.kLeft)*speed, driverxbox.getX(GenericHID.Hand.kLeft)*speed,false);
		}
		SmartDashboard.putData("Drive", drivetrain.drive);
		SmartDashboard.putNumber("exper", drivetrain.drive.getExpiration());
		
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
		/*grabber.claw.set(SmartDashboard.getNumber("SET_SERVO", 0.5));
		SmartDashboard.putNumber("servopos", grabber.claw.getPosition());
		SmartDashboard.putNumber("servo", grabber.claw.get());*/
	}
	@Override
	public void pidWrite(double pid) {
		autorotaterate = pid;
	}

}
