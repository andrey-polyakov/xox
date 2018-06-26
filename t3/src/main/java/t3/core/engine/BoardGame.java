package t3.core.engine;

import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;
import t3.core.data.structures.RoundRobin;

import java.util.*;

/**
 * Represents board game and does answer some useful questions. It ensures games goes on according to the rules.
 * <br><br>
 * Terminology wise, every board has cells where players put marks in them. Players want to
 * put marks in winning shapes.
 */
public class BoardGame {
    protected RoundRobin turnsSchedule;
    protected BoardCell[][] cells;
    protected int remainingCells = 0;
    protected Integer winner;

    public BoardGame(Set<Integer> players) {
        turnsSchedule = new RoundRobin(players);
    }

    /**
     * Given player makes a mark in a cell.
     * @param coordinates
     * @param playerId
     * @return
     */
    public int takeTurn(BoardCellCoordinates coordinates, int playerId) {
        winner = cells[coordinates.getRow()][coordinates.getColumn()].mark(playerId);
        return winner;
    }

    /**
     * ID of whoever player won.
     * @return
     */
    public Integer getWinner() {
        return winner;
    }

    /**
     * For testing purposes.
     * @param coordinates
     * @return
     */
    BoardCell getCell(BoardCellCoordinates coordinates) {
        return cells[coordinates.getRow()][coordinates.getColumn()];
    }

    /**
     * Game maybe over with ot without a winner.
     * @return
     */
    public boolean isGameOver() {
        return remainingCells == 0 || winner != null;
    }
}
