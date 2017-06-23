package aktion;

import lejos.hardware.ev3.EV3;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.lcd.TextLCD;


public class Test {

	public static void main(String[] args) {
		//EV3-Stein suchen&finden
		EV3 ev3brick = (EV3) BrickFinder.getLocal();
		
		//Buttons und LCD-Display instantiieren
		Keys buttons = ev3brick.getKeys();
		TextLCD lcddisplay = ev3brick.getTextLCD();
		
		//Test auf LCD-Display schreiben
		lcddisplay.drawString("Helloworld",2,4);
		
		//auf Reaktion des Anwenders der Testinstallation warten
		buttons.waitForAnyPress();
		
	}
}
