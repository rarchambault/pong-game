package ppPackage;

import static ppPackage.ppSimParams.EMAX;
import static ppPackage.ppSimParams.EMIN;
import static ppPackage.ppSimParams.LPaddleXinit;
import static ppPackage.ppSimParams.LPaddleYinit;
import static ppPackage.ppSimParams.OFFSET;
import static ppPackage.ppSimParams.RSEED;
import static ppPackage.ppSimParams.STARTDELAY;
import static ppPackage.ppSimParams.TSCALE;
import static ppPackage.ppSimParams.ThetaMAX;
import static ppPackage.ppSimParams.ThetaMIN;
import static ppPackage.ppSimParams.VoMAX;
import static ppPackage.ppSimParams.VoMIN;
import static ppPackage.ppSimParams.Xinit;
import static ppPackage.ppSimParams.YinitMAX;
import static ppPackage.ppSimParams.YinitMIN;
import static ppPackage.ppSimParams.agent;
import static ppPackage.ppSimParams.agentScore;
import static ppPackage.ppSimParams.agentScoreCount;
import static ppPackage.ppSimParams.human;
import static ppPackage.ppSimParams.humanScore;
import static ppPackage.ppSimParams.humanScoreCount;
import static ppPackage.ppSimParams.ppPaddleW;
import static ppPackage.ppSimParams.ppPaddleXinit;
import static ppPackage.ppSimParams.ppPaddleYinit;
import static ppPackage.ppSimParams.reactionTime;
import static ppPackage.ppSimParams.running;
import static ppPackage.ppSimParams.timeMultiplier;
import static ppPackage.ppSimParams.traceButton;
import static ppPackage.ppSimParams.xmax;
import static ppPackage.ppSimParams.ymax;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import acm.graphics.GPoint;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

/**
 * The ppSim class is the main class of this program. It sets up all the
 * buttons, labels and sliders of the displayed menu. It also monitors the code
 * that is executed when each one of the buttons is pressed via the
 * ActionListeners. It calls an instance of the ppTable class to generate the
 * left wall.
 * 
 * This class exports methods which are called to set up a new game and to
 * create an instance of the ppBall class using random parameters for the
 * initial y position, initial velocity, energy loss parameter and initial
 * direction - that is where each one of the objects is started to initialize
 * the simulation.
 * 
 * It is also in ppSim that the method setP() to change paddle's position is
 * called via the mouseMoved event, which is monitored in ppSim by the
 * MouseListeners.
 * 
 * The ppSim class is a Graphics Program as this is where all GObjects are
 * added. This is the display that the user sees when the program is run.
 * 
 * @author Roxanne Archambault
 * @date 11/16/2021
 * 
 *       This code contains elements from the ECSE202 Assignment 2 handout
 *       provided by Prof. Frank Ferrie as well as from Mlle Katrina Sarah-Ève
 *       Poulin, who provided parts of this code during the ESCE202 tutorials.
 */

public class ppSim extends GraphicsProgram {

	// Instance variables
	ppTable myTable; // Instance of ppTable
	ppPaddle RPaddle; // Instance of ppPaddle for player paddle
	ppPaddleAgent LPaddle; // Instance of ppPaddle for agent paddle
	ppBall myBall; // Instance of ppBall
	RandomGenerator rgen; // Instance of the RandomGenerator class

	/**
	 * The main method of the ppSim class is where the simulation is started; every
	 * instance of the other classes and every graphic element will be added after
	 * this point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new ppSim().start(args);
	}

	/**
	 * The init method is where the graphic elements (buttons, sliders, labels, and
	 * ground plane, paddles and ball through other methods which are called via
	 * this method) are created and added to the display.
	 */
	public void init() {
		this.resize(xmax + OFFSET, ymax + OFFSET); // Change window size to the set parameters

		// Add the buttons for the menu items
		add(new JButton("Clear"), SOUTH); // Button to clear the scores on the score board
		add(new JButton("New Serve"), SOUTH); // Button to start a new game
		traceButton = new JToggleButton("Trace", false); // JToggleButton to enable or disable the tracing of marks to
															// indicate the ball's trajectory (starts at false so that
															// the trajectory is not plotted when the program starts)
		add(traceButton, SOUTH); // traceButton is added to the display
		add(new JButton("Quit"), SOUTH); // Button to quit the program

		// Add JTextFields for the score board
		agent = new JTextField("Agent");
		agent.setEditable(false); // So that the agent's name cannot be changed
		agentScore = new JTextField("0");
		agentScore.setEditable(false); // So that the user cannot change the score
		human = new JTextField("PLAYER NAME", 10); // This JTextField can be edited, so the player can change their name
		human.addActionListener(this);
		humanScore = new JTextField("0");
		humanScore.setEditable(false); // So that the user cannot change the score
		add(agent, NORTH);
		add(agentScore, NORTH);
		add(human, NORTH);
		add(humanScore, NORTH);

		// Add sliders for the regulation of the response time of the agent
		add(new JLabel("-t"), SOUTH); // Indicates the left end of the slider, where the time pause will be shorter
										// (agent is faster)
		timeMultiplier = new JSlider((int) TSCALE, 6000, (int) TSCALE); // This first slider changes the multiplier that
		// converts
		// TICK to milliseconds (its default value is the TSCAlE
		// parameter (2000 ms))
		add(timeMultiplier, SOUTH); // Add slider to the display
		add(new JLabel("+t"), SOUTH); // Indicates the right end of the slider, where the time pause will be longer
										// (agent is slower)
		add(new JButton("rtime"), SOUTH); // Button to reset the timePauseSlider to its default value

		add(new JLabel("-lag"), SOUTH); // Indicates the left end of the slider, where the reaction time will be shorter
										// (agent is faster)
		reactionTime = new JSlider(0, 15, 0); // This second slider changes the reaction time of the agent
		add(reactionTime, SOUTH); // Add slider to the display
		add(new JLabel("+lag"), SOUTH); // Indicates the right end of the slider, where the reaction time will be longer
										// (agent is slower)
		add(new JButton("rlag"), SOUTH); // Button to reset the timePauseSlider to its default value

		addMouseListeners(); // So that we can monitor when the user changes the position of the mouse in
								// order to update the position of the paddle
		addActionListeners(); // So that we can monitor when the buttons are clicked
		addKeyListeners(); // So that we can monitor when the "enter" key is pressed to set the player's
							// name

		// Create random number generator
		rgen = RandomGenerator.getInstance();
		rgen.setSeed(RSEED);

		// Generate table
		myTable = new ppTable(this);

		newGame(); // Initialize menu and start the simulation

	}

