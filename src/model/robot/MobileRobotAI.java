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
					robot.sendCommand("R1.GETPOS");
					result = input.readLine();
					parsePosition(result, position);

					robot.sendCommand("L1.SCAN");
					result = input.readLine();
					parseMeasures(result, measures);
					map.drawLaserScan(position, measures);

					int range = checkForWall(position,'N', 5);
					if(range == -1){
						System.out.println("move forward");
						robot.sendCommand("P1.MOVEFW 40");
						result = input.readLine();
					}else{
						findingWall = false;
						int distance = ((range - 3) * map.getCellDimension());
						System.out.println("move forward");
						robot.sendCommand("P1.MOVEFW "+distance);
						result = input.readLine();

						robot.sendCommand("P1.ROTATELEFT 90");
						result = input.readLine();
					}
				}

				boolean findingExit = true;

				while (findingExit){
					robot.sendCommand("R1.GETPOS");
					result = input.readLine();
					parsePosition(result, position);

					robot.sendCommand("L1.SCAN");
					result = input.readLine();
					parseMeasures(result, measures);
					map.drawLaserScan(position, measures);

					int rangeRight = checkForWall(position,'E', 5);
					System.out.println("rangeRight : " + rangeRight);
					if(rangeRight == -1){
						robot.sendCommand("P1.ROTATERIGHT 90");
						result = input.readLine();

						robot.sendCommand("P1.MOVEFW 40");
						result = input.readLine();
					}else{
						int rangeFront = checkForWall(position,'N', 5);
						if (rangeFront == -1){
							robot.sendCommand("P1.MOVEFW 40");
							result = input.readLine();
						}else {
							robot.sendCommand("P1.ROTATELEFT 90");
							result = input.readLine();
						}
					}
				}

//				robot.sendCommand("R1.GETPOS");
//				result = input.readLine();
//				parsePosition(result, position);
//
//				robot.sendCommand("L1.SCAN");
//				result = input.readLine();
//				parseMeasures(result, measures);
//				map.drawLaserScan(position, measures);

//				robot.sendCommand("P1.MOVEBW 60");
//				result = input.readLine();
//
//				robot.sendCommand("R1.GETPOS");
//				result = input.readLine();
//				parsePosition(result, position);
//
//				robot.sendCommand("L1.SCAN");
//				result = input.readLine();
//				parseMeasures(result, measures);
//				map.drawLaserScan(position, measures);
//
//
//
//				robot.sendCommand("P1.MOVEFW 100");
//				result = input.readLine();
//
//				robot.sendCommand("R1.GETPOS");
//				result = input.readLine();
//				parsePosition(result, position);
//
//				robot.sendCommand("L1.SCAN");
//				result = input.readLine();
//				parseMeasures(result, measures);
//				map.drawLaserScan(position, measures);
//
//				robot.sendCommand("P1.ROTATELEFT 45");
//				result = input.readLine();
//
//				robot.sendCommand("P1.MOVEFW 70");
//				result = input.readLine();
//
//				robot.sendCommand("R1.GETPOS");
//				result = input.readLine();
//				parsePosition(result, position);
//
//				robot.sendCommand("L1.SCAN");
//				result = input.readLine();
//				parseMeasures(result, measures);
//				map.drawLaserScan(position, measures);
//
//				robot.sendCommand("P1.MOVEFW 70");
//				result = input.readLine();
//
//				robot.sendCommand("P1.ROTATERIGHT 45");
//				result = input.readLine();
//
//				robot.sendCommand("R1.GETPOS");
//				result = input.readLine();
//				parsePosition(result, position);
//
//				robot.sendCommand("L1.SCAN");
//				result = input.readLine();
//				parseMeasures(result, measures);
//				map.drawLaserScan(position, measures);
//
//				robot.sendCommand("P1.MOVEFW 90");
//				result = input.readLine();
//
//				robot.sendCommand("R1.GETPOS");
//				result = input.readLine();
//				parsePosition(result, position);
//
//				robot.sendCommand("L1.SCAN");
//				result = input.readLine();
//				parseMeasures(result, measures);
//				map.drawLaserScan(position, measures);
//
//				robot.sendCommand("P1.ROTATERIGHT 45");
//				result = input.readLine();
//
//				robot.sendCommand("P1.MOVEFW 90");
//				result = input.readLine();
//
//				robot.sendCommand("R1.GETPOS");
//				result = input.readLine();
//				parsePosition(result, position);
//
//				robot.sendCommand("L1.SCAN");
//				result = input.readLine();
//				parseMeasures(result, measures);
//				map.drawLaserScan(position, measures);
//
//				robot.sendCommand("P1.ROTATERIGHT 45");
//				result = input.readLine();
//
//				robot.sendCommand("P1.MOVEFW 100");
//				result = input.readLine();
//
//				robot.sendCommand("R1.GETPOS");
//				result = input.readLine();
//				parsePosition(result, position);
//
//				robot.sendCommand("L1.SCAN");
//				result = input.readLine();
//				parseMeasures(result, measures);
//				map.drawLaserScan(position, measures);
//
//				robot.sendCommand("P1.ROTATERIGHT 90");
//				result = input.readLine();
//
//				robot.sendCommand("P1.MOVEFW 80");
//				result = input.readLine();
//
//				robot.sendCommand("R1.GETPOS");
//				result = input.readLine();
//				parsePosition(result, position);
//
//				robot.sendCommand("L1.SCAN");
//				result = input.readLine();
//				parseMeasures(result, measures);
//				map.drawLaserScan(position, measures);
//
//				robot.sendCommand("P1.MOVEFW 100");
//				result = input.readLine();
//
//				robot.sendCommand("R1.GETPOS");
//				result = input.readLine();
//				parsePosition(result, position);
//
//				robot.sendCommand("L1.SCAN");
//				result = input.readLine();
//				parseMeasures(result, measures);
//				map.drawLaserScan(position, measures);
				this.running = false;
			} catch (IOException ioe) {
				System.err.println("execution stopped");
				running = false;
			}
		}

	}

	private int checkForWall(double position[],char direction,int distance){
		direction = convertDirectionRelative(position,direction);
		for(int i = 1; i <= distance;i++){
			int x;
			int y;
			switch (direction) {
				case 'N':
					x = positionToCell(position[0]);
					y = positionToCell(position[1]) + i;
					break;
				case 'E':
					x = positionToCell(position[0]) + i;
					y = positionToCell(position[1]);
					break;
				case 'S':
					x = positionToCell(position[0]);
					y = positionToCell(position[1]) - i;
					break;
				case 'W':
					x = positionToCell(position[0]) - i;
					y = positionToCell(position[1]);
					break;
				default:
					return -1;
			}
			char result = map.getGrid()[x][y];
			if (result == 'o'){
				return i;
			}
		}
		return -1;
	}

	private char convertDirectionRelative(double position[],char direction){
		char[] directions = {'N','E','S','W'};
		int pos = -1;
		for(int i = 0; i < directions.length; i++) {
			if(directions[i] == direction) {
				pos = i;
				break;
			}
		}
		switch ((int) Math.round(position[2])){
			case 360:
				return directions[(pos + 1)%4];
			case 90:
				return directions[(pos + 2)%4];
			case 180:
				return directions[(pos + 3)%4];
			case 270:
				return directions[pos];
			default:
				return 'N';
		}
	}

	private int positionToCell(double position){
		return (int)position / map.getCellDimension();
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
