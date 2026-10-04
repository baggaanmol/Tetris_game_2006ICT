package tetris.game;

import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

/**
 * Deterministic placement AI.
 *
 * <p>The AI evaluates every legal rotation and column, simulates the drop,
 * clears completed rows, and chooses the board with the best combination of
 * lines, low stack height, few holes, and a smooth surface. This is the same
 * style of board search used by practical Tetris-playing agents; it is not
 * a random auto-player.</p>
 */
public class AI {
    private final boardeval evaluator;

    public AI(boardeval eval) {
        this.evaluator = eval;
    }

    public record Move(int rotation, int column, int row, int score) {}

    private static final Map<String, int[][]> BASE_SHAPES = new HashMap<>();
    private static final Map<String, int[][][]> SHAPES = new HashMap<>();

    static {
        BASE_SHAPES.put("l", new int[][]{
                {0, 0}, {0, 1}, {1, 1}, {2, 1}
        });
        BASE_SHAPES.put("ll", new int[][]{
                {2, 0}, {0, 1}, {1, 1}, {2, 1}
        });
        BASE_SHAPES.put("square", new int[][]{
                {0, 0}, {1, 0}, {0, 1}, {1, 1}
        });
        BASE_SHAPES.put("s", new int[][]{
                {2, 0}, {1, 0}, {1, 1}, {0, 1}
        });
        BASE_SHAPES.put("zig", new int[][]{
                {1, 0}, {0, 0}, {1, 1}, {2, 1}
        });
        BASE_SHAPES.put("t", new int[][]{
                {0, 0}, {1, 0}, {1, 1}, {2, 0}
        });
        BASE_SHAPES.put("line", new int[][]{
                {0, 0}, {1, 0}, {2, 0}, {3, 0}
        });

        for (Map.Entry<String, int[][]> entry : BASE_SHAPES.entrySet()) {
            int[][][] states = new int[4][][];
            int[][] current = normalize(entry.getValue());
            for (int index = 0; index < states.length; index++) {
                states[index] = current;
                current = rotateClockwise(current);
            }
            SHAPES.put(entry.getKey(), states);
        }
    }

    private static int[][] rotateClockwise(int[][] cells) {
        int[][] rotated = new int[cells.length][2];
        for (int index = 0; index < cells.length; index++) {
            rotated[index][0] = -cells[index][1];
            rotated[index][1] = cells[index][0];
        }
        return normalize(rotated);
    }

    private static int[][] normalize(int[][] cells) {
        int minColumn = Integer.MAX_VALUE;
        int minRow = Integer.MAX_VALUE;
        for (int[] cell : cells) {
            minColumn = Math.min(minColumn, cell[0]);
            minRow = Math.min(minRow, cell[1]);
        }

        int[][] normalized = new int[cells.length][2];
        for (int index = 0; index < cells.length; index++) {
            normalized[index][0] = cells[index][0] - minColumn;
            normalized[index][1] = cells[index][1] - minRow;
        }
        return normalized;
    }

    private static int maxColumn(int[][] cells) {
        int max = 0;
        for (int[] cell : cells) {
            max = Math.max(max, cell[0]);
        }
        return max;
    }

    private static int maxRow(int[][] cells) {
        int max = 0;
        for (int[] cell : cells) {
            max = Math.max(max, cell[1]);
        }
        return max;
    }

    private static int[][] cloneBoard(int[][] board) {
        int[][] copy = new int[board.length][];
        for (int column = 0; column < board.length; column++) {
            copy[column] = board[column].clone();
        }
        return copy;
    }

