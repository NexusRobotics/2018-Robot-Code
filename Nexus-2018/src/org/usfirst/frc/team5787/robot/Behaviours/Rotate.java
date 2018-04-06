package org.usfirst.frc.team5787.robot.Behaviours;

import org.usfirst.frc.team5787.robot.Sensor;
import org.usfirst.frc.team5787.robot.Sensor;
import org.usfirst.frc.team5787.robot.Robot;
import org.usfirst.frc.team5787.robot.Sensor;
import org.usfirst.frc.team5787.robot.subsystems.*;

import edu.wpi.first.wpilibj.PIDController;
public class Rotate extends Behaviour {
	
	Drivetrain drivetrain;
	PIDController turnController;
	public Rotate(Goal goal, Sensor sensor) {
		super(goal, sensor);
		drivetrain = Robot.instance.drivetrain;
		turnController = new PIDController(0.01, 0, 0, sensor.ahrs, drivetrain);
		turnController.setInputRange(-180.0f, 180.0f);
		turnController.setOutputRange(-1, 1);;
		turnController.setContinuous(true);
		turnController.setAbsoluteTolerance(2.0f);
		turnController.enable();
	}

	@Override
	public Behaviour nextBehaviour() {
		if (turnController.onTarget()) {
			turnController.disable();
			turnController.free();
			if (goal.pos == 45) {
				return new RoamAtAngle(new Goal(goal.yaw,0.8),sensor);
			} else {
				return new RoamAtAngle(new Goal(goal.yaw,1),sensor);
			}
		}else
			return this;
	}

	@Override
	public void run() {
		turnController.setSetpoint(goal.yaw);
		
	}

}
