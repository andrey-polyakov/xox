package t3.core.data.structures;

import java.util.Set;

/**
 * Indicates which players may win by marking the cell.
 */
public class WinningCoordinate extends BoardCellCoordinates {
    private Set<Integer> players;

    public WinningCoordinate(int row, int col, Set<Integer> players) {
        super(row, col);
        this.players = players;
    }

    public Set<Integer> getPlayers() {
        return players;
    }

    public void mergePlayers(WinningCoordinate winningCoordinate) {
        players.addAll(winningCoordinate.getPlayers());
    }
}
