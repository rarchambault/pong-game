package ppPackage;

/**
 * The ppBall class is where the ball's position is calculated and updated in ppSim in a loop where time is incremented at every position update. 
 * It is also where the program checks for collisions between the ball and the different objects which have been added to the display 
 * (the ground plane and the two paddles). The player's and the agent's scores are updated in ppBall as well.
 * 
 * @author Roxanne Archambault
 * @date 11/16/2021
 * 
 * This code contains elements from the ECSE202 Assignment 2 handout
 *         provided by Prof. Frank Ferrie as well as from Mlle Katrina Sarah-Ève
 *         Poulin, who provided parts of this code during the ESCE202 tutorials.
 */
import static ppPackage.ppSimParams.ETHR;
import static ppPackage.ppSimParams.LPaddleXgain;
import static ppPackage.ppSimParams.LPaddleYgain;
import static ppPackage.ppSimParams.PD;
import static ppPackage.ppSimParams.Pi;
import static ppPackage.ppSimParams.TICK;
import static ppPackage.ppSimParams.VoxMAX;
import static ppPackage.ppSimParams.Xs;
import static ppPackage.ppSimParams.Ymax;
import static ppPackage.ppSimParams.Ys;
import static ppPackage.ppSimParams.agentScore;
import static ppPackage.ppSimParams.agentScoreCount;
import static ppPackage.ppSimParams.bMass;
import static ppPackage.ppSimParams.bSize;
import static ppPackage.ppSimParams.g;
import static ppPackage.ppSimParams.humanScore;
import static ppPackage.ppSimParams.humanScoreCount;
import static ppPackage.ppSimParams.k;
import static ppPackage.ppSimParams.ppPaddleW;
import static ppPackage.ppSimParams.ppPaddleXgain;
import static ppPackage.ppSimParams.ppPaddleYgain;
import static ppPackage.ppSimParams.running;
import static ppPackage.ppSimParams.timeMultiplier;
import static ppPackage.ppSimParams.traceButton;

import java.awt.Color;

import acm.graphics.GOval;
import acm.graphics.GPoint;
import acm.program.GraphicsProgram;

public class ppBall extends Thread {

	// Instance variables
	private double Xinit; // Initial position of ball - X
	private double Yinit; // Initial position of ball - Y
	private double Vo; // Initial velocity (Magnitude)
	private double theta; // Initial direction
	private double loss; // Energy loss on collision
	private double X, Y, Xo, Yo; // Position parameters for the ball
	double Vx, Vy; // x and y components of the ball's velocity
	private double KEx, KEy; // x and y components of kinetic energy
	private double PE; // Potential energy
	private double ScrX, ScrY; // Screen coordinates to add the dots
	private Color color; // Color of ball
	private GraphicsProgram GProgram; // Instance of ppSim class (this)
	GOval myBall; // Graphics object representing ball
	ppTable myTable; // Instance of ppTable
	ppPaddle RPaddle; // Instance of ppPaddle that is going to be moved by the player
	GPoint p; // Used to update the position of the ball
	ppPaddle LPaddle; // Instance of ppPaddle that represents the agent

	/**
	 * The constructor for the ppBall class copies parameters to instance variables,
	 * creates an instance of a GOval to represent the ping-pong ball, and adds it
	 * to the display.
	 *
	 * @param Xinit    - starting position of the ball X (meters)
	 * @param Yinit    - starting position of the ball Y (meters)
	 * @param Vo       - initial velocity (meters/second)
	 * @param theta    - initial angle to the horizontal (degrees)
	 * @param loss     - loss on collision ([0,1])
	 * @param color    - ball color (Color)
	 * @param myTable  - a reference to the ppTable class used to access the W2S
	 *                 method to convert world coordinates in screen coordinates
	 * @param GProgram - a reference to the ppSim class used to manage the display
	 */

	public ppBall(double Xinit, double Yinit, double Vo, double theta, double loss, Color color, ppTable myTable,
			GraphicsProgram GProgram) {

		// Attribute the constructor variables to the instance variables so that they
		// can be used everywhere in the ppBall class
		this.Xinit = Xinit;
		this.Yinit = Yinit;
		this.Vo = Vo;
		this.theta = theta;
		this.loss = loss;
		this.color = color;
		this.GProgram = GProgram;
		this.myTable = myTable;

		// Create the ball instance
		p = myTable.W2S(new GPoint(Xinit, Yinit)); // Convert world coordinates of the initial position of the ball to
													// screen coordinates
		double ScrX = p.getX();
		double ScrY = p.getY();
		this.myBall = new GOval(ScrX, ScrY, 2 * bSize * Xs, 2 * bSize * Ys);
		myBall.setColor(color);
		myBall.setFilled(true);
		GProgram.add(myBall);

	}

	/**
	 * The run method of the ppBall class is where the ball's position is updated
	 * and where the program check for collisions with the ground plane and with
	 * each of the paddles, in which case the score is updated and displayed on the
	 * score board.
	 */

