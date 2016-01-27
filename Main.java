//Siyuan Zhou
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
	public final static int DEFAULT_WIDTH = 680;
	public final static int DEFAULT_HEIGHT = 740;
	public final static int CELL_WIDTH = 20;
	public final static int CELL_HEIGHT = 20;
	public final static int LEFT_MARGIN = 20;
	public final static int UP_MARGIN = 20;

	public static void main(String[] args) {

		//generating a 5x5 maze with debugging on to show the steps of algorithm.  
		Maze maze = new Maze(5, 5, true);
		System.out.println();
		maze.display();
		new Main().graphicalDisplay(maze.getG());
		
		//generating a larger maze with debugging off
		Maze mazeLarger = new Maze(40,20,false);
		System.out.println();
		mazeLarger.display();
		new Main().graphicalDisplay(mazeLarger.getG());
		
		//test the components
		Maze.Cell cell = new Maze(2,2,false).new Cell(1, 1);
		System.out.println(cell);
		
	}

	public void graphicalDisplay(Maze.Cell[][] printG) {
		JFrame jf = new JFrame("maze");
		jf.setBounds(50, 50, Main.DEFAULT_WIDTH, Main.DEFAULT_HEIGHT);
		DrawArea da = new Main().new DrawArea(printG);
		da.setPreferredSize(new Dimension(printG[0].length * CELL_WIDTH + 50,
				printG.length * CELL_HEIGHT + 50));
		da.repaint();
		jf.add(da);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);

	}

	class DrawArea extends JPanel {
		private static final long serialVersionUID = -8061289275096161310L;
		private Maze.Cell[][] printG;

		public DrawArea(Maze.Cell[][] printG) {
			this.printG = printG;
		}

		public void setPrintG(Maze.Cell[][] printG) {
			this.printG = printG;
		}

		public void paint(Graphics g) {
			int widthOfPrintG = printG[0].length;
			int heightOfPrintG = printG.length;
			for (int i = 0; i < heightOfPrintG; i++) {
				for (int j = 0; j < widthOfPrintG; j++) {
					if(printG[i][j].getIsPath()){
						g.fillOval(j * CELL_WIDTH + LEFT_MARGIN, i
								* CELL_HEIGHT + UP_MARGIN, CELL_HEIGHT-5,CELL_WIDTH-5);
					}
					if (printG[i][j].getUpWall()) {
						g.drawLine(j * CELL_WIDTH + LEFT_MARGIN, i
								* CELL_HEIGHT + UP_MARGIN, (j + 1) * CELL_WIDTH
								+ LEFT_MARGIN, i * CELL_HEIGHT + UP_MARGIN);
					}
					if (printG[i][j].getRightWall()) {
						g.drawLine((j + 1) * CELL_WIDTH + LEFT_MARGIN, i
								* CELL_HEIGHT + UP_MARGIN, (j + 1) * CELL_WIDTH
								+ LEFT_MARGIN, (i + 1) * CELL_HEIGHT
								+ UP_MARGIN);
					}
					if (printG[i][j].getDownWall()) {
						g.drawLine((j + 1) * CELL_WIDTH + LEFT_MARGIN, (i + 1)
								* CELL_HEIGHT + UP_MARGIN, (j) * CELL_WIDTH
								+ LEFT_MARGIN, (i + 1) * CELL_HEIGHT
								+ UP_MARGIN);
					}
					if (printG[i][j].getLeftWall()) {
						g.drawLine((j) * CELL_WIDTH + LEFT_MARGIN, (i + 1)
								* CELL_HEIGHT + UP_MARGIN, (j) * CELL_WIDTH
								+ LEFT_MARGIN, (i) * CELL_HEIGHT + UP_MARGIN);
					}
				}
			}
		}
	}
}
