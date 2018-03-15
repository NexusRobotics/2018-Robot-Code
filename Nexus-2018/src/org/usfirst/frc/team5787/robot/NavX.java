package org.usfirst.frc.team5787.robot;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;

public class NavX extends AHRS {
	public NavX () {
		super(SPI.Port.kMXP);
	}
}
