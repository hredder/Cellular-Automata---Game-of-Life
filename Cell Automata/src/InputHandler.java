
public class InputHandler {

	
	public static boolean checkRectangularBounds(int x1, int y1, int w, int h, int mx1, int my1) {
		
		//Checks if a click is in a certain rectangular area
		
		if (mx1 > x1 && mx1 < (x1+w)) {
			if (my1 > y1 && my1 < (y1+h)) {
				return true;
			}
		}
		return false;
		
	}
	public static int getClickGrid(int m, int cellSize) {
		
		return m/cellSize;
		
	}
}
