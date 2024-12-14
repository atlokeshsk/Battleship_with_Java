package battleship;

import java.util.*;

class BattleShip {
    String[][] field = new String[11][11];
    String[][] hiddenField = new String[11][11];
    Scanner scanner;
    int shipRemaining = 5;
    Map<String, List<int[]>> ships = new HashMap<>(); // Track ships and their cells

    BattleShip() {
        scanner = new Scanner(System.in);
    }

    void initializeGame() {
        for (int row = 0; row < field.length; row++) {
            for (int column = 0; column < field.length; column++) {
                if (row == 0 && column == 0) {
                    field[row][column] = " ";
                    hiddenField[row][column] = " ";
                    continue;
                }
                if (row == 0) {
                    field[0][column] = String.valueOf(column);
                    hiddenField[0][column] = String.valueOf(column);
                    continue;
                }
                if (column == 0) {
                    char ch = (char) (64 + row);
                    field[row][column] = String.valueOf(ch);
                    hiddenField[row][column] = String.valueOf(ch);
                    continue;
                }
                field[row][column] = "~";
                hiddenField[row][column] = "~";
            }
        }
    }

    void getCoordinates(int length, String shipName) {
        System.out.println("Enter the coordinates of the " + shipName + " (" + length + " cells):");
        System.out.println();
        boolean valid = false;
        while (!valid) {
            String firstCoordinate = scanner.next();
            String secondCoordinate = scanner.next();

            if (parseCoordinate(firstCoordinate) == null || parseCoordinate(secondCoordinate) == null) {
                System.out.println("Error! Invalid coordinates for the " + shipName + ". Try again.");
                continue;
            }

            valid = placeShip(firstCoordinate, secondCoordinate, length, shipName);

            if (!valid) {
                System.out.println();
            }
        }
    }

    boolean placeShip(String firstCoordinate, String secondCoordinate, int length, String shipName) {
        int[] first = parseCoordinate(firstCoordinate);
        int[] second = parseCoordinate(secondCoordinate);

        int firstRow = first[0];
        int firstCol = first[1];
        int secondRow = second[0];
        int secondCol = second[1];

        if (firstRow != secondRow && firstCol != secondCol) {
            System.out.println();
            System.out.println("Error! Wrong ship location! Try again:");
            return false;
        }

        if (Math.abs(firstRow - secondRow) + Math.abs(firstCol - secondCol) + 1 != length) {
            System.out.println();
            System.out.println("Error! Wrong length of the " + shipName + "! Try again.");
            return false;
        }

        if (!isValidPlacement(firstRow, firstCol, secondRow, secondCol)) {
            System.out.println();
            System.out.println("Error! You placed it too close to another one. Try again:");
            return false;
        }

        placeShipOnField(firstRow, firstCol, secondRow, secondCol, shipName);
        return true;
    }