	public void run() {
		// Initialize simulation parameters
		Xo = Xinit; // Set initial X position
		Yo = Yinit; // Set initial Y position
		double time = 0; // Time starts at 0 and counts up
		double Vt = bMass * g / (4 * Pi * bSize * bSize * k); // Terminal velocity
		double Vox = Vo * Math.cos(theta * Pi / 180); // X component of velocity
		double Voy = Vo * Math.sin(theta * Pi / 180); // Y component of velocity

		// Main simulation loop
		while (running) {
			X = Vox * Vt / g * (1 - Math.exp(-g * time / Vt)); // Update relative position
			Y = Vt / g * (Voy + Vt) * (1 - Math.exp(-g * time / Vt)) - Vt * time;
			double Vx = Vox * Math.exp(-g * time / Vt); // Update velocity
			double Vy = (Voy + Vt) * Math.exp(-g * time / Vt) - Vt;

			GProgram.pause(TICK * timeMultiplier.getValue()); // Pause program so that the users have time to see the
																// ball's
			// trajectory

			// Check for collision with the ground plane
			if (Y + Yo <= bSize && Vy < 0) {
				GPoint e = energies(Vx, Vy); // Send current velocities to the energies method to update them using the
												// energy loss parameter
				Vox = e.getX();
				Voy = e.getY();
				if (Vx < 0)
					Vox = -Vox; // The ball must bounce back in the same direction (with respect to x) that it
								// came from
				time = 0; // Time is reset at every collision
				Xo += X; // Need to accumulate distance between collisions
				Yo = bSize; // The absolute position of the ball on the ground
				X = 0; // (X,Y) is the instantaneous position along an arc
				Y = 0; // Absolute position is (Xo+X, Yo+Y)
			}

			// Check for collision with agent paddle
			if (X + Xo <= LPaddle.getP().getX() && Vx < 0) { // Check if the ball has reached the x position of the
																// agent paddle
				if (LPaddle.contact(X + Xo, Y + Yo)) { // Check if the ball is in contact with the agent paddle
					GPoint e = energies(Vx, Vy); // Send current velocities to the energies method to update them using
													// the energy loss parameter
					Vox = e.getX();
					Voy = e.getY();
					Vox *= LPaddleXgain; // Scale X component of velocity
					Voy *= LPaddleYgain * LPaddle.getSgnVy(); // Scale Y + same dir. as paddle
					if (Vy < 0)
						Voy = -Voy; // The ball must bounce back in the same direction (with respect to y) that it
					// came from
					time = 0; // Time is reset at every collision
					Xo = LPaddle.getP().getX() + ppPaddleW / 2; // The new origin for the next trajectory is the impact
																// point
					Yo += Y;
					X = 0; // X and Y are starting directly on the new origin before the next trajectory
					Y = 0;
				} else { // If the ball reached the x position of the agent's paddle but is not in
							// contact
							// with it - the player gets a point
					humanScoreUpdate(); // Update the player's score and display it in the appropriate JTextField
					GProgram.println("Agent missed the ball: point to Player"); // The system prints a line in the
																				// console so we can monitor which case
																				// occurred
					kill(); // The ball stops moving
				}

			}

			// Check for collisions with paddle
			if (X + Xo >= RPaddle.getP().getX() - bSize - ppPaddleW / 2 && Vx > 0) { // Check if the ball has reached
																						// the x position of the
																						// player's paddle
				if (RPaddle.contact(X + Xo, Y + Yo)) { // Check if the ball is in contact with the player's paddle
					GPoint e = energies(Vx, Vy); // Send current velocities to the energies method to update them using
													// the energy loss parameter
					Vox = -1 * e.getX(); // The x velocity of the ball is in the opposite direction after it hits the
											// player's paddle
					Voy = e.getY();
					Vox *= ppPaddleXgain; // Scale X component of velocity
					Voy *= ppPaddleYgain * RPaddle.getSgnVy(); // Scale Y + same dir. as paddle
					time = 0; // Time is reset at every collision
					Xo = RPaddle.getP().getX() - bSize - ppPaddleW / 2; // The new origin for the next trajectory is the
																		// impact point
					Yo += Y;
					X = 0; // X and Y are starting directly on the new origin before the next trajectory
					Y = 0;
				} else { // If the ball reached the x position of the player's paddle but is not in
							// contact
							// with it - the agent gets a point
					agentScoreUpdate(); // Update the agent's score and display it in the appropriate JTextField
					GProgram.println("Player missed the ball: point to Agent"); // The system prints a line in the
																				// console so we can monitor which case
																				// occurred
					kill(); // If the player fails to hit the ball, the simulation stops
				}

			}

			// Check if the ball is out of bounds vertically
			if (Yo + Y > Ymax) {
				kill(); // The ball stops moving
				// Add point to agent or human because ball is out of bounds
				if (Vx > 0) { // If the ball reached the player's paddle's x position (by approaching it from
								// the left, which means the agent has sent it) - the player gets a point
					humanScoreUpdate(); // Update the player's score and display it in the appropriate JTextField
					GProgram.println("Agent threw ball out of bounds: point to Player"); // The system prints a line
																							// in the
					// console so we can monitor which case
					// occurred
				} else if (Vx < 0) { // If the ball reached the agent's paddle's x position (by approaching it from
										// the right, which means the player has sent it) - agent gets a point
					agentScoreUpdate(); // Update the agent's score and display it in the appropriate JTextField
					GProgram.println("Player threw ball out of bounds: point to Agent"); // The system prints a line
																							// in the
					// console so we can monitor which case
					// occurred
				}
			}

			// Update the position of the ball. Plot a tick mark at current location.
			p = myTable.W2S(new GPoint(Xo + X - bSize, Yo + Y + bSize)); // Get current position (top left corner of
			// rectangle
			// in which the ball is) in screen coordinates
			ScrX = p.getX();
			ScrY = p.getY();
			myBall.setLocation(ScrX, ScrY);

			// Check if JToggleButton (from ppSim) is selected and plot a tick mark at
			// current location only if it is
			if (traceButton.isSelected()) {
				GPoint pp = myTable.W2S(new GPoint(Xo + X, Yo + Y)); // Get current position (center of ball) in screen
				// coordinates
				trace(pp.getX(), pp.getY());
			}

			time += TICK; // Time incrementation after each position update

		}

	}

