package org.usfirst.frc.team5787.robot.Behaviours;

import org.usfirst.frc.team5787.robot.Sensor;

public abstract class Behaviour {
	Goal goal;
	Sensor sensor;
	public Behaviour(Goal goal, Sensor sensor) {
		this.goal = goal;
		this.sensor = sensor;
	}
	abstract public Behaviour nextBehaviour();
	abstract public void run();
}
