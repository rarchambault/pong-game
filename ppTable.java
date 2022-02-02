package ppPackage;

import static ppPackage.ppSimParams.OFFSET;
import static ppPackage.ppSimParams.Xmin;
import static ppPackage.ppSimParams.Xs;
import static ppPackage.ppSimParams.Ymin;
import static ppPackage.ppSimParams.Ys;
import static ppPackage.ppSimParams.ymax;

import java.awt.Color;

import acm.graphics.GPoint;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

/**
 * The ppTable class is used here to create the ground plane. It is also here
 * that one can find the W2S and S2W methods, which are used respectively to
 * convert world coordinates into screen coordinates and vice-versa. This class
 * also contains the method that is used in ppSim to clear the elements of the
 * screen when a new game is created.
 * 
 * @author Roxanne Archambault
 * @date 11/16/2021
 * 
 *       This code contains elements from the ECSE202 Assignment 2 handout
 *       provided by Prof. Frank Ferrie as well as from Mlle Katrina Sarah-Ève
 *       Poulin, who provided parts of this code during the ESCE202 tutorials.
 */

public class ppTable extends GraphicsProgram { // So that it can modify things on the grpahic display
	// Instance variable
	GraphicsProgram GProgram; // Instance of ppSim

	public ppTable(GraphicsProgram GProgram) {
		this.GProgram = GProgram;

		// Create the ground plane and add it to ppSim
		drawGroundPlane();
	}

	/***
	 * Method to convert from world to screen coordinates.
	 * 
	 * @param P a point object in world coordinates
	 * @return p the corresponding point object in screen coordinates
	 * 
	 */

	public GPoint W2S(GPoint P) {
		return new GPoint((P.getX() - Xmin) * Xs, ymax - (P.getY() - Ymin) * Ys);
	}

	/***
	 * Method to convert from screen to world coordinates.
	 * 
	 * @param P a point object in screen coordinates
	 * @return p the corresponding point object in world coordinates
	 * 
	 */
	public GPoint S2W(GPoint P) {
		double ScrX = P.getX();
		double ScrY = P.getY();
		double WorldX = ScrX / Xs + Xmin;
		double WorldY = (ymax - ScrY) / Ys + Ymin;
		return new GPoint(WorldX, WorldY);
	}

	/***
	 * The newScreen method erases the current display by removing all objects and
	 * then regenerating the display; called whenever a new serve is requested.
	 */
	public void newScreen() {
		// Remove all objects on the screen (except buttons, sliders, and labels, which
		// are not on the center of the diplay)
		GProgram.removeAll();
		// Redraw the ground plane and add it to ppSim
		drawGroundPlane();
	}

	/**
	 * The drawGroundPlane method creates the GRect object that represents the
	 * ground plane and adds it to the display (ppSim)
	 */
	public void drawGroundPlane() {
		GRect gPlane = new GRect(0, ppSimParams.HEIGHT, ppSimParams.WIDTH + OFFSET, 3);
		gPlane.setColor(Color.BLACK);
		gPlane.setFilled(true);
		GProgram.add(gPlane);
	}

}
