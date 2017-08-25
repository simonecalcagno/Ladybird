package aktion;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Action{

	public static EV3LargeRegulatedMotor LEFT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.B);
	public static EV3LargeRegulatedMotor RIGHT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.C);

	public static EV3 ev3brick = (EV3) BrickFinder.getLocal();
	public static TextLCD lcddisplay = ev3brick.getTextLCD();
	public static Keys buttons = ev3brick.getKeys();

	public static Port portS4 = ev3brick.getPort("S4");
	public static EV3UltrasonicSensor sonicSensor = new EV3UltrasonicSensor(portS4);

	public static EV3ColorSensor colorSense = new EV3ColorSensor(SensorPort.S1);

	public static volatile boolean objectDetected = false;
	public static volatile boolean ladybirdDetected = false;
	public static MoveThread moveThread;
	public static DetectThread detectThread;
	public static LadybirdThread ladybirdThread;
	public static int mThreadCount = 0;
	public static int dThreadCount = 0;
	public static int lThreadCount = 0;
	public static int search = 3;

	public static void main(String[] args){

		while(buttons.getButtons() != Keys.ID_ESCAPE){
			if(dThreadCount==0){
				detectThread = new DetectThread();
				detectThread.start();
			}
			if(lThreadCount==0){
				ladybirdThread = new LadybirdThread();
				ladybirdThread.start();

			}
			if(mThreadCount==0){
				moveThread = new MoveThread();
				moveThread.start();
			}
		}
	}
}

//-------------------------------------------------------------------------

class MoveThread extends Thread{
	public void run() {
		Action.mThreadCount++;
		while(Action.ladybirdDetected == false && Action.objectDetected == false){
			Action.LEFT_MOTOR.forward();
			Action.RIGHT_MOTOR.forward();
		}

		if(Action.objectDetected){
			Action.LEFT_MOTOR.stop();
			Action.RIGHT_MOTOR.stop();
			Action.RIGHT_MOTOR.rotateTo(360);
			Action.RIGHT_MOTOR.stop();
		}if(Action.ladybirdDetected){
			Action.LEFT_MOTOR.stop();
			Action.RIGHT_MOTOR.stop();
			Action.buttons.waitForAnyPress();
			Action.ladybirdDetected = false;
		}
		Action.mThreadCount = 0;
	}
}

//------------------------------------------------------------------------

class DetectThread extends Thread{
	public void run() {
		Action.dThreadCount++;
		SampleProvider sonicdistance = Action.sonicSensor.getDistanceMode();
		float[] sampleUltraSonic = new float[sonicdistance.sampleSize()];

		while(Action.objectDetected == false){
			sonicdistance.fetchSample(sampleUltraSonic,0);

			if(sampleUltraSonic[0] < 0.1){
				Action.objectDetected = true;

			}else{
				Action.objectDetected = false;
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Action.objectDetected = false;
		Action.dThreadCount = 0;
	}

}


//-------------------------------------------------------------------------
class LadybirdThread extends Thread{
	public void run(){
		Action.lThreadCount++;
		Action.colorSense.setFloodlight(false);

		while(Action.ladybirdDetected==false){
			int found = Action.colorSense.getColorID();

			if(found==Action.search){
				Action.ladybirdDetected = true;
				Action.lcddisplay.drawString("Ladybird detected",0,0);
			}
		}
		Action.lThreadCount = 0;
	}
}

