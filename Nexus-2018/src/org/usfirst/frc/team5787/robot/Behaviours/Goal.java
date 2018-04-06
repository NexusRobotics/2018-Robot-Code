package org.usfirst.frc.team5787.robot.Behaviours;

public class Goal {
	public double yaw, pos;
	public Goal nextGoal = null;
	
	public Goal(double yaw) {
		this.yaw = yaw;
		this.pos = 0;
	}
	public Goal(double yaw, double pos) {
		this.yaw = yaw;
		this.pos = pos;
	}
	
	public Goal(double yaw, double pos, Goal nextGoal) {
		this.yaw = yaw;
		this.pos = pos;
		this.nextGoal = nextGoal;
	}
}
