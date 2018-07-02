package t3.core.engine;

import t3.core.InvalidMoveException;
import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;
import t3.core.data.structures.RoundRobin;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Represents board game and does answer some useful questions. It ensures games goes on according to the rules.
 * <br><br>
 * Terminology wise, every board has cells where players put marks in them. Players want to
 * put marks in winning shapes like 4 in line on 4x4 board.
 */
public abstract class BoardGame {
    protected RoundRobin turnsSchedule;
    protected BoardCell[][] cells;
    protected int remainingCells = 0;
    protected Integer winner;

    public BoardGame(Set<Integer> players, boolean randomTurnOrder) {
        turnsSchedule = new RoundRobin(players);
        if (randomTurnOrder) {
            Random r = new Random();
            for (int i = 0; i < 3; i++) {
                if (r.nextBoolean()) {
                    turnsSchedule.passTurn();
                }
            }
        }
    }

    /**
     * Given player makes a mark in a cell.
     * @param coordinates
     * @return
     */
    public Integer takeTurn(BoardCellCoordinates coordinates) {
        if (!isValidMove(coordinates)) {
            throw new InvalidMoveException("Coordinates out of range or cell is taken: " + coordinates);
        }
        int playerId = turnsSchedule.passTurn();
        winner = cells[coordinates.getRow()][coordinates.getColumn()].mark(playerId);
        remainingCells--;
        return winner;
    }

    public boolean isValidMove(BoardCellCoordinates coordinates) {
        if (coordinates.getRow() < 0 || coordinates.getColumn() < 0) {
            return false;
        }
        if (coordinates.getRow() > cells.length - 1 || coordinates.getColumn() > cells[coordinates.getRow()].length - 1) {
            return false;
        }
        return !cells[coordinates.getRow()][coordinates.getColumn()].isMarked();
    }

    /**
     *
     * @return id of the player who plays next.
     */
    public int getNextPlayer() {
        return turnsSchedule.getNextPlayer();
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
     * For viewing purposes
     * @param coordinates
     * @return
     */
    public Integer getPlayerId(BoardCellCoordinates coordinates) {
        return cells[coordinates.getRow()][coordinates.getColumn()].getPlayerId();
    }

    public abstract List<BoardCellCoordinates> getCorners();

    /**
     * Game maybe over with ot without a winner.
     * @return
     */
    public boolean isGameOver() {
        return remainingCells == 0 || winner != null;
    }

    public abstract Map<BoardCellCoordinates, Set<Integer>> getWinningCoordinates();

}