    private static boolean canPlace(
            int[][] board,
            int[][] cells,
            int columnOffset,
            int rowOffset
    ) {
        for (int[] cell : cells) {
            int column = columnOffset + cell[0];
            int row = rowOffset + cell[1];
            if (column < 0 || column >= board.length
                    || row < 0 || row >= board[0].length
                    || board[column][row] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the lowest legal row reachable from the top of the board.
     */
    private static int findLandingRow(
            int[][] board,
            int[][] cells,
            int columnOffset
    ) {
        int highestRow = maxRow(cells);
        int lastValidRow = -1;
        for (int row = 0; row <= board[0].length - highestRow - 1; row++) {
            if (!canPlace(board, cells, columnOffset, row)) {
                break;
            }
            lastValidRow = row;
        }
        return lastValidRow;
    }

    private static void place(
            int[][] board,
            int[][] cells,
            int columnOffset,
            int rowOffset
    ) {
        for (int[] cell : cells) {
            board[columnOffset + cell[0]][rowOffset + cell[1]] = 1;
        }
    }

    /**
     * Removes full rows from a simulated board and returns their count.
     */
    private static int clearLines(int[][] board) {
        int cleared = 0;
        for (int row = board[0].length - 1; row >= 0; row--) {
            boolean full = true;
            for (int column = 0; column < board.length; column++) {
                if (board[column][row] == 0) {
                    full = false;
                    break;
                }
            }
            if (!full) {
                continue;
            }

            cleared++;
            for (int shiftedRow = row; shiftedRow > 0; shiftedRow--) {
                for (int column = 0; column < board.length; column++) {
                    board[column][shiftedRow] = board[column][shiftedRow - 1];
                }
            }
            for (int column = 0; column < board.length; column++) {
                board[column][0] = 0;
            }
            row++;
        }
        return cleared;
    }

    /**
     * Searches all legal placements and evaluates the resulting board.
     */
    public Move bestMove(form piece, int[][] board) {
        if (piece == null || board == null || board.length == 0) {
            return null;
        }

        int[][][] states = SHAPES.get(piece.getName());
        if (states == null) {
            return null;
        }

        Move best = null;
        for (int rotation = 0; rotation < states.length; rotation++) {
            int[][] cells = states[rotation];
            int shapeWidth = maxColumn(cells) + 1;
            for (int column = 0;
                 column <= board.length - shapeWidth;
                 column++) {
                int row = findLandingRow(board, cells, column);
                if (row < 0) {
                    continue;
                }

                int[][] candidate = cloneBoard(board);
                place(candidate, cells, column, row);
                int linesCleared = clearLines(candidate);
                int score = evaluate(candidate, linesCleared);

                if (best == null || score > best.score()) {
                    best = new Move(rotation, column, row, score);
                }
            }
        }
        return best;
    }

    private int evaluate(int[][] board, int linesCleared) {
        // The line-clear reward dominates survival penalties.
        return (linesCleared * 1_200)
                + evaluator.evaluate(board)
                - (evaluator.maximumHeight(board) * 8)
                - (evaluator.holes(board) * 80)
                - (evaluator.bumpiness(board) * 12);
    }

    public void applyMove(form piece, Move move) {
        if (piece == null || move == null) {
            return;
        }
        int[][] cells = SHAPES.get(piece.getName())[move.rotation()];
        Rectangle[] blocks = {piece.a, piece.b, piece.c, piece.d};
        for (int index = 0; index < blocks.length; index++) {
            blocks[index].setX(
                    (move.column() + cells[index][0]) * Tetris.size);
            blocks[index].setY(
                    (move.row() + cells[index][1]) * Tetris.size);
        }
        piece.form = move.rotation() + 1;
    }

    /**
     * Places an AI-controlled piece at the top of its planned column. Normal
     * gravity then moves it visibly to the landing row instead of teleporting
     * it directly onto the stack.
     */
    public void positionAtSpawn(form piece, Move move) {
        if (piece == null || move == null) {
            return;
        }
        int[][] cells = SHAPES.get(piece.getName())[move.rotation()];
        Rectangle[] blocks = {piece.a, piece.b, piece.c, piece.d};
        for (int index = 0; index < blocks.length; index++) {
            blocks[index].setX(
                    (move.column() + cells[index][0]) * Tetris.size);
            blocks[index].setY(cells[index][1] * Tetris.size);
        }
        piece.form = move.rotation() + 1;
    }

    public Move play(form piece, int[][] board) {
        Move move = bestMove(piece, board);
        applyMove(piece, move);
        return move;
    }
}