	/**
	 * trace: A simple method to plot a dot at the current location in screen
	 * coordinates
	 * 
	 * @param scrX
	 * @param scrY
	 */

	private void trace(double ScrX, double ScrY) {
		// GPoint o = W2S(new GPoint(bSize, bSize));
		GOval pt = new GOval(ScrX, ScrY, PD, PD);
		pt.setColor(Color.BLACK);
		pt.setFilled(true);
		GProgram.add(pt);
	}

	/**
	 * setRightPaddle - Setter for right paddle (player's paddle)
	 * 
	 * @param myPaddle
	 */
	public void setRightPaddle(ppPaddle myPaddle) {
		this.RPaddle = myPaddle;
	}

	/**
	 * setRightPaddle - Setter for left paddle (agent's paddle)
	 * 
	 * @param LPaddle
	 */
	public void setLeftPaddle(ppPaddle LPaddle) {
		this.LPaddle = LPaddle;
	}

	/**
	 * getP - Getter for ball position
	 * 
	 * @return GPoint containing the present x and y coordinates of the ball
	 */
	public GPoint getP() {
		return new GPoint(Xo + X, Yo + Y);
	}

	/**
	 * getV - Getter for ball velocity
	 * 
	 * @return GPoint containing the present x and y coordinates of the ball's
	 *         velocity
	 */
	public GPoint getV() {
		return new GPoint(Vx, Vy);
	}

	/**
	 * kill - Terminates simulation
	 */
	void kill() {
		running = false;
	}

	/**
	 * agentScoreUpdate - Method to update the score of the agent when it gets a
	 * point and display it in the appropriate JTextField
	 */
	private void agentScoreUpdate() {
		agentScoreCount++; // The appropriate score is incremented
		agentScore.setText(String.valueOf(agentScoreCount)); // The updated score is displayed in the appropriate
																// JTextField
	}

	/**
	 * humanScoreUpdate - Method to update the score of the agent when it gets a
	 * point and display it in the appropriate JTextField
	 */
	private void humanScoreUpdate() {
		humanScoreCount++; // The appropriate score is incremented
		humanScore.setText(String.valueOf(humanScoreCount)); // The updated score is displayed in the appropriate
																// JTextField
	}

	/**
	 * Method to update the velocity of the ball considering the energy loss after
	 * every collision If there has been a collision with the ground, this method
	 * also evaluates if the ball does not have enough energy to keep bouncing; in
	 * that case, the ball stays on the ground as it has ended its journey.
	 *
	 * @param Vx the x velocity of the ball at the moment of the collision
	 * @param Vy the y velocity of the ball at the moment of the collision
	 * @return a GPoint whose coordinates represent the updated x and y velocities
	 */

	public GPoint energies(double Vx, double Vy) {
		double newVx, newVy;
		KEx = 0.5 * bMass * Vx * Vx * (1 - loss); // Calculate new x and y components of the ball's kinetic energy
		KEy = 0.5 * bMass * Vy * Vy * (1 - loss);
		if (Y + Yo <= bSize && Vy < 0) { // If the ball is on the ground plane
			PE = 0;
		} else { // If the ball is in the air
			PE = bMass * g * Y;
		}
		if (KEx + KEy + PE < ETHR) { // Check if the ball's total mechanical energy falls below the set threshold
			GProgram.println("Ball fell on the floor because it ran out of energy: no point attributed");
			kill(); // The ball stops moving
		}
		newVx = Math.sqrt(2 * KEx / bMass); // Calculate new velocities
		newVy = Math.sqrt(2 * KEy / bMass);
		if (newVx > VoxMAX) // If the x component of the new velocity exceed the set maximum, change it so
							// that it remains at said maximum
			newVx = VoxMAX;
		return new GPoint(newVx, newVy);

	}
}
