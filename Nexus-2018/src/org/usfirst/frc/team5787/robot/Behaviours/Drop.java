package org.usfirst.frc.team5787.robot.Behaviours;

import org.usfirst.frc.team5787.robot.Robot;
import org.usfirst.frc.team5787.robot.Sensor;
import org.usfirst.frc.team5787.robot.subsystems.Grabber;

public class Drop extends Behaviour {
	Grabber grabber;
	public Drop(Goal goal, Sensor sensor) {
		super(goal, sensor);
		grabber = Robot.instance.grabber;
	}

	@Override
	public Behaviour nextBehaviour() {
		return this;
	}

	@Override
	public void run() {
		grabber.release();
	}

}
