package org.usfirst.frc.team5787.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.*;
public class Sensor {
	private int leftUltra, rightUltra;
	public AHRS ahrs;
	private double cubeProx;

	public Sensor(AHRS ahrs) {
		this.ahrs = ahrs;
	}
	public int getLeftUltra() {
		return leftUltra;
	}
	
	public int getRightUltra() {
		return rightUltra;
	}
	
	public double getCubeProx() {
		return cubeProx;
	}
}
