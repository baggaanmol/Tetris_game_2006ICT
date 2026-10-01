package tetris.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure grid operations shared by gameplay and regression tests.
 *
 * <p>The board is stored as {@code grid[column][row]} to match the game and
 * AI code.</p>
 */
public final class BoardModel {
    private BoardModel() {
    }

    /**
     * Clears every completed row in one pass and compacts the remaining rows
     * toward the bottom. Returns the original row indexes that were cleared.
     */
    public static List<Integer> clearCompletedRows(int[][] grid) {
        int height = validateGrid(grid);
        int width = grid.length;
        boolean[] completed = new boolean[height];
        List<Integer> clearedRows = new ArrayList<>();

        for (int row = 0; row < height; row++) {
            boolean full = true;
            for (int column = 0; column < width; column++) {
                if (grid[column][row] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                completed[row] = true;
                clearedRows.add(row);
            }
        }

        if (clearedRows.isEmpty()) {
            return List.of();
        }

        int destinationRow = height - 1;
        for (int sourceRow = height - 1; sourceRow >= 0; sourceRow--) {
            if (completed[sourceRow]) {
                continue;
            }
            if (destinationRow != sourceRow) {
                for (int column = 0; column < width; column++) {
                    grid[column][destinationRow] = grid[column][sourceRow];
                }
            }
            destinationRow--;
        }

        for (int row = destinationRow; row >= 0; row--) {
            for (int column = 0; column < width; column++) {
                grid[column][row] = 0;
            }
        }

        return List.copyOf(clearedRows);
    }

    private static int validateGrid(int[][] grid) {
        if (grid == null || grid.length == 0
                || grid[0] == null || grid[0].length == 0) {
            throw new IllegalArgumentException(
                    "Board must have at least one column and one row");
        }

        int height = grid[0].length;
        for (int[] column : grid) {
            if (column == null || column.length != height) {
                throw new IllegalArgumentException(
                        "Board columns must have equal heights");
            }
        }
        return height;
    }
}