package aktion;

import java.io.File;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Keys;
import lejos.hardware.Sound;
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
	
	public static EV3 ev3brick = (EV3) BrickFinder.getLocal();
	public static TextLCD lcddisplay = ev3brick.getTextLCD();
	public static Keys buttons = ev3brick.getKeys();
	
	public static File screamFile = new File("ScreamSound.wav");
	public static File oopsFile = new File("OopsSound.wav");

	public static EV3LargeRegulatedMotor LEFT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.B);
	public static EV3LargeRegulatedMotor RIGHT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.C);

	public static EV3UltrasonicSensor sonicSensor = new EV3UltrasonicSensor(SensorPort.S4);
	public static EV3ColorSensor colorSense = new EV3ColorSensor(SensorPort.S1);

	public static volatile boolean objectDetected = false;
	public static volatile boolean ladybirdDetected = false;
	
	public MoveThread moveThread;
	public DetectThread detectThread;
	public LadybirdThread ladybirdThread;
	
	public static volatile int mThreadCount = 0;
	public static volatile int dThreadCount = 0;
	public static volatile int lThreadCount = 0;

	public static final int COLOR_SEARCH = 0;

	public static void main(String[] args){

		while(Action.buttons.getButtons() != Keys.ID_ESCAPE){
			if(dThreadCount==0){
				DetectThread detectThread = new DetectThread();
				dThreadCount++;
				detectThread.start();
			}
			if(lThreadCount==0){
				LadybirdThread ladybirdThread = new LadybirdThread();
				lThreadCount++;
				ladybirdThread.start();
			}
			if(mThreadCount==0){
				MoveThread moveThread = new MoveThread();
				mThreadCount++;
				moveThread.start();
			}
			
			if(ladybirdDetected){
				RIGHT_MOTOR.stop();
				LEFT_MOTOR.stop();
				lcddisplay.drawString("Ladybird detected",0,0);
				Sound.playSample(screamFile,100);
				buttons.waitForAnyPress();
				ladybirdDetected = false;
				lcddisplay.clear();
				mThreadCount = 0;
				lThreadCount = 0;
			}if(objectDetected){
				Action.RIGHT_MOTOR.stop();
				Action.LEFT_MOTOR.stop();
				Sound.playSample(Action.oopsFile,100);
				Action.LEFT_MOTOR.rotate(360);
				Action.LEFT_MOTOR.stop();
				objectDetected = false;
				mThreadCount=0;
				dThreadCount = 0;
			}
		}
	}
}

//-------------------------------------------------------------------------

class MoveThread extends Thread{
	public void run() {
		while(!Action.objectDetected && !Action.ladybirdDetected){
			Action.LEFT_MOTOR.forward();
			Action.RIGHT_MOTOR.forward();
		}
	}
}

//------------------------------------------------------------------------

class DetectThread extends Thread{
	public void run() {
		SampleProvider sonicdistance = Action.sonicSensor.getDistanceMode();
		float[] sampleUltraSonic = new float[sonicdistance.sampleSize()];

		while(!Action.objectDetected){
			sonicdistance.fetchSample(sampleUltraSonic,0);

			if(sampleUltraSonic[0] < 0.1){
				Action.objectDetected = true;
				Thread.currentThread().interrupt();
			}

		}

	}

}


//-------------------------------------------------------------------------
class LadybirdThread extends Thread{
	public void run(){
		Action.colorSense.setFloodlight(false);

		while(!Action.ladybirdDetected){
			int found = Action.colorSense.getColorID();

			if(found==Action.COLOR_SEARCH){
				Action.ladybirdDetected = true;			
			}
		}		
	}
}

