package tetris.game;

import java.util.Objects;

/**
 * A single entry in the local leaderboard.
 */
public final class HighScoreEntry {
    private static final int MAX_PLAYER_NAME_LENGTH = 20;

    private final String playerName;
    private final int score;

    public HighScoreEntry(String playerName, int score) {
        if (score < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }

        this.playerName = sanitisePlayerName(playerName);
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    private static String sanitisePlayerName(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return "PLAYER";
        }

        String trimmedName = playerName.trim();
        if (trimmedName.equalsIgnoreCase("anonymous")) {
            return "PLAYER";
        }
        if (trimmedName.length() > MAX_PLAYER_NAME_LENGTH) {
            return trimmedName.substring(0, MAX_PLAYER_NAME_LENGTH);
        }
        return trimmedName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof HighScoreEntry other)) {
            return false;
        }
        return score == other.score && playerName.equals(other.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, score);
    }

    @Override
    public String toString() {
        return playerName + ": " + score;
    }
}