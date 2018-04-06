package org.usfirst.frc.team5787.robot.Behaviours;
import org.usfirst.frc.team5787.robot.Robot;
import org.usfirst.frc.team5787.robot.Sensor;
import org.usfirst.frc.team5787.robot.subsystems.*;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;

public class Lift extends Behaviour {
	private DriverStation station;
	Lifter lifter;
	Preferences prefs;
	public Lift(Goal goal, Sensor sensor) {
		super(goal, sensor);
		lifter = Robot.instance.lifter;
		prefs = Preferences.getInstance();
	}

	@Override
	public Behaviour nextBehaviour() {
		if (lifter.atTarget()) {
			if (prefs.getString("START", "switch") == "gravy") {
				if (station.getGameSpecificMessage().charAt(0)=='R')
					return new RoamAtAngle(new Goal(0,0.1,new Goal(45,0.4,new Goal(0,1))),sensor);
				else 
					return new RoamAtAngle(new Goal(0,0.1,new Goal(-45,0.4,new Goal(0,1))),sensor);
			} else return new RoamAtAngle(new Goal(0,0.1),new Sensor(sensor.ahrs));
		} else 
			return this;
	}

	@Override
	public void run() {
		lifter.raiseToSwitch();
	}

}
