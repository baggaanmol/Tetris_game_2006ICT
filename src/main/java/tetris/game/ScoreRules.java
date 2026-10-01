package tetris.game;

/**
 * The course project's score rule: award 50 points for each completed row.
 */
public final class ScoreRules {
    public static final int POINTS_PER_LINE = 50;

    private ScoreRules() {
    }

    public static int pointsForClearedLines(int clearedLines) {
        if (clearedLines < 0) {
            throw new IllegalArgumentException(
                    "Cleared line count cannot be negative");
        }
        return Math.multiplyExact(clearedLines, POINTS_PER_LINE);
    }
}