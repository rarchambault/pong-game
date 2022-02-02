package ppPackage;

import static ppPackage.ppSimParams.TICK;
import static ppPackage.ppSimParams.reactionTime;
import static ppPackage.ppSimParams.running;
import static ppPackage.ppSimParams.timeMultiplier;

import java.awt.Color;

import acm.graphics.GPoint;
import acm.program.GraphicsProgram;

/**
 * The ppPaddleAgent is used to create the paddle object for the agent. It is
 * also where the setP method is called to update the agent's paddle's position
 * (but not every time so that the agent does not always hit the ball).
 * 
 * @author Roxanne Archambault
 * @date 11/16/2021
 *
 *       This code contains elements from the ECSE202 Assignment 2 handout
 *       provided by Prof. Frank Ferrie as well as from Mlle Katrina Sarah-Ève
 *       Poulin, who provided parts of this code during the ESCE202 tutorials.
 */
public class ppPaddleAgent extends ppPaddle { // So that it can have the same properties as ppPaddle and use the methods
												// that ppPaddle exports.

	// Instance variables
	ppBall myBall; // Instance of ppBall
	GraphicsProgram GProgram; // Instance of ppSim

	/**
	 * The constructor of the ppPaddle creates the GRect object that represents the
	 * agent's paddle using the constructor of the ppPaddle class. It therefore has
	 * the same parameters as the ppPaddle class:
	 * 
	 * @param X        - The X position where the paddle's center is going to be set
	 * @param Y        - The Y position where the paddle's center is going to be set
	 * @param myColor  - The color of the paddle
	 * @param myTable  - An instance of the ppTable class which will be used for its
	 *                 W2S method
	 * @param GProgram - An instance of the ppSim class which will be used to add
	 *                 the paddle to the display
	 */
	public ppPaddleAgent(double X, double Y, Color myColor, ppTable myTable, GraphicsProgram GProgram) {

		// Call the ppPaddle constructor to create the paddle object
		super(X, Y, myColor, myTable, GProgram);

		// Attribute the variable of the constructor to the instance variables so that
		// they can be used elsewhere in this class
		this.GProgram = GProgram;

	}

	/**
	 * The run method of the ppPaddleAgent class is where the paddle's position is
	 * updated every nth iteration via the setP method.
	 */
	public void run() {
		int ballSkip = 0; // Parameter which is used to determine when to update the agent's paddle's
							// position, initialized to 0
		int AgentLag = reactionTime.getValue(); // Making AgentLag larger slows down the agent
		double lastX = X;
		double lastY = Y;
		while (running) {
			Vx = (X - lastX) / TICK; // Calculate X and Y velocities of the paddle according to the present and last
			// position of the paddle
			Vy = (Y - lastY) / TICK;
			lastX = X;
			lastY = Y;
			if (ballSkip++ >= AgentLag) { // The agent's paddle's position is only updated when this condition is true
											// so that it does not always return the ball
				// Code to update paddle position
				double ballY = myBall.getP().getY(); // Get the y position of the ball
				this.setP(new GPoint(this.getP().getX(), ballY)); // Set the paddle's position to the y position of the
																	// ball while keeping its current x position
				ballSkip = 0; // Reinitialize the ballSkip parameter so that the next iterations will be
								// skipped
			}
			GProgram.pause(TICK * timeMultiplier.getValue()); // Time to mS
		}
	}

	/**
	 * The attachBall method sets the value of the myBall instance variable in
	 * ppPaddleAgent so that these two objects are linked when initializing the game
	 * components in newGame in ppSim.
	 * 
	 * @param myBall the instance of ppBall which is declared in ppPaddleAgent
	 */
	public void attachBall(ppBall myBall) { // Sets the value of the myBall instance variable in ppPaddleAgent.
		this.myBall = myBall;
	}
}
