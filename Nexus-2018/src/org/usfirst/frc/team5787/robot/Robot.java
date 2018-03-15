/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5787.robot;

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
public class Robot extends IterativeRobot{
	public static final boolean IS_PRACTICE_ROBOT = false;
	private enum Automode{
		defaultauto, drivestraight, testauto
	}
	
	private static Robot instance;
	
	private Preferences prefs = Preferences.getInstance();
	//private SerialPort rs232;
	private DriverStation station;
	private boolean clawServoOpen = true;
	private boolean clawiskill = false;
	private int clawrevivecount = 0;
	private SendableChooser<Boolean> drive_chooser = new SendableChooser<>();
	private SendableChooser<Automode> automode = new SendableChooser<>(); 
	private XboxController driverxbox, manipxbox;
	private boolean arcademode = false;
	private AnalogInput ultrasonic;
	private double speed = 0.3D;
	private RobotController autoController;
	private AHRS ahrs;
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
		drive_chooser.addDefault("Tank Drive", Boolean.FALSE);
		drive_chooser.addObject("Arcade Drive", Boolean.TRUE);
		SmartDashboard.putData("Drive Mode", drive_chooser);
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
		
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		drivetrain.drive.tankDrive(0.03, 0.03, false);
		//drive.tankDrive(1D, 0, false);
	}

	/**
	 * This function is called periodically during operator control.
	 */
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
			speed = prefs.getDouble("DRIVE_SPEED_FAST", 1);
		}
		else {
			speed = prefs.getDouble("DRIVE_SPEED_SLOW", 0.3D);
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
		
		if (driverxbox.getBumper(GenericHID.Hand.kRight)) {
			grabber.leftArm.set(prefs.getDouble("ARM_SPEED_PULL", -0.5D));
			grabber.rightArm.set(prefs.getDouble("ARM_SPEED_PULL", -0.5D));
		}
		
		else if (manipxbox.getAButton()) {
			grabber.leftArm.set(prefs.getDouble("ARM_SPEED_SHOOT", 1));
			grabber.rightArm.set(prefs.getDouble("ARM_SPEED_SHOOT", 1));
		}
		else {
			grabber.leftArm.set(prefs.getDouble("ARM_SPEED_STATIC",-0.1 ));
			grabber.rightArm.set(prefs.getDouble("ARM_SPEED_STATIC",-0.1 ));
		}
		
		if (currentupmode==Upmode.me) {
			climber.climber.set(driverxbox.getY(GenericHID.Hand.kRight)*-1);
		} else if (currentupmode==Upmode.block){
		lifter.lifter.set(manipxbox.getY(GenericHID.Hand.kLeft)*-1);
		} else{
			lifter.lifter.set(0);
			climber.climber.set(0);
		}
		if(driverxbox.getYButtonPressed()) {
			arcademode = !arcademode;
		}
		if (currentupmode == Upmode.block) {
			if (arcademode)
				drivetrain.drive.arcadeDrive(driverxbox.getY(GenericHID.Hand.kLeft)*speed*-1D, driverxbox.getX(GenericHID.Hand.kRight)*speed,false);
			else
				drivetrain.drive.tankDrive(driverxbox.getY(GenericHID.Hand.kLeft)*speed*-1D, driverxbox.getY(GenericHID.Hand.kRight)*speed*-1D,false);
		} else {
			drivetrain.drive.arcadeDrive(driverxbox.getY(GenericHID.Hand.kLeft)*speed*-1D, driverxbox.getX(GenericHID.Hand.kLeft)*speed,false);
		}
		SmartDashboard.putData("Drive", drivetrain.drive);
		SmartDashboard.putNumber("exper", drivetrain.drive.getExpiration());
		SmartDashboard.putNumber("servopos", grabber.claw.getPosition());
		SmartDashboard.putNumber("servo", grabber.claw.get());
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
	}

}
