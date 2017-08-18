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

	public static volatile boolean objectDetected = false;
	public static MoveThread moveThread;
	public static DetectThread detectThread;
	public static int mThreadCount = 0;
	public static int dThreadCount = 0;

	public static void main(String[] args){

		while(buttons.getButtons() != Keys.ID_ESCAPE){
			if(dThreadCount==0){
				detectThread = new DetectThread();
				detectThread.start();
				dThreadCount++;
			}
			if(mThreadCount==0){
				moveThread = new MoveThread();
				moveThread.start();
				mThreadCount++;
			}
			
		}
	}
}

//-------------------------------------------------------------------------

class MoveThread extends Thread{
	public void run() {
		Action.lcddisplay.drawString("MoveThread gestartet", 0, 5);
		while(Action.objectDetected == false){
			Action.LEFT_MOTOR.forward();
			Action.RIGHT_MOTOR.forward();
		}
		//		if(Bewegung.objectDetected){
		//			LEFT_MOTOR.stop();
		//			RIGHT_MOTOR.stop();

		Action.LEFT_MOTOR.stop();
		Action.RIGHT_MOTOR.rotateTo(360);
		Action.RIGHT_MOTOR.stop();
		Action.mThreadCount = 0;
		Action.lcddisplay.clear();
		Action.lcddisplay.drawInt(Action.mThreadCount,1,1);
		
	}

}

//------------------------------------------------------------------------

class DetectThread extends Thread{
	public void run() {

		SampleProvider sonicdistance = Action.sonicSensor.getDistanceMode();
		float[] sampleUltraSonic = new float[sonicdistance.sampleSize()];
		Action.lcddisplay.drawString("DetectThread gestartet", 0, 0);

		while(Action.objectDetected == false){
			sonicdistance.fetchSample(sampleUltraSonic,0);

			if(sampleUltraSonic[0] < 2.5){
				Action.objectDetected = true;

			}else{
				Action.objectDetected = false;
			}
		}
		Action.dThreadCount = 0;
		Action.lcddisplay.drawString("det"+String.valueOf(Action.dThreadCount), 1, 1);
	}

}



