package model.robot;

import model.virtualmap.OccupancyMap;

import java.io.PipedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PipedOutputStream;
import java.io.IOException;

import java.util.StringTokenizer;

/**
 * Title    :   The Mobile Robot Explorer Simulation Environment v2.0
 * Copyright:   GNU General Public License as published by the Free Software Foundation
 * Company  :   Hanze University of Applied Sciences
 *
 * @author Dustin Meijer        (2012)
 * @author Alexander Jeurissen  (2012)
 * @author Davide Brugali       (2002)
 * @version 2.0
 */

public class MobileRobotAI implements Runnable {

	private final OccupancyMap map;
	private final MobileRobot robot;

	private boolean running;

	public MobileRobotAI(MobileRobot robot, OccupancyMap map) {
		this.map = map;
		this.robot = robot;

	}

	/**
	 * In this method the gui.controller sends commands to the robot and its devices.
	 * At the moment all the commands are hardcoded.
	 * The exercise is to let the gui.controller make intelligent decisions based on
	 * what has been discovered so far. This information is contained in the OccupancyMap.
	 */
	public void run() {
		String result;
		this.running = true;
		double position[] = new double[3];
		double measures[] = new double[360];
		while (running) {
			try {

				PipedInputStream pipeIn = new PipedInputStream();
				BufferedReader input = new BufferedReader(new InputStreamReader(pipeIn));
				PrintWriter output = new PrintWriter(new PipedOutputStream(pipeIn), true);

				robot.setOutput(output);

//      ases where a variable value is never used after its assignment, i.e.:
				System.out.println("intelligence running");

				boolean findingWall = true;

				while (findingWall){
					getPos(input,position);
					scan(input,position,measures);
					if(measures[0] == 100.0){
						moveForward(input,40);
					}else{
						findingWall = false;
						int distance = (int)measures[0] - 15;
						moveForward(input,distance);
						rotateLeft(input,90);
					}
				}

				boolean mappingRoom = true;

				while (mappingRoom && !map.checkCompleteMap()){
					getPos(input,position);
					scan(input,position,measures);
					if (!checkWallRight(measures,30,50,5)){
						rotateRight(input,90);
						moveForward(input,35);
					}else{
						if (checkWallFront(measures,40,40)){
							int distance = checkWallRange(measures,40,40);
							moveForward(input,distance - 15);
							rotateLeft(input,90);
						}else {
							moveForward(input,25);
						}
					}
				}

//
				this.running = false;
			} catch (IOException ioe) {
				System.err.println("execution stopped");
				running = false;
			}
		}
	}

	private boolean checkWallFront(double[] measures, int width, int range){
		for (int i = 0; i < width; i++){
			int number = ((359-(width/2))+i)%360;
			if (measures[number] < range)
				return true;
		}
		return false;
	}

	private int checkWallRange(double[] measures, int width, int range){
		int shortRange = range;
		for (int i = 0; i < width; i++){
			int number = ((359-(width/2))+i)%360;
			if (measures[number] < shortRange)
				shortRange = (int)measures[number];
		}
		return shortRange;
	}

	private boolean checkWallRight(double[] measures, int width, int range, int displacement){
		for (int i = 0; i < width; i++){
			int number = ((90-(width/2))+i+displacement)%360;
			if (measures[number] < range)
				return true;
		}
		return false;
	}

	private void moveForward(BufferedReader input, int steps) throws  IOException {
		robot.sendCommand("P1.MOVEFW "+ steps);
		String result = input.readLine();
	}

	private void rotateLeft(BufferedReader input, int degrees) throws IOException {
		robot.sendCommand("P1.ROTATELEFT "+degrees);
		String result = input.readLine();
	}

	private void rotateRight(BufferedReader input, int degrees) throws IOException {
		robot.sendCommand("P1.ROTATERIGHT "+degrees);
		String result = input.readLine();
	}

	private void getPos(BufferedReader input, double[] position) throws IOException {
		robot.sendCommand("R1.GETPOS");
		String result = input.readLine();
		parsePosition(result, position);
	}

	private void scan(BufferedReader input, double[] position, double[] measures) throws IOException {
		robot.sendCommand("L1.SCAN");
		String result = input.readLine();
		parseMeasures(result, measures);
		map.drawLaserScan(position, measures);
	}

	private void parsePosition(String value, double position[]) {
		int indexInit;
		int indexEnd;
		String parameter;

		indexInit = value.indexOf("X=");
		parameter = value.substring(indexInit + 2);
		indexEnd = parameter.indexOf(' ');
		position[0] = Double.parseDouble(parameter.substring(0, indexEnd));

		indexInit = value.indexOf("Y=");
		parameter = value.substring(indexInit + 2);
		indexEnd = parameter.indexOf(' ');
		position[1] = Double.parseDouble(parameter.substring(0, indexEnd));

		indexInit = value.indexOf("DIR=");
		parameter = value.substring(indexInit + 4);
		position[2] = Double.parseDouble(parameter);
	}

	private void parseMeasures(String value, double measures[]) {
		for (int i = 0; i < 360; i++) {
			measures[i] = 100.0;
		}
		if (value.length() >= 5) {
			value = value.substring(5);  // removes the "SCAN " keyword

			StringTokenizer tokenizer = new StringTokenizer(value, " ");

			double distance;
			int direction;
			while (tokenizer.hasMoreTokens()) {
				distance = Double.parseDouble(tokenizer.nextToken().substring(2));
				direction = (int) Math.round(Math.toDegrees(Double.parseDouble(tokenizer.nextToken().substring(2))));
				if (direction == 360) {
					direction = 0;
				}
				measures[direction] = distance;
				// Printing out all the degrees and what it encountered.
				//System.out.println("direction = " + direction + " distance = " + distance);
			}
		}
	}


}
