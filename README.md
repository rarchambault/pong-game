# pong-game

This Java project was created as an assignment for the course ECSE202 Introduction to Software Development during the Fall2021 term at McGill University.
It is an implementation of the classic Pong game; it simulates a ping pong ball being thrown between the user and the computer, and keeps track of the score when 
either of them misses the ball. 

On the display, there is a floor which represents a
ping pong table, as well as two paddles which will throw a ball back and forth.

A ball is first thrown by an "agent", represented by the left paddle, and the user must throw it back by moving the 
mouse; this allows them to move the right paddle so that the ball is sent back towards the agent if it bounces on the
player's paddle.

At the top of the screen (NORTH region), a scoreboard indicates the scores of the agent and the player. A point is made by the agent or
the player when their opponent fails to send back the ball. The agent or the player also makes a point if their opponent 
sends the ball outside of the borders of the screen, i.e., if the ball's y position is higher that the top of the screen.
Each one of these cases can be monitored via messages displayed in the console.

There are 6 buttons and 2 sliders on the bottom of the screen (SOUTH region).
- the "Clear" button is used to clear the scores on the scoreboard (set them back to 0);

- the "New Serve" button is used to start a new simulation, i.e., for the agent to send a new ball towards the player;

- the "Trace" buttons, which is a Toggle Button, is used to show or not the trajectory of the ball (when it is selected,
the ball's trajectory is plotted on the screen);

- the "Quit" button exits the program;

- the "-t / +t" slider is used to increase or decrease the speed of the whole simulation (increases or decreases the
pause between each update of the ball's position)."-t" makes the game harder and "+t" makes the game easier";

- the "rtime" button is used to set the "-t / +t" slider back to its default value;

- the "-lag / +lag" slider is used to increase or decrease the accuracy of the agent paddle (increases or decreases the
number of times that the agent paddle's position is updated in regard to the number of times the ball's position is 
updated)."-lag" makes the game harder and "+lag" makes the game easier;

- the "rlag" button is used to set the "-lag / +lag" slider back to its default value.

This project is comprised of 6 different classes; the ppSim class, which is also the graphic display (it extends
 GraphicsProgram), is the main class. You can now go through these classes, where more explanations will be provided!
