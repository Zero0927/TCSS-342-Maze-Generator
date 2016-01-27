//Siyuan Zhou
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class Maze {
	private Cell[][] G;
	private char[][] printG;
	private char[][] solutionG;
	private int cols;
	private int rows;
	private Cell start;
	private Cell end;
	private boolean debug;
	final static char WALL_CHAR = 'X';
	final static char VISITED_CHAR = 'V';
	final static char PATH_CHAR = '+';

	public Maze(int width, int depth, boolean debug) {
		this.debug = debug;
		this.cols = width;
		this.rows = depth;
		G = new Cell[depth][width];
		printG = new char[2 * depth + 1][2 * width + 1];
		solutionG = new char[2 * depth + 1][2 * width + 1];
		for (int i = 0; i < depth; i++)
			for (int j = 0; j < width; j++)
				G[i][j] = new Cell(i, j);
		start = G[0][0];
		start.setUpWall(false);
		end = G[depth - 1][width - 1];
		end.setDownWall(false);
		// create the maze
		generate();
		// find the solution of this G
		solvMaze();
	}

	/**
	 * solve maze
	 */
	public void solvMaze() {
		for (int i = 0; i < this.rows; i++)
			for (int j = 0; j < this.cols; j++)
				G[i][j].setVisited(false);
		Stack<Cell> path = new Stack<Cell>();
		path.push(start);
		start.setVisited(true);
		Cell currentCell = start;
		Cell lastCell = start;
		boolean popFlag = false;
		while (!path.isEmpty()) {
			// get the sequence cells which are not visited and are around
			// current cell
			Set<Entry<Cell, Integer>> randomSet = randomSeqOfCell(currentCell)
					.entrySet();
			boolean flag = false;// the flag means there is a cell which can
									// achieve from last cell
			for (Entry<Cell, Integer> entry : randomSet) {
				// get the cell from sequence cells which can achieve from last
				// cell
				switch (entry.getValue()) {
				case 0:
					if (!lastCell.getUpWall()) {
						flag = true;
					}
					break;
				case 1:
					if (!lastCell.getRightWall()) {
						flag = true;
					}
					break;
				case 2:
					if (!lastCell.getDownWall()) {
						flag = true;
					}
					break;
				case 3:
					if (!lastCell.getLeftWall()) {
						flag = true;
					}
					break;
				}
				if (flag) {
					if (popFlag) {
						path.push(lastCell);
						popFlag = false;
					}
					currentCell = entry.getKey();
					lastCell = currentCell;
					currentCell.setVisited(true);
					path.push(currentCell);

					if (currentCell == end) {
						// get the solution of this maze
						for (int pathi = 0, len = path.size(); pathi < len; pathi++) {
							path.elementAt(pathi).setIsPath(true);
						}
						currentCell.setIsPath(true);
					}
					// display();
					break;
				}
			}
			// there is no cell which can achieve from last cell
			if (!flag) {
				popFlag = true;
				currentCell = path.pop();
				lastCell = currentCell;
			}
		}
	}

	public Cell[][] getG() {
		return G;
	}

	/**
	 * create the maze
	 */
	public void generate() {
		Stack<Cell> stack = new Stack<>();
		stack.push(start);
		start.setVisited(true);
		Cell currentCell = start;
		Cell lastCell;
		while (!stack.isEmpty()) {
			HashMap<Cell, Integer> randomSeq = randomSeqOfCell(currentCell);
			currentCell.setVisited(true);
			if (randomSeq.size() <= 0) {
				currentCell = stack.pop();
			} else {
				lastCell = currentCell;
				int seq = -1;
				for (Entry<Cell, Integer> en : randomSeq.entrySet()) {
					currentCell = en.getKey();
					seq = en.getValue();
					break;
				}

				// set the wall of related cell
				switch (seq) {
				case 0:
					lastCell.setUpWall(false);
					currentCell.setDownWall(false);
					break;
				case 1:
					lastCell.setRightWall(false);
					currentCell.setLeftWall(false);
					break;
				case 2:
					lastCell.setDownWall(false);
					currentCell.setUpWall(false);
					break;
				case 3:
					lastCell.setLeftWall(false);
					currentCell.setRightWall(false);
					break;
				}
				stack.push(currentCell);
			}
			if (debug) {
				// set the printG with char 'V'
				initPrintG(VISITED_CHAR, printG);
				// debug the maze
				print();
			} else {
				initPrintG(VISITED_CHAR, printG);
			}
		}
	}

	/**
	 * get the random sequence of a certain cell
	 * 
	 * @param cell
	 *            the certain cell
	 * @return the random sequence of a certain cell
	 */
	public HashMap<Cell, Integer> randomSeqOfCell(Cell cell) {

		HashMap<Cell, Integer> positions = new HashMap<Cell, Integer>();
		// get the position of the cell
		int rowOfCell = cell.getPosition()[0];
		int colOfCell = cell.getPosition()[1];
		// create a new sequence
		int seq[] = new int[] { -1, -1, -1, -1 };

		Random rand = new Random();
		int seqi = 0;
		while (seqi < 4) {
			boolean addAvi = true;
			int nexti = rand.nextInt(4);
			for (int i = 0; i < seqi; i++) {
				if (seq[i] == nexti) {
					addAvi = false;
					break;
				}
			}
			if (addAvi) {
				seq[seqi++] = nexti;
			}
		}
		for (int i = 0; i < seqi; i++) {
			int newRowOfCell = rowOfCell, newColOfCell = colOfCell;
			switch (seq[i]) {
			case 0:// up
				newRowOfCell = rowOfCell - 1;
				break;
			case 1:// right
				newColOfCell = colOfCell + 1;
				break;
			case 2:// down
				newRowOfCell = rowOfCell + 1;
				break;
			case 3:// left
				newColOfCell = colOfCell - 1;
				break;

			}
			if (newRowOfCell > -1 && newRowOfCell < this.rows
					&& newColOfCell > -1 && newColOfCell < this.cols
					&& !G[newRowOfCell][newColOfCell].getVisited())
				positions.put(G[newRowOfCell][newColOfCell], seq[i]);
		}
		return positions;
	}

	/**
	 * init the printG based on G. if the CellFlag is '+', then init the
	 * solution of G. if the CellFlag is 'V', then init the maze
	 * 
	 * @param CellFlag
	 */
	public void initPrintG(char CellFlag, char[][] pathG) {
		int printGHeight = pathG.length;
		int printGWidth = pathG[0].length;

		for (int i = 0; i < printGHeight; i++) {
			// init the top wall of printG
			if (i == 0) {
				for (int j = 0; j < printGWidth; j++) {
					if (!(j % 2 == 0) && !G[i][j / 2].getUpWall()) {
						pathG[i][j] = ' ';
					} else {
						pathG[i][j] = WALL_CHAR;
					}
				}
			} else {// init other wall of printG
				for (int j = 0; j < printGWidth; j++) {
					if (i % 2 == 0) {// init even row of printG
						if (j % 2 == 0) { // init even row and even column of
											// printG
							pathG[i][j] = WALL_CHAR;
						} else {
							if (G[i / 2 - 1][j / 2].getDownWall()) {
								pathG[i][j] = WALL_CHAR;
							} else {
								pathG[i][j] = ' ';
							}
						}
					} else {// init odd row of printG
						if (j % 2 == 0) {// init odd row and even column of
											// printG
							if (j < printGWidth - 1) {
								if (G[i / 2][j / 2].getLeftWall()) {
									pathG[i][j] = WALL_CHAR;
								} else {
									pathG[i][j] = ' ';
								}
							} else {// init odd row and last column of printG
								if (G[i / 2][j / 2 - 1].getRightWall()) {
									pathG[i][j] = WALL_CHAR;
								} else {
									pathG[i][j] = ' ';
								}
							}
						} else {
							// init the odd row and odd column of printG that is
							// the cell of G
							if (CellFlag == PATH_CHAR) {
								if (G[i / 2][j / 2].getIsPath()) {
									pathG[i][j] = CellFlag;
								} else {
									pathG[i][j] = ' ';
								}
							} else {
								if (G[i / 2][j / 2].getVisited()) {
									pathG[i][j] = CellFlag;
								} else {
									pathG[i][j] = ' ';
								}
							}
						}
					}
				}
			}

		}
	}

	/**
	 * print the printG, that is the maze without solution
	 */
	public void print() {
		int printGHeight = printG.length;
		int printGWidth = printG[0].length;
		System.out.println("\nthe maze:");
		for (int i = 0; i < printGHeight; i++) {
			for (int j = 0; j < printGWidth; j++) {
				System.out.print(printG[i][j]);
			}
			System.out.println();
		}
	}

	/**
	 * print the printG, that is the maze
	 */
	public void display() {
		int printGHeight = printG.length;
		int printGWidth = printG[0].length;
		System.out.println("\nthe maze:");
		// set the printG with char 'V'
		for (int i = 0; i < printGHeight; i++) {
			for (int j = 0; j < printGWidth; j++) {
				System.out.print(printG[i][j]);
			}
			System.out.println();
		}

		System.out.println("\nthe solution of maze:");
		// set the printG with char '+' to print the solution of G
		initPrintG(PATH_CHAR, solutionG);
		for (int i = 0; i < printGHeight; i++) {
			for (int j = 0; j < printGWidth; j++) {
				System.out.print(solutionG[i][j]);
			}
			System.out.println();
		}
	}

	class Cell {
		private int position[];// record the position of cell in maze

		// record the wall around this cell
		private boolean wall[] = new boolean[] { true, true, true, true };

		private boolean visited = false;
		private boolean isPath = false;

		public Cell(int i, int j) {
			position = new int[] { i, j };
		}

		public void setIsPath(boolean isPath) {
			this.isPath = isPath;
		}

		public boolean getIsPath() {
			return isPath;
		}

		public void setUpWall(boolean upWall) {
			wall[0] = upWall;
		}

		public boolean getUpWall() {
			return wall[0];
		}

		public void setRightWall(boolean rightWall) {
			wall[1] = rightWall;
		}

		public boolean getRightWall() {
			return wall[1];
		}

		public void setDownWall(boolean downWall) {
			wall[2] = downWall;
		}

		public boolean getDownWall() {
			return wall[2];
		}

		public void setLeftWall(boolean leftWall) {
			wall[3] = leftWall;
		}

		public boolean getLeftWall() {
			return wall[3];
		}

		public void setVisited(boolean v) {
			visited = v;
		}

		public boolean getVisited() {
			return visited;
		}

		public int[] getPosition() {
			return position;
		}

		public String toString() {
			String wallStr = "";
			for (boolean b : wall) {
				wallStr += String.valueOf(b) + ",";
			}
			return "position: (" + position[0] + "," + position[1]
					+ "), wall:[" + wallStr.substring(0, wallStr.length() - 1)
					+ "]";
		}

		public void print() {
			System.out.println(this.toString());
		}
	}
}
