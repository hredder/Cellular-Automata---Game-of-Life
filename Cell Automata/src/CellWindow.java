import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import processing.core.PApplet;

public class CellWindow extends PApplet {
	
	public static int screenSizeX;
	public static int screenSizeY;
	
	//The length of the cell array
	public static int cellArrWidth;
	
	//The height of the cell array
	public static int cellArrHeight;
	
	//An array of 0's and 1's: 1 = alive, 0 = dead (You can change this to 100x100 or 200x200 but starts to break after that
	public static int[][] cell;
	
	//The length in pixels of the cell(This program does not support non square arrays)
	public static int cellSizeX;
	public static int cellSizeY;
	
	//Boolean on whether the updateGeneration method is being called
	public boolean running = false;
	public int genCount = 0;
	
	//Time in milleseconds between each generation
	public long delayTime = 0;
	
	//Countger for this time
	public long delaySum = 0;
	
	public static enum ruleSet {
		gameOfLife, growth1, growth2
	}
	
	public static ruleSet rules;
	
	public static void main(String[] args){
		
		//Load in properties from config file
		
		Properties prop = new Properties();
		
		FileInputStream inStream = null;
		
		try {
			inStream = new FileInputStream("config.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			prop.load(inStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		screenSizeX = Integer.parseInt(prop.getProperty("screenX"));
		screenSizeY = Integer.parseInt(prop.getProperty("screenY"));
		cellArrWidth = Integer.parseInt(prop.getProperty("arrayWidth"));
		cellArrHeight = Integer.parseInt(prop.getProperty("arrayHeight"));
		
		if (prop.getProperty("ruleset").equals("gameoflife")) {
			
			rules = CellWindow.ruleSet.gameOfLife;
		}
		else if (prop.getProperty("ruleset").equals("growth1")) {
			
			rules = CellWindow.ruleSet.growth1;
		}
		else if (prop.getProperty("ruleset").equals("growth2")) {
			
			rules = CellWindow.ruleSet.growth2;
		}

		
		cell = new int[cellArrWidth][cellArrHeight];
		cellSizeX = screenSizeX/cellArrWidth;
		cellSizeY = screenSizeY/cellArrHeight;
		
		//Creates window
		String[] pargs = {""};
		CellWindow cw = new CellWindow();
		PApplet.runSketch(pargs, cw);
		
		
	
	}
	
	public void settings() {
		this.size(screenSizeX+200,screenSizeY);
	}
	
	public void drawUI() {
		
		//Drawing the grid
		this.strokeWeight(1);
		this.stroke(0,255,0);
		this.fill(0,255,0);
		for (int i = 0; i < cell.length; i++) {
			
			this.line(0, i*cellSizeY, screenSizeX, i*cellSizeY);
			this.line(i*cellSizeX, 0, i*cellSizeX, screenSizeY);
		}
		
		//Drawing buttons
		this.rect(screenSizeX+50,50,50,25);
		this.rect(screenSizeX+50,100,50,25);
		this.text("Generation: " + genCount, screenSizeX+50, 150,150,25);
		this.text("Ruleset: " + this.rules, screenSizeX+50, 175, 150,25);
		this.fill(0,0,0);
		
		this.text("Reset", screenSizeX+50, 115);
		
		if (!running) {
			this.text("Start", screenSizeX+50, 65);
		}
		else {
			this.text("Pause", screenSizeX+50, 65);
		}
	}
	
	public void drawCells() {
		fill(255,0,0);
		for (int i = 0; i < cell.length; i++) {
			for (int j = 0; j < cell[i].length; j++) {
				if (cell[i][j] == 1) {
					this.rect(i*cellSizeX, j*cellSizeY, cellSizeX, cellSizeY);
				}
			}
		}
	}
	
	public void draw() {
		
		//Main loop of program
		
		long startTime = System.currentTimeMillis();
		
		this.background(0);
		drawCells();
		drawUI();
		if (running && delaySum > delayTime) {
			delaySum -= delayTime;
			updateGeneration();
			genCount++;
		}
		
		long endTime = System.currentTimeMillis();
		delaySum += (endTime - startTime);
		
	}

	public void mousePressed() {

		int mx = this.mouseX;
		int my = this.mouseY;
		int mb = this.mouseButton;
		
		//39 = right click and 37 = left click
		
		if (InputHandler.checkRectangularBounds(screenSizeX+50,50,50,25,mx,my)) {
			running ^= true;
		}
		else if (InputHandler.checkRectangularBounds(screenSizeX+50, 100, 50, 25, mx, my)) {
			//Reset Button
			for (int i = 0; i < cell.length; i++) {
				for (int j = 0; j < cell[i].length; j++) {
					cell[i][j] = 0;
				}
			}
			genCount = 0;
		}
		
		else if (mx < screenSizeX){
			int cellX = InputHandler.getClickGrid(mx, cellSizeX);
			int cellY = InputHandler.getClickGrid(my, cellSizeY);
			
			if (mb == 37) {
				cell[cellX][cellY] = 1;
			}
			else {
				cell[cellX][cellY] = 0;
			}
		}
		
	}

	
	public void updateGeneration() {
		
		//Initializing the tempArray;
		int[][] tempArr = new int[cell.length][cell[1].length];
		

		
		//GameOfLife
		if (this.rules == CellWindow.ruleSet.gameOfLife) {
		
			for (int i = 0; i < cell.length; i++) {
				for (int j = 0; j < cell[i].length; j++) {
				
					int num = liveNeighbors(cell, i, j);
					if (cell[i][j] == 1) {
						if (num < 2) {
							tempArr[i][j] = 0;
						}
						else if (num == 2 || num == 3) {
							tempArr[i][j] = 1;
						}
						else {
							tempArr[i][j] = 0;
						}
					}
					else {
						if (num == 3) {
							tempArr[i][j] = 1;
						}
					}
		
				}
			}

		}
		//Growth1
		else if (this.rules == CellWindow.ruleSet.growth1) {
			
			for (int i = 0; i < cell.length; i++) {
				for (int j = 0; j < cell[i].length; j++) {
					if (cell[i][j] == 0) {
						if (liveNeighbors(cell, i, j) == 1) {
							tempArr[i][j] = 1;
						}
					}
					if (cell[i][j] == 1) {
						if (liveNeighbors(cell, i, j) > 2) {
							tempArr[i][j] = 0;
						}
						else {
							tempArr[i][j] = 1;
						}
					}
				}
			}
			
		}
		
		else if (this.rules == CellWindow.ruleSet.growth2) {
			
			for (int i = 0; i < cell.length; i++) {
				for (int j = 0; j < cell[i].length; j++) {
					if (cell[i][j] == 1) {
						tempArr[i][j] = 1;
					}
					else if (cell[i][j] == 0) {
						if (liveNeighbors(cell,i,j) > 0){
							tempArr[i][j] = 1;
						}
					}
				}
			}
		}
		
		cell = tempArr;
	}
	
	public int liveNeighbors(int[][] cellArr, int x, int y) {
		
		//Checks how many adjacent cells are alive (Up to 8)
		
		int count = 0;
		int maxX = cellArrWidth-1;
		int maxY = cellArrHeight-1;
		
		if (y < maxY) {
			count += cellArr[x][y+1];
		}
		
		if (y > 0) {
			count += cellArr[x][y-1];
		}
		
		if(x < maxX) {
			count += cellArr[x+1][y];
		}
		
		if (x > 0) {
			count += cellArr[x-1][y];
		}
		
		if (x > 0 && y > 0) {
			count += cellArr[x-1][y-1];
		}
		
		if (x < maxX && y < maxY) {
			count += cellArr[x+1][y+1];
		}
		
		if (x > 0 && y < maxY) {
			count += cellArr[x-1][y+1];
		}
		
		if (x < maxX && y > 0) {
			count += cellArr[x+1][y-1];
		}
		
		return count;
	}
}