    boolean isValidPlacement(int firstRow, int firstCol, int secondRow, int secondCol) {
        int rowStart = Math.min(firstRow, secondRow);
        int rowEnd = Math.max(firstRow, secondRow);
        int colStart = Math.min(firstCol, secondCol);
        int colEnd = Math.max(firstCol, secondCol);

        for (int row = rowStart - 1; row <= rowEnd + 1; row++) {
            for (int col = colStart - 1; col <= colEnd + 1; col++) {
                if (row >= 1 && row <= 10 && col >= 1 && col <= 10) {
                    if (field[row][col].equals("O")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    void placeShipOnField(int firstRow, int firstCol, int secondRow, int secondCol, String shipName) {
        List<int[]> shipCells = new ArrayList<>();
        if (firstRow == secondRow) {
            for (int i = Math.min(firstCol, secondCol); i <= Math.max(firstCol, secondCol); i++) {
                field[firstRow][i] = "O";
                shipCells.add(new int[]{firstRow, i});
            }
        } else {
            for (int i = Math.min(firstRow, secondRow); i <= Math.max(firstRow, secondRow); i++) {
                field[i][firstCol] = "O";
                shipCells.add(new int[]{i, firstCol});
            }
        }
        ships.put(shipName, shipCells);
        System.out.println();
        printField(field);
    }

    int[] parseCoordinate(String coordinate) {
        if (coordinate.length() < 2 || coordinate.length() > 3) {
            return null;
        }

        char rowChar = coordinate.charAt(0);
        int col = Integer.parseInt(coordinate.substring(1));

        if (rowChar < 'A' || rowChar > 'J' || col < 1 || col > 10) {
            return null;
        }

        int row = rowChar - 'A' + 1;
        return new int[]{row, col};
    }

    void printField(String[][] fieldToPrint) {
        for (int row = 0; row < fieldToPrint.length; row++) {
            for (int column = 0; column < fieldToPrint.length; column++) {
                System.out.printf("%s ", fieldToPrint[row][column]);
            }
            System.out.println();
        }
        System.out.println();
    }

    void takeShot() {
        while (true) {
            System.out.println();
            String shotCoordinate = scanner.next();
            int[] shot = parseCoordinate(shotCoordinate);

            if (shot == null) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                continue;
            }

            int row = shot[0];
            int col = shot[1];

            if (field[row][col].equals("O") || field[row][col].equals("X")) {
                field[row][col] = "X";
                hiddenField[row][col] = "X";
                if(checkIfShipSunk(row, col)){
                    if(ships.isEmpty()){
                        break;
                    }
                    System.out.println("You sank a ship!");
                } else {
                    System.out.println("You hit a ship!");
                }
            } else if (field[row][col].equals("~")) {
                field[row][col] = "M";
                hiddenField[row][col] = "M";
                System.out.println("You missed. Try again:");
            }
           break;

        }
    }

    boolean checkIfShipSunk(int row, int col) {
        for (Map.Entry<String, List<int[]>> entry : ships.entrySet()) {
            List<int[]> shipCells = entry.getValue();
            shipCells.removeIf(cell -> cell[0] == row && cell[1] == col);

            if (shipCells.isEmpty()) {
                ships.remove(entry.getKey());
                return true;
            }
        }
        return false;
    }
}

class GameControl{
    BattleShip player1;
    BattleShip player2;
    Scanner scanner;


    public GameControl(BattleShip player1, BattleShip player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.scanner = new Scanner(System.in);
    }

    public void initalizePlayer1(){
        initializePlayer(player1, 1);
    }

    public void initalizePlayer2(){
        initializePlayer(player2, 2);
    }

    void initializePlayer(BattleShip player, int name){
        System.out.printf("Player %d, place your ships on the game field\n", name);
        System.out.println();
        player.initializeGame();
        player.printField(player.hiddenField);
        player.getCoordinates(5, "Aircraft Carrier");
        player.getCoordinates(4, "Battleship");
        player.getCoordinates(3, "Submarine");
        player.getCoordinates(3, "Cruiser");
        player.getCoordinates(2, "Destroyer");
        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();
    }

    void startGame(){
        int currentPlayer = 1;
        while (true){
            switch (currentPlayer%2){
                case 1:
                    player2.printField(player2.hiddenField);
                    System.out.println("---------------------");
                    player1.printField(player1.field);
                    System.out.println("Player 1, it's your turn:");
                    player2.takeShot();
                    if(player2.ships.isEmpty()){
                        System.out.println("You sank the last ship. You won. Congratulations!");
                        return;
                    }
                    break;
                case 0:
                    player1.printField(player1.hiddenField);
                    System.out.println("---------------------");
                    player2.printField(player2.field);
                    System.out.println("Player 2, it's your turn:");
                    player1.takeShot();
                    if(player1.ships.isEmpty()){
                        System.out.println("You sank the last ship. You won. Congratulations!");
                        return;
                    }
                    break;
            }
            currentPlayer++;
            System.out.println("Press Enter and pass the move to another player");
            scanner.nextLine();
        }

    }
}

public class Main {
    public static void main(String[] args) {

        BattleShip player1 = new BattleShip();
        BattleShip player2 = new BattleShip();
        GameControl game = new GameControl(player1, player2);
        game.initalizePlayer1();
        game.initalizePlayer2();
        game.startGame();
    }
}
