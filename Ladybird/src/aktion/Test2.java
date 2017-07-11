package aktion;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class Test2 {
	
	public static void main(String[] args){
		
		EV3LargeRegulatedMotor LEFT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.B);
		EV3LargeRegulatedMotor RIGHT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.C);
		
		EV3 ev3brick = (EV3) BrickFinder.getLocal();
		
		Keys buttons = ev3brick.getKeys();
		
		buttons.waitForAnyPress();
		
		LEFT_MOTOR.forward();
		RIGHT_MOTOR.forward();
		LCD.drawString("FORWARD", 0, 0);
		
		buttons.waitForAnyPress();
		
		LEFT_MOTOR.backward();
		RIGHT_MOTOR.backward();
		LCD.drawString("BACKWARD", 0, 0);
		
		buttons.waitForAnyPress();
		
		LEFT_MOTOR.stop();
		RIGHT_MOTOR.stop();
		
		LCD.drawString("STOP", 0, 0);
		
		buttons.waitForAnyPress();
	}

}
