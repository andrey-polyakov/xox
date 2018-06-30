package t3.core.data.structures;

import java.util.Set;

/**
 * Indicates which players may win by marking the cell.
 */
public class WinningCoordinate extends BoardCellCoordinates {
    private Set<Integer> players;

    public WinningCoordinate(BoardCellCoordinates coordinates, Set<Integer> players) {
        super(coordinates.getRow(), coordinates.getColumn());
        this.players = players;
    }

    public Set<Integer> getPlayers() {
        return players;
    }

    public void mergePlayers(WinningCoordinate winningCoordinate) {
        players.addAll(winningCoordinate.getPlayers());
    }
}
