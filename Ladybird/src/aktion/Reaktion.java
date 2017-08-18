package aktion;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Reaktion {

	public Reaktion() {
		// TODO Auto-generated constructor stub
	}

	//gibt true zurück wenn ein Objekt entdeckt wurde
	public static boolean detectObject(){

		EV3 ev3brick = (EV3) BrickFinder.getLocal();
		TextLCD lcddisplay = ev3brick.getTextLCD();
		Keys buttons = ev3brick.getKeys();

		buttons.waitForAnyPress();

		Port portS4 = ev3brick.getPort("S4");

		EV3UltrasonicSensor sonicSensor = new EV3UltrasonicSensor(portS4);

		SampleProvider sonicdistance = sonicSensor.getDistanceMode();

		float[] sampleUltraSonic = new float[sonicdistance.sampleSize()];

		sonicdistance.fetchSample(sampleUltraSonic,0);

		if(sampleUltraSonic[0] < 2.5){
			return true;
		}else{
			return false;
		}
	}

}
