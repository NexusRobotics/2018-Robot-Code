package org.usfirst.frc.team5787.robot;

import java.util.ArrayList;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class RobotController {
	public enum TaskType{
		MOVE, MOVE_TO, ROTATE_L, ROTATE_R, PICKUP, PLACE
	};
	public RobotController(DifferentialDrive drive, SpeedController rightArm, SpeedController lifter, Servo claw, ArrayList<Task> taskQueue, AHRS ahrs) {
		this.drive = drive;
		this.claw = claw;
		this.taskQueue = taskQueue;
		this.ahrs = ahrs;
		this.arms = rightArm;
		this.lifter = lifter;
		
	}
	public SpeedController arms, lifter;
	public DifferentialDrive drive;
	public Servo claw;
	public AnalogInput ultrasonic;
	public float taskProgress = 0;
	public float prevValue;
	public static final int PICKUP_STEPS = 20;
	public static final int PLACE_STEPS = 21;
	public AHRS ahrs;
	public ArrayList<Task> taskQueue = new ArrayList<Task>();
	private Preferences prefs;
	public static final int MAX_PROXIMITY = 200;
	
	public void ControllerInit() {
		prefs = Preferences.getInstance();
	}
	public void update() {
		if (taskProgress <= 0) {
			drive.tankDrive(0, 0);
			taskQueue.remove(0);
			taskProgress = taskQueue.get(0).amount;
			
		}
		Task task = taskQueue.get(0);
		switch (task.type) {
		case MOVE:
			
			drive.tankDrive(0.3, 0.3);
			taskProgress -= Math.abs(prevValue - Math.sqrt(Math.pow(ahrs.getDisplacementX(), 2) + Math.pow(ahrs.getDisplacementZ(), 2)));
			prevValue = (float)Math.sqrt(Math.pow(ahrs.getDisplacementX(), 2) + Math.pow(ahrs.getDisplacementZ(), 2));
			break;
		case MOVE_TO:
			drive.tankDrive(0.3, 0.3);
			taskProgress = getSensorProximity() - task.amount;
			break;
		case ROTATE_L:
			drive.tankDrive(-0.3, 0.3);
			taskProgress -= Math.abs(prevValue - ahrs.getYaw());
			prevValue = ahrs.getYaw();
			break;
		case ROTATE_R:
			drive.tankDrive(0.3, -0.3);
			taskProgress -= Math.abs(prevValue - ahrs.getYaw());
			prevValue = ahrs.getYaw();
			break;
		case PICKUP:
			if (taskProgress == PICKUP_STEPS) {
				taskProgress--;
				claw.set(0.6);
			}
			else if (taskProgress > PICKUP_STEPS - 5) {
				taskProgress--;
				arms.set(1);
			}
			else if (taskProgress > 0) {
				taskProgress--;
				lifter.set(0.1);
				
			}
			
			
			break;
		case PLACE:
			if (taskProgress > 16) {
				arms.set(-1);
			}
			
			else if (taskProgress > 0) {
				taskProgress--;
				lifter.set(-0.1);
			}
			if (taskProgress == 16) {
				claw.set(1);
			}
			break;
		}
	}
	public final double SUPPLIED_VOLTAGE = 12.0;
	public final double SCALE_FACTOR = (SUPPLIED_VOLTAGE / 1024) * 5;
	public int getSensorProximity() {
		return (int)(ultrasonic.getVoltage() / SCALE_FACTOR);
	}
	public static class Task{
		public TaskType type;
		public float amount;
		public Task(TaskType type, float amount) {
			this.type = type;
			this.amount = amount;
		}
	}
}