	/**
	 * The newBall method creates an instance of the ppBall class using the
	 * RandomGenerator instance to get random values for the initial y position,
	 * energy loss parameter, initial velocity and initial direction.
	 * 
	 * @return a instance of the ppBall class with the parameters that have been
	 *         determined
	 */
	ppBall newBall() {

		// Generate parameters for ppBall (they all have their own range from the
		// ppSimParams class)
		Color iColor = Color.RED; // Ball color
		double iYinit = rgen.nextDouble(YinitMIN, YinitMAX); // Initial Y position of the ball
		double iLoss = rgen.nextDouble(EMIN, EMAX); // Energy loss parameter
		double iVel = rgen.nextDouble(VoMIN, VoMAX); // Initial velocity of the ball
		double iTheta = rgen.nextDouble(ThetaMIN, ThetaMAX); // Initial direction of the ball

		// Create new ppBall
		return new ppBall(Xinit + ppPaddleW / 2, iYinit, iVel, iTheta, iLoss, iColor, myTable, this);
	}

	/**
	 * The newGame method is called in the init method of the ppSim class and when
	 * the New Serve button is pressed. It stops the simulation that was running
	 * before if there was one and removes everything from the display, then it adds
	 * the graphic elements (ground plane, paddles, ball) back. It then calls the
	 * start method on the ball and the paddles to begin the simulation.
	 */
	public void newGame() {
		if (myBall != null) // If an instance of ppBall already exists on the display
			myBall.kill(); // Stop current game in play
		myTable.newScreen(); // Remove the current elements on the display and adds a new ground plane
		myBall = newBall(); // Create a new instance of ppBall with new random values for the initial
							// parameters

		RPaddle = new ppPaddle(ppPaddleXinit, ppPaddleYinit, Color.GREEN, myTable, this); // Create a new paddle for the
																							// player
		LPaddle = new ppPaddleAgent(LPaddleXinit, LPaddleYinit, Color.BLUE, myTable, this); // Create a new paddle for
																							// the agent

		// Link the objects together so that they can use the same instances of the
		// ppPaddle and ppBall classes throughout the program
		LPaddle.attachBall(myBall);
		myBall.setRightPaddle(RPaddle);
		myBall.setLeftPaddle(LPaddle);

		pause(STARTDELAY); // Pause before starting the new game so that the user can have time to prepare

		// Start simulation
		running = true;
		myBall.start();
		LPaddle.start();
		RPaddle.start();
	}

	/**
	 * The setPlayerName method is used to disable editing for that text field of
	 * the player's name for the rest of the game so that they cannot change their
	 * name anymore.
	 */
	public void setPlayerName() {
		human.setEditable(false);
	}

	/**
	 * The mouseMoved method is the Mouse Handler - a moved event moves the paddle
	 * up and down in Y
	 */
	public void mouseMoved(MouseEvent e) {
		if (myTable == null || RPaddle == null) // If there are no instances of myTable or ppPaddle for the player, the
												// MouseListeners return a null pointer exception when the program tries
												// to move RPaddle or to access the S2W method in ppTable. This happens
												// if the mouse is moved before RPaddle and myTable are created, so this
												// condition avoids the error.
			return;
		GPoint Pm = myTable.S2W(new GPoint(e.getX(), e.getY())); // Convert new mouse position to screen coordinates
		double PaddleX = RPaddle.getP().getX(); // Get current paddle x position (the x position does not change even if
												// the mouse is moved)
		double PaddleY = Pm.getY(); // Get the new mouse y position
		RPaddle.setP(new GPoint(PaddleX, PaddleY)); // Set the new paddle position to the same x position and the new y
													// position
	}

	/**
	 * The actionPerformed method is triggered when ANY button is pressed; then, it
	 * chooses the right action consequently.
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand(); // Attributes the text of the button that was pressed (the ActionCommand)
												// to a String
		if (command.equals("New Serve")) { // If the New Serve button has been pressed
			newGame(); // Call the newGame method to reset the game display and start it again
		} else if (command.equals("Quit")) { // If the Quit button has been pressed
			System.exit(0); // Exit the program
		} else if (command.equals("Clear")) { // If the Clear button has been pressed
			// Clear scores
			agentScoreCount = 0;
			humanScoreCount = 0;
			// Display new scores on the JLabels
			agentScore.setText(String.valueOf(agentScoreCount));
			humanScore.setText(String.valueOf(humanScoreCount));
		} else if (command.equals("rtime")) { // If the rtime button has been pressed
			timeMultiplier.setValue((int) TSCALE); // reset timeMultiplier to its default value (TSCALE)
		} else if (command.equals("rlag")) { // If the rlag button has been pressed
			reactionTime.setValue(0); // reset reactionTime to its default value (0)
		} else if (command.equals(human.getText())) { // If the player changes their name and presses ENTER
			setPlayerName(); // Call setPlayerName method to disable editing for the player name
								// text field
		}
	}

}
