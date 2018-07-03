package t3.core.controller;

import t3.core.data.structures.BoardCellCoordinates;
import t3.core.engine.BasicT3Strategy;
import t3.core.engine.GenericTicTacToe;
import t3.core.engine.Strategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * Concrete controller of generalized tic-tac-toe.
 */
public class T3Controller {

    public static final String INCORRECT_SQUARE_SIZE = "Incorrect for property squareSize, a number between 4 and 8 is expected";
    private int size = 4;
    private GenericTicTacToe t3;
    private T3View view = new T3View();
    public static final int COMPUTER_ID = 3;
    private Strategy strategy;
    private Scanner in = new Scanner(System.in);

    public boolean isGameOver() {
        return t3.isGameOver();
    }

    /**
     * Basic view supporting fields up to 99x99.
     */
    class T3View {
        private Map<Integer, String> playerToIcons;

        public void printWhoMakesTheMove() {
            out.println("Player " + t3.getNextPlayer() + " (" + playerToIcons.get(t3.getNextPlayer()) + ") takes turn");
        }
        public void refreshScreen() {
            out.print(" ");
            for (int column = 0; column < size; column++) {
                out.print(" " + column);
            }
            out.println();

            for (int row = 0; row < size; row++) {
                if (row < 10)
                    out.print(row + " ");
                for (int column = 0; column < size; column++) {
                    out.print(playerToIcons.get(t3.getPlayerId(new BoardCellCoordinates(row, column))));
                    out.print(" ");
                }
                out.println();
            }
            if (t3.isGameOver())
                if (t3.getWinner() == null) {
                    out.println("It's a draw!");
                } else {
                    out.println("The winner is Player " + t3.getWinner());
                }
        }

        public void setPlayerToIcons(Map<Integer, String> playerToIcons) {
            this.playerToIcons = playerToIcons;
        }
    }

    public void init() {
        Map<Integer, String> playersSetup = readConfiguration();
        view.setPlayerToIcons(playersSetup);
        t3 = new GenericTicTacToe(playersSetup.keySet(), size, size, true);
        strategy = new BasicT3Strategy(COMPUTER_ID, t3);
        playersSetup.put(null, "_");
    }

    public boolean firstRenderingStep() {
        if (t3.getNextPlayer() == COMPUTER_ID) {
            view.printWhoMakesTheMove();
            t3.takeTurn(strategy.makeMove());
            return true;
        }
        return false;
    }

    public void secondRenderingStep() {
        view.refreshScreen();
        view.printWhoMakesTheMove();
        BoardCellCoordinates coordinates = readUserInput();
        while (true) {
            if (t3.isValidMove(coordinates)) {
                break;
            }
            err.println("Cell has been taken or out of range.");
            coordinates = readUserInput();
        }
        t3.takeTurn(coordinates);
    }

    private BoardCellCoordinates readUserInput() {
        while (true) {
            try {
                in.reset();
                out.println("Please enter coordinates one by one, e.g.\n2 [ENTER] \n0 [ENTER]");
                in.hasNext();
                int row = in.nextInt();
                in.hasNext();
                int col = in.nextInt();
                return new BoardCellCoordinates(row, col);
            } catch (Exception e) {
                err.println("Invalid numeric input, please try again.");
            }
        }
    }

    private Map<Integer, String> readConfiguration() {
        String filePath = "t3.properties";
        Properties prop = new Properties();
        Map<Integer, String> playerToIcons = new HashMap<>();
        String player1 = "X", player2 = "O", computer = "#";

        try (InputStream is = new FileInputStream(new File(System.getProperty("configFolder", null), filePath))) {
            // Loading the properties.
            prop.load(is);
            // Getting properties
            try {
                size = Integer.valueOf(prop.getProperty("squareSize", "4"));
                if (size < 4 && size > 8) {
                    System.err.println(INCORRECT_SQUARE_SIZE);
                    System.exit(1);
                }
            } catch (NumberFormatException b) {
                System.err.println(INCORRECT_SQUARE_SIZE);
                System.exit(1);
            }
            player1 = prop.getProperty("player1", "X");
            player2 = prop.getProperty("player2", "O");
            computer = prop.getProperty("computer", "#");
            if (player1.length() != 1) {
                System.err.println("Warning: 'player1' parameter accepts exactly one character");
            }
            if (player2.length() != 1) {
                System.err.println("Warning: 'player2' parameter accepts exactly one character");
            }
            if (computer.length() != 1) {
                System.err.println("Warning: 'computer' parameter accepts exactly one character");
            }
        } catch (Exception ex) {
            out.println("Warning: Using defaults, failed ro read configuration file: " + ex.getLocalizedMessage() + "" +
                    "\nConsider placing configuration in the current directory or set path as follows: -DconfigFolder=/tmp");
        }
        playerToIcons.put(1, player1);
        playerToIcons.put(2, player2);
        playerToIcons.put(COMPUTER_ID, computer);
        return playerToIcons;
    }

    public static void main(String[] argv) {
        T3Controller controller = new T3Controller();
        controller.init();
        do {
            if (controller.firstRenderingStep()) {
                if (controller.isGameOver()) {
                    break;
                }
                continue;
            }
            controller.secondRenderingStep();
        } while (!controller.isGameOver());
        controller.view.refreshScreen();
        out.println("See you later, alligator!");
    }

}
