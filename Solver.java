/* *****************************************************************************
 *  Name: Alain Plana
 *  Date: 24.05.2020
 *  Description: Solver data type. implement A* search to solve n-by-n slider puzzles.
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Solver {

    private final Stack<Board> boards;
    private final int moves;
    private final boolean solvable;


    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();

        MinPQ<Seeker> minPQ = new MinPQ<>();
        minPQ.insert(new Seeker(initial, 0, null));

        Board twin = initial.twin();

        MinPQ<Seeker> minPQ1 = new MinPQ<>();
        minPQ1.insert(new Seeker(twin, 0, null));

        Answer answer = solve(minPQ, minPQ1);
        this.moves = answer.getSteps();
        this.solvable = answer.isSolvable();
        this.boards = from(answer.getSeeker());
    }

    private Stack<Board> from(Seeker s) {
        Stack<Board> stack = new Stack<>();
        for (; s.prev != null; s = s.prev) stack.push(s.board);
        stack.push(s.board);
        return stack;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() { return this.solvable; }

    // min number of moves to solve initial board
    public int moves() { return this.moves; }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        return (moves != -1) ? new End() : null;
    }

    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    private class Answer {
        private Seeker seeker;
        private int steps;
        private boolean solvable;

        private Answer(Seeker solution, int moves, boolean isSolvable) {
            this.setSteps(moves);
            this.setSolvable(isSolvable);
            this.setSeeker(solution);
        }

        public Seeker getSeeker() {
            return seeker;
        }

        public void setSeeker(Seeker seeker) {
            this.seeker = seeker;
        }

        public int getSteps() {
            return steps;
        }

        public void setSteps(int steps) {
            this.steps = steps;
        }

        public boolean isSolvable() {
            return solvable;
        }

        public void setSolvable(boolean solvable) {
            this.solvable = solvable;
        }
    }

    private Answer solve(MinPQ<Seeker> minPQ, MinPQ<Seeker> minPQ1) {
        Seeker min = minPQ.min();
        Seeker seeker = minPQ.min();

        while (!min.board.isGoal() || !seeker.board.isGoal()) {
            min = minPQ.delMin();
            seeker = minPQ1.delMin();

            for (Board board : min.board.neighbors()) {
                if (board.isGoal()) {
                    min = new Seeker(board, min.steps + 1, min);
                    return new Answer(min, min.steps, true);
                }
                if (min.prev == null || !min.prev.board.equals(board))
                    minPQ.insert(new Seeker(board, min.steps + 1, min));
            }

            for (Board board: seeker.board.neighbors()) {
                if (board.isGoal()) return new Answer(null, -1, false);
                if (seeker.prev == null || !seeker.prev.board.equals(board))
                    minPQ1.insert(new Seeker(board, seeker.steps +1, seeker));
            }
        }

        return  (min.board.isGoal())
                ? new Answer(min, min.steps, true)
                : new Answer(null, -1, false);
    }

    private class Seeker implements Comparable<Seeker> {
        private final Board board;
        private final int steps;
        private final Seeker prev;
        private int priority;

        public Seeker(Board board, int moves, Seeker previous) {
            this.board = board;
            this.steps = moves;
            this.prev = previous;
            setPriority(moves + board.manhattan());
        }

        public int compareTo(Seeker that) {
            return Integer.compare(this.getPriority(), that.getPriority());
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
    }

    private class End implements Iterable<Board> {

        public Iterator<Board> iterator() { return new EndIterator(); }

        private class EndIterator implements Iterator<Board> {
            public boolean hasNext() { return !Solver.this.boards.isEmpty(); }
            public Board next() {
                if (Solver.this.boards.isEmpty())
                    throw new NoSuchElementException();
                return Solver.this.boards.pop();
            }
            public void remove() { throw new UnsupportedOperationException(); }
        }
    }
}
