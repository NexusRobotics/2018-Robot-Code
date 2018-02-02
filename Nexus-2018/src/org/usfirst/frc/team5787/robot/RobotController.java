package org.usfirst.frc.team5787.robot;

import java.util.ArrayList;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class RobotController {
	public enum TaskType{
		MOVE, MOVE_TO, ROTATE_L, ROTATE_R, PICKUP, PLACE
	};
	public RobotController(DifferentialDrive drive, ArrayList<Task> taskQueue, AHRS ahrs) {
		this.drive = drive;
		this.taskQueue = taskQueue;
		this.ahrs = ahrs;
	}
	public DifferentialDrive drive;
	public float taskProgress = 0;
	public float prevValue;
	public AHRS ahrs;
	public ArrayList<Task> taskQueue = new ArrayList<Task>();
	public void update() {
		if (taskProgress <= 0) {
			drive.tankDrive(0, 0);
			taskQueue.remove(0);
			taskProgress = taskQueue.get(0).amount;
			
		}
		Task task = taskQueue.get(0);
		if (task.type == TaskType.MOVE) {
			drive.tankDrive(0.3, 0.3);
			taskProgress -= Math.abs(prevValue - Math.sqrt(Math.pow(ahrs.getDisplacementX(), 2) + Math.pow(ahrs.getDisplacementZ(), 2)));
			prevValue = (float)Math.sqrt(Math.pow(ahrs.getDisplacementX(), 2) + Math.pow(ahrs.getDisplacementZ(), 2));
		}
		else if (task.type == TaskType.ROTATE_L) {
			drive.tankDrive(-0.3, 0.3);
			taskProgress -= Math.abs(prevValue - ahrs.getYaw());
			prevValue = ahrs.getYaw();
		}
		else if (task.type == TaskType.ROTATE_R) {
			drive.tankDrive(0.3, -0.3);
			taskProgress -= Math.abs(prevValue - ahrs.getYaw());
			prevValue = ahrs.getYaw();
		}
		else if (task.type == TaskType.MOVE_TO) {
			drive.tankDrive(0.3, 0.3);
			taskProgress = getSensorProximity() - task.amount;
		}
	}
	public int getSensorProximity() {
		return 0;
	}
	public class Task{
		public TaskType type;
		public float amount;
		public Task(TaskType type, float amount) {
			this.type = type;
			this.amount = amount;
		}
	}
}
