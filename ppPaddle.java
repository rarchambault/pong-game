
package ppPackage;

import static ppPackage.ppSimParams.TICK;
import static ppPackage.ppSimParams.TSCALE;
import static ppPackage.ppSimParams.Xs;
import static ppPackage.ppSimParams.Ys;
import static ppPackage.ppSimParams.ppPaddleH;
import static ppPackage.ppSimParams.ppPaddleW;

import java.awt.Color;

import acm.graphics.GPoint;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

/**
 * The ppPaddle class creates the GRect object which represents the paddle and
 * adds it to ppSim. It also updates the paddle's velocity as the program runs.
 * The ppPaddle class also exports a few methods which are called in other
 * classes: getV(), which returns the paddle's velocity; setP(), which updates
 * the paddle's position; getP(), which returns the position of the paddle;
 * getSgnVy(), which returns the sign of the Y velocity of the paddle; and
 * contact(), which returns true if the ball is in collision with the paddle.
 * 
 * @author Roxanne Archambault
 * @date 11/16/2021
 * 
 *       This code contains elements from the ECSE202 Assignment 2 handout
 *       provided by Prof. Frank Ferrie as well as from Mlle Katrina Sarah-Ève
 *       Poulin, who provided parts of this code during the ESCE202 tutorials.
 */

public class ppPaddle extends Thread { // So that it can be started at the same time as the other objects in this
										// simulation

	// Instance variables
	double X; // X position of the paddle
	double Y; // Y position of the paddle
	private ppTable myTable; // Instance of ppTable
	private GraphicsProgram GProgram; // Instance of ppSim
	private GRect myPaddle; // Instance of ppPaddle
	double Vx; // X velocity of the paddle
	double Vy; // Y velocity of the paddle
	private Color myColor; // Color of the paddle

	/**
	 * getV - Method to return the paddle's current velocity components
	 * 
	 * @return GPoint containing the x and y components of the current velocity of
	 *         the paddle
	 */
	public GPoint getV() { // Returns paddle velocity (Vx,Vy)
		return new GPoint(Vx, Vy);
	}

	/**
	 * setP - Method that sets and moves paddle to (X,Y)
	 * 
	 * @param P the point where the paddle will be moved (in world coordinates)
	 */
	public void setP(GPoint P) {
		// P is in world coordinates
		X = P.getX();
		Y = P.getY();
		double upperLeftX = P.getX() - ppPaddleW / 2; // The origin of the paddle (GRect) is its center
		double upperLeftY = P.getY() + ppPaddleH / 2;
		GPoint screenP = myTable.W2S(new GPoint(upperLeftX, upperLeftY)); // Convert world coordinates to screen
																			// coordinates
		double ScrX = screenP.getX();
		double ScrY = screenP.getY();
		myPaddle.setLocation(ScrX, ScrY); // Set paddle to the new location (in screen coordinates)
	}

	/**
	 * getP - Method that returns paddle location (X,Y)
	 * 
	 * @return GPoint containing the current x and y coordinates of the paddle
	 */
	public GPoint getP() {
		return new GPoint(X, Y);
	}

	/**
	 * getSgnVy - Method that returns the sign of the current y component of the
	 * paddle's velocity
	 * 
	 * @return a double, either 1 or -1, representing the sign of the paddle's
	 *         current velocity
	 */
	public double getSgnVy() {
		if (Vy >= 0)
			return 1;
		else
			return -1;
	}

	/**
	 * contact - Method that returns true if a surface at position (Wx,Wy) is deemed
	 * to be in contact with the paddle.
	 * 
	 * @param Wx the current x position of the paddle in world coordinates
	 * @param Wy the current y position of the paddle in world coordinates
	 * @return a boolean which is true or false (true if the ball is in contact with
	 *         the paddle)
	 */
	public boolean contact(double Wx, double Wy) {
// check y position of ball against paddle position (in world coordinates)
		if (Wy >= Y - ppPaddleH / 2 && Wy <= Y + ppPaddleH / 2) {
			return true; // (Wx,Wy) is deemed to be in contact
			// with the paddle.
		} else
			return false;
	}

	/**
	 * The constructor of the ppPaddle class creates a GRect object representing the
	 * player's paddle and adds it to the display.
	 * 
	 * @param X        - The X position where the paddle's center is going to be set
	 * @param Y        - The Y position where the paddle's center is going to be set
	 * @param myColor  - The color of the paddle
	 * @param myTable  - An instance of the ppTable class which will be used for its
	 *                 W2S method
	 * @param GProgram - An instance of the ppSim class which will be used to add
	 *                 the paddle to the display
	 */
	public ppPaddle(double X, double Y, Color myColor, ppTable myTable, GraphicsProgram GProgram) {

		// Attribute the variables of the constructor to the instance variables so that
		// they can be manipulated everywhere in the ppPaddle class
		this.X = X;
		this.Y = Y;
		this.myColor = myColor;
		this.myTable = myTable;
		this.GProgram = GProgram;

		// Upper left corner of paddle in world coordinates
		double upper_left_x = X - ppPaddleW / 2; // The origin of the paddle (GRect) is on its upper left corner, so we
													// have to change the coordinates so that the center of the paddle
													// is at (X,Y)
		double upper_left_y = Y + ppPaddleH / 2;
		GPoint p = myTable.W2S(new GPoint(upper_left_x, upper_left_y)); // Change world coordinates of the upper left
																		// corner to screen coordinates
		double ScrX = p.getX();
		double ScrY = p.getY();
		this.myPaddle = new GRect(ScrX, ScrY, ppPaddleW * Xs, ppPaddleH * Ys); // Create a GRect for the paddle
		myPaddle.setFilled(true);
		myPaddle.setColor(myColor);
		GProgram.add(this.myPaddle); // Add the paddle to ppSim
	}

	/**
	 * The run method of the ppPaddle class is used to calculate the velocity of the
	 * paddle as it is moved by the user. This is used in the getSgnVy method to
	 * determine the direction of the ball after a collision with the player's
	 * paddle. It also pauses the update of the paddle's position every time it is
	 * updated.
	 */
	public void run() {
		double lastX = X;
		double lastY = Y;
		while (true) {
			Vx = (X - lastX) / TICK; // Calculate X and Y velocities of the paddle according to the present and last
										// position of the paddle as set by the user
			Vy = (Y - lastY) / TICK;
			lastX = X;
			lastY = Y;
			GProgram.pause(TICK * TSCALE); // Time to mS
		}
	}

}
