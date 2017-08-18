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
	
	
	public static volatile boolean objectDetected = false;
	public static MoveThread moveThread;
	public static DetectThread detectThread;
	public static int mThreadCount = 0;
	public static int dThreadCount = 0;

	public static void main(String[] args){
		EV3 ev3brick = (EV3) BrickFinder.getLocal();
		Keys buttons = ev3brick.getKeys();
		
		while(buttons.getButtons() != Keys.ID_ESCAPE){
			while(mThreadCount==0){
				moveThread = new MoveThread();
				moveThread.start();
				mThreadCount++;
			}
			while(dThreadCount==0){
				detectThread = new DetectThread();
				detectThread.start();
				dThreadCount++;
			}
		}
	}
}

//-------------------------------------------------------------------------

class MoveThread extends Thread{

	public EV3LargeRegulatedMotor LEFT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.B);
	public EV3LargeRegulatedMotor RIGHT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.C);

	public EV3 ev3brick = (EV3) BrickFinder.getLocal();
	public TextLCD lcddisplay = ev3brick.getTextLCD();
	

	public void run() {
		lcddisplay.drawString("MoveThread gestartet", 0, 0);
		do{
			LEFT_MOTOR.forward();
			RIGHT_MOTOR.forward();

			//		if(Bewegung.objectDetected){
			//			LEFT_MOTOR.stop();
			//			RIGHT_MOTOR.stop();
		}while(Action.objectDetected == false);

		LEFT_MOTOR.stop();
		RIGHT_MOTOR.stop();
		RIGHT_MOTOR.rotate(360);
		Action.mThreadCount = 0;
	}

}

//------------------------------------------------------------------------

class DetectThread extends Thread{

	public EV3 ev3brick = (EV3) BrickFinder.getLocal();
	public TextLCD lcddisplay = ev3brick.getTextLCD();
	public Keys buttons = ev3brick.getKeys();

	public Port portS4 = ev3brick.getPort("S4");

	public EV3UltrasonicSensor sonicSensor = new EV3UltrasonicSensor(portS4);

	public SampleProvider sonicdistance = sonicSensor.getDistanceMode();

	public float[] sampleUltraSonic = new float[sonicdistance.sampleSize()];


	public void run() {
		lcddisplay.drawString("DetectThread gestartet", 0, 0);
		sonicdistance.fetchSample(sampleUltraSonic,0);

		if(sampleUltraSonic[0] < 2.5){
			Action.objectDetected = true;
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			Action.objectDetected = false;
		}
		Action.dThreadCount = 0;
	}

}



