import aktion.Action;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;


public class DetectColor {

	public static EV3ColorSensor colorSense = new EV3ColorSensor(SensorPort.S1);
	public static EV3 ev3brick = (EV3) BrickFinder.getLocal();
	public static TextLCD lcddisplay = ev3brick.getTextLCD();
	public static Keys buttons = ev3brick.getKeys();

	
	public static void main(String[] args){
		colorSense.setFloodlight(false);
		
		lcddisplay.drawString("Please show robot \n the color to \n detect.", 0, 0);
		buttons.waitForAnyPress();
		int search = colorSense.getColorID();
		lcddisplay.clear();
		lcddisplay.drawString(String.valueOf(search),0,0);
		buttons.waitForAnyPress();
		
	}
	
}


