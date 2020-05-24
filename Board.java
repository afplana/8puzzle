/* *****************************************************************************
 *  Name: Alain Plana
 *  Date: 17/5/2020
 *  Description: Inmutable representation of Board of NxN tiles
 **************************************************************************** */

import edu.princeton.cs.algs4.Stack;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Board {

    private final int[][] tiles;
    private final int dimension;
    private final int hamming;
    private final int manhattan;
    private final boolean goal;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.tiles = tiles.clone();
        this.dimension = this.tiles.length;
        this.hamming = hmmng();
        this.manhattan = mnhttn();
        this.goal = goalBoard();
    }

    // string representation of this board
    public String toString() {
        StringBuilder tilesRep = new StringBuilder();
        tilesRep.append(dimension+System.lineSeparator());
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) tilesRep.append(" ").append(tiles[i][j]);
            tilesRep.append(System.lineSeparator());
        }
        return tilesRep.toString();
    }

    // board dimension n
    public int dimension() {
        return this.dimension;
    }

    // number of tiles out of place
    public int hamming() {
        return this.hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return this.manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return this.goal;
    }

    // does this board equal y?
    public boolean equals(Object that) {
        if (that == null) return false;

        Board board;
        try {
            board = (Board) that;
        } catch (ClassCastException e) { return false;       }

        if (board.dimension() != this.dimension) return false;

        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++)
                if (this.tiles[i][j] != board.tiles[i][j]) return false;
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return new Neighbors(this.tiles);
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        return new Board(exchd(tiles, dimension));
    }

    // AUx methods

    private int mod(int i) {
        return (i > 0) ? +i : -i;
    }

    private int hmmng() {
        int hmmng = 0;
        for (int i = 0; i < this.dimension; i++)
            for (int j = 0; j < this.dimension; j++) hmmng = hmmngFormula(hmmng, i, j);
        return hmmng;
    }



    private int mnhttn() {
        int mnhttn = 0;
        for (int i = 0; i < this.dimension; i++)
            for (int j = 0; j < this.dimension; j++) mnhttn = mnhttnFormula(mnhttn, i, j);
        return mnhttn;
    }

    private int mnhttnFormula(int mnhttn, int i, int j) {
        if (tiles[i][j] != 0)
            mnhttn += mod(((tiles[i][j] - 1) / this.dimension) - i) + mod(
                    ((tiles[i][j] - 1) % this.dimension) - j);
        return mnhttn;
    }

    private int hmmngFormula(int hmmng, int i, int j) {
        if (this.tiles[i][j] != (i * dimension + j + 1)
                && this.tiles[i][j] != 0) hmmng++;
        return hmmng;
    }

    private boolean goalBoard() {
        for (int i = 0; i < this.dimension; i++)
            for (int j = 0; j < this.dimension; j++)
                if (this.tiles[i][j] != ((i * this.dimension + j + 1) % (this.dimension
                        * this.dimension)))
                    return false;
        return true;
    }

    private static int[][] exchd(int[][] ints, int length) {
        int x = 0;
        int y = 0;
        for_1:
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (ints[i][j] != 0) {
                    x = i;
                    y = j;
                    break for_1;
                }
            }
        }

        int m = length-1;
        int n = length-1;
        for_2:
        for (int i = length-1; i >= 0; i--) {
            for (int j = length-1; j >= 0; j--) {
                if (ints[i][j] != 0 && i != x && j != y) {
                    m = i;
                    n = j;
                    break for_2;
                }
            }
        }

        return exch(ints, x, y, m, n);
    }

    private static int[][] exch(int[][] input, int i, int j, int k, int x) {
        int[][] exchd = input.clone();
        int swap = exchd[i][j];
        exchd[i][j] = exchd[k][x];
        exchd[k][x] = swap;
        return exchd;
    }

    private class Neighbors implements  Iterable<Board> {
        private final int[][] ints;
        private final Stack<Board> neighbors = new Stack<>();

        public Neighbors(int[][] ints) {
            this.ints = ints.clone();
            getNeighbors();
        }

        private void getNeighbors() {
            int x = 0;
            int y = 0;
            for (int i = 0; i < dimension; i++)
                for (int j = 0; j < dimension; j++) {
                    if (ints[i][j] == 0) {
                        x = i;
                        y = j;
                    }
                }

            int[][] nghbrs = {{x-1, y}, {x+1, y}, {x, y-1}, {x, y+1}};
            for (int i = 0; i < nghbrs.length; i++) {
                try { neighbors.push(new Board(exch(ints, x, y, nghbrs[i][0], nghbrs[i][1]))); }
                catch (IndexOutOfBoundsException e) { /* Do nothing. */ }
            }
        }

        public Iterator<Board> iterator() { return new NeighborsIterator(); }

        private class NeighborsIterator implements Iterator<Board> {
            public boolean hasNext() { return !neighbors.isEmpty(); }
            public Board next() {
                if (neighbors.isEmpty())
                    throw new NoSuchElementException();
                return neighbors.pop(); }
            public void remove() { throw new UnsupportedOperationException(); }
        }
    }
}
