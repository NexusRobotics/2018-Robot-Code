package org.usfirst.frc.team5787.robot.Behaviours;

import org.usfirst.frc.team5787.robot.Robot;
import org.usfirst.frc.team5787.robot.Sensor;
import org.usfirst.frc.team5787.robot.subsystems.Drivetrain;
import org.usfirst.frc.team5787.robot.subsystems.Grabber;
import org.usfirst.frc.team5787.robot.subsystems.Lifter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;

public class VicStyle extends Behaviour {
	Preferences prefs;
	Timer timer;
	Lifter lifter;
	Drivetrain drivetrain;
	Grabber grabber;
	private DriverStation station;
	public VicStyle(Goal goal, Sensor sensor) {
		super(goal, sensor);
		timer = new Timer();
		timer.reset();
		timer.start();
		
		lifter = Robot.instance.lifter;
		drivetrain = Robot.instance.drivetrain;
		station = DriverStation.getInstance();
		grabber = Robot.instance.grabber;
		prefs = Preferences.getInstance();
	}

	@Override
	public Behaviour nextBehaviour() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void run() {
		lifter.raiseToSwitch();
		if (timer.get()<prefs.getDouble("AUTO_TIME", 1.8)){
			drivetrain.drive(prefs.getDouble("AUTO_SPEED", 0.4));
		} else if (station.getGameSpecificMessage().charAt(0)=='R') {
			grabber.release();
		}
		
	}

}
