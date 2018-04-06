package org.usfirst.frc.team5787.robot.Behaviours;
import org.usfirst.frc.team5787.robot.Robot;
import org.usfirst.frc.team5787.robot.Sensor;
import org.usfirst.frc.team5787.robot.subsystems.*;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
public class RoamAtAngle extends Behaviour implements PIDOutput {
	private double lastcurrent;
	PowerDistributionPanel pdp = new PowerDistributionPanel();
	Drivetrain drivetrain;
	Timer timer;
	PIDController turnController;
public RoamAtAngle(Goal goal, Sensor sensor) {
		super(goal, sensor);
		drivetrain = Robot.instance.drivetrain;
		timer = new Timer();
		timer.reset();
		timer.start();
		lastcurrent = pdp.getTotalCurrent();
		turnController = new PIDController(0.01, 0, 0, sensor.ahrs, drivetrain);
		turnController.setInputRange(-180.0f, 180.0f);
		turnController.setOutputRange(-1, 1);;
		turnController.setContinuous(true);
		turnController.setAbsoluteTolerance(2.0f);
		turnController.enable();
	}

@Override
	public Behaviour nextBehaviour() {
	if (goal.pos<timer.get()) {
		turnController.disable();
		drivetrain.drive(0);
		if (goal.nextGoal != null) {
			return new RoamAtAngle(goal.nextGoal,sensor);
		}
		switch ((int)goal.yaw) {
		case 0:
			return new Break(new Goal(45), sensor);
		case 45:
			return new Break(new Goal(0), sensor);
		default:
			return this;
		}
	} else if(contact()) {
		return new Drop(new Goal(0), sensor);
	} else
		return this;
	}

	@Override
	public void run() {
		

	}
	private boolean contact() {
		if(pdp.getTotalCurrent()-lastcurrent>5) {
			return true;
		} else {
			lastcurrent = pdp.getTotalCurrent();
			return false;
		}
	}

	@Override
	public void pidWrite(double output) {
		drivetrain.drive(0.4, output);
		
	}

}
