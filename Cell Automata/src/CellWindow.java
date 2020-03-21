import processing.core.*;

public class CellWindow extends PApplet {
	
	//An array of 0's and 1's: 1 = alive, 0 = dead (You can change this to 100x100 or 200x200 but starts to break after that
	public static int[][] cell = new int[50][50];
	
	//The length in pixels of the cell(This program does not support non square arrays)
	public static int cellSize;
	
	//Boolean on whether the updateGeneration method is being called
	public boolean running = false;
	
	//Time in milleseconds between each generation
	public long delayTime = 15;
	
	//Countger for this time
	public long delaySum = 0;
	
	public static void main(String[] args) {
		
		//Creates window
		String[] pargs = {""};
		CellWindow cw = new CellWindow();
		PApplet.runSketch(pargs, cw);
		cellSize = 600/cell.length;
		
		//Initiates cell array with 0's
		
		for (int i = 0; i < cell.length; i++) {
			for (int j = 0; j < cell[i].length; j++) {
				cell[i][j] = 0;
			}
		}
	
	}
	
	public void settings() {
		this.size(800,600);
	}
	
	public void drawUI() {
		
		//Drawing the grid
		this.stroke(0,255,0);
		this.fill(0,255,0);
		for (int i = 0; i < cell.length; i++) {
			
			this.line(0, i*cellSize, 600, i*cellSize);
			this.line(i*cellSize, 0, i*cellSize, 600);
		}
		
		//Drawing buttons
		this.rect(650,50,50,25);
		this.rect(650,100,50,25);
		this.fill(0,0,0);
		
		this.text("Reset", 650, 115);
		
		if (!running) {
			this.text("Start", 650, 65);
		}
		else {
			this.text("Pause", 650, 65);
		}
	}
	
	public void drawCells() {
		fill(255,0,0);
		for (int i = 0; i < cell.length; i++) {
			for (int j = 0; j < cell[i].length; j++) {
				if (cell[i][j] == 1) {
					this.rect(i*cellSize, j*cellSize, cellSize, cellSize);
				}
			}
		}
	}
	
	public void draw() {
		
		long startTime = System.currentTimeMillis();
		
		this.background(0);
		drawCells();
		drawUI();
		if (running && delaySum > delayTime) {
			delaySum = 0;
			updateGeneration();
		}
		
		long endTime = System.currentTimeMillis();
		delaySum += (endTime - startTime);
		
	}

	public void mousePressed() {

		int mx = this.mouseX;
		int my = this.mouseY;
		
		//Switches the value of running if the text box at the top is clicked
		if (InputHandler.checkRectangularBounds(650,50,50,25,mx,my)) {
			running ^= true;
		}
		else if (InputHandler.checkRectangularBounds(650, 100, 50, 25, mx, my)) {
			for (int i = 0; i < cell.length; i++) {
				for (int j = 0; j < cell[i].length; j++) {
					cell[i][j] = 0;
				}
			}
		}
		
		else if (mx < 600 && !running){
			int cellX = InputHandler.getClickGrid(mx, cellSize);
			int cellY = InputHandler.getClickGrid(my, cellSize);
			
			cell[cellX][cellY] = 1;
		}
		
	}
	
	//Numerical methods below
	
	public void updateGeneration() {
		
		//This method implements the rules for Conway's game of life
		
		int[][] tempArr = new int[cell.length][cell[1].length];
		
		for (int i = 0; i < cell.length; i++) {
			for (int j = 0; j < cell[i].length; j++) {
				tempArr[i][j] = 0;
			}
		}
		
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
		
		cell = tempArr;
	}
	
	public int liveNeighbors(int[][] cellArr, int x, int y) {
		
		//Checks how many adjacent cells are alive (Up to 8)
		
		int count = 0;
		int max = cell.length-1;
		if (y < max) {
			count += cellArr[x][y+1];
		}
		
		if (y > 0) {
			count += cellArr[x][y-1];
		}
		
		if(x < max) {
			count += cellArr[x+1][y];
		}
		
		if (x > 0) {
			count += cellArr[x-1][y];
		}
		
		if (x > 0 && y > 0) {
			count += cellArr[x-1][y-1];
		}
		
		if (x < max && y < max) {
			count += cellArr[x+1][y+1];
		}
		
		if (x > 0 && y < max) {
			count += cellArr[x-1][y+1];
		}
		
		if (x < 49 && y > 0) {
			count += cellArr[x+1][y-1];
		}
		
		return count;
	}
}
