package org.usfirst.frc.team5787.robot.Behaviours;

import org.usfirst.frc.team5787.robot.Sensor;
import org.usfirst.frc.team5787.robot.Robot;
import org.usfirst.frc.team5787.robot.Sensor;
import org.usfirst.frc.team5787.robot.subsystems.*;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class Break extends Behaviour {
	private double lastcurrent;
	PowerDistributionPanel pdp = new PowerDistributionPanel();
	Drivetrain drivetrain;
	public Break(Goal goal, Sensor sensor) {
		super(goal, sensor);
		drivetrain = Robot.instance.drivetrain;
		lastcurrent = pdp.getTotalCurrent();
	}

	@Override
	public Behaviour nextBehaviour() {
		if (contact()) {
			return new Drop(new Goal(0), sensor);
		} else return new Rotate(goal, sensor);
	}

	@Override
	public void run() {
		drivetrain.stop();
	}
	
	private boolean contact() {
		if(pdp.getTotalCurrent()-lastcurrent>5) {
			return true;
		} else {
			lastcurrent = pdp.getTotalCurrent();
			return false;
		}
	}

}
