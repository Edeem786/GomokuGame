package com.example.gomokuexample;

import com.sun.tools.javac.Main;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class GomokuGameFX extends Application {
    private static final int CELL_SIZE = 40;
    private int BOARD_SIZE = 10;
    private static final int TOP_ROW = 80; // vertical size of space above board
    private int TURNTIMER = 30; // time limit for each player
    private boolean FIGHTINGAI = false;
    private int WINLEN = 5; // length of stones in a row needed to win
    private static int BOARD_WIDTH;
    private static int BOARD_HEIGHT;
    private static final int PADDING = 20; // for labels
    private int FONTSIZE = BOARD_SIZE * 2; // font size scales with board size
    private GomokuGame game;
    private Label timerlabel; // showing how much time left
    private Label turnlabel; // showing whose turn is it
    private Label alertlabel; // displaying messages (such as invalid move)
    private Label player1stats;
    private Label player2stats;
    private Button menubutton; // button to send player back to main menu
    private Button restartbutton; // button for restarting a game
    private Timeline timer;
    private int timeleft = TURNTIMER; // current time left
    private Canvas canvas;
    private int lastx = 0;
    private int lasty = 0;

    public GomokuGameFX(int sizesetting, int timesetting, int winlen, boolean fightingai) {
        // initialize all variables according to settings
        this.BOARD_SIZE = sizesetting;
        this.FONTSIZE = BOARD_SIZE * 2;
        this.TURNTIMER = timesetting;
        this.WINLEN = winlen;
        this.FIGHTINGAI = fightingai;
        this.BOARD_WIDTH = BOARD_SIZE * CELL_SIZE;
        this.BOARD_HEIGHT = BOARD_SIZE * CELL_SIZE + TOP_ROW;
        this.lastx = 0;
        this.lasty = 0;
    }

    @Override
    public void start(Stage primaryStage) {
        game = new GomokuGame(BOARD_SIZE, WINLEN);
        StackPane root = new StackPane();
        canvas = new Canvas(CELL_SIZE * BOARD_SIZE, CELL_SIZE * BOARD_SIZE + TOP_ROW);
        root.getChildren().add(canvas);

        // initialize all labels
        timerlabel = new Label();
        timerlabel.setFont(new Font(FONTSIZE));
        timerlabel.setTextFill(Color.BLACK);
        root.getChildren().add(timerlabel);
        resetTimer();

        turnlabel = new Label();
        turnlabel.setFont(new Font(FONTSIZE));
        turnlabel.setTextFill(Color.BLACK);
        root.getChildren().add(turnlabel);

        alertlabel = new Label();
        alertlabel.setFont(new Font(FONTSIZE));
        alertlabel.setTextFill(Color.RED);
        root.getChildren().add(alertlabel);

        player1stats = new Label();
        player1stats.setFont(new Font(FONTSIZE / 4));
        player1stats.setTextFill(Color.BLACK);
        root.getChildren().add(player1stats);
        player1stats.setTranslateX(-(BOARD_WIDTH / 3) - PADDING);
        player1stats.setTranslateY( -(BOARD_HEIGHT / 2) + PADDING);

        player2stats = new Label();
        player2stats.setFont(new Font(FONTSIZE/ 4));
        player2stats.setTextFill(Color.BLACK);
        root.getChildren().add(player2stats);
        player2stats.setTranslateX((BOARD_WIDTH / 3) + PADDING);
        player2stats.setTranslateY( -(BOARD_HEIGHT / 2) + PADDING);

        // initialize restart and main menu button
        restartbutton =  new Button("RESTART");
        menubutton = new Button("MENU");

        // initialize positions
        menubutton.setTranslateY(-(BOARD_HEIGHT / 2) + menubutton.getHeight() + PADDING);
        menubutton.setTranslateX(-BOARD_WIDTH / 8);
        restartbutton.setTranslateY(-(BOARD_HEIGHT / 2) + restartbutton.getHeight() + PADDING);
        restartbutton.setTranslateX(BOARD_WIDTH / 8);
        root.getChildren().add(menubutton);
        root.getChildren().add(restartbutton);

        // width scales with board size
        menubutton.setMaxWidth(BOARD_SIZE * 8);
        restartbutton.setMaxWidth(BOARD_SIZE * 8);

        // draw initial board
        drawBoard(canvas.getGraphicsContext2D());

        canvas.setOnMouseClicked(e -> {
            if (FIGHTINGAI && game.getCurrentPlayer() != 1) return; // stop player from moving if AI is computing

            int x = (int) (e.getX() / CELL_SIZE);
            int y = (int) ((e.getY() - TOP_ROW )/ CELL_SIZE);

            if (game.move(x, y)) {
                // move is succesful (not invalid)
                lastx = x;
                lasty = y;
                drawBoard(canvas.getGraphicsContext2D());
                resetTimer(); // timer back to start
                if (game.isGameOver()) {
                    endgame();
                } else if (FIGHTINGAI) {
                    int[] pickmove = game.findmove(lastx, lasty);
                    int aix = pickmove[0];
                    int aiy = pickmove[1];
                    displayMsg("AI makes a move!", 1);
                    game.move(aix, aiy);
                    System.out.println("AI MOVE : " + aix + " " + aiy);
                    drawBoard(canvas.getGraphicsContext2D());
                    resetTimer();
                    if (game.isGameOver()) {
                        endgame();
                    }
                }
            }
            else if (!game.isGameOver()) {
                displayMsg("Invalid move!", 1);
                System.out.println("Invalid move!");
            }
        });


        // event listener for pressing menu button
        menubutton.setOnAction(e -> {
            Mainmenu menu = new Mainmenu();
            try {
                menu.start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // event listener for pressing restart button
        restartbutton.setOnAction(e -> {
            GomokuGameFX newgame = new GomokuGameFX(BOARD_SIZE, TURNTIMER, WINLEN, FIGHTINGAI);
            try {
                newgame.start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        Scene scene = new Scene(root, CELL_SIZE * BOARD_SIZE, CELL_SIZE * BOARD_SIZE + TOP_ROW);
        primaryStage.setTitle("Gomoku Game");
        primaryStage.setScene(scene);
        primaryStage.show();
        resetTimer();
        drawBoard(canvas.getGraphicsContext2D());
    }

    // draw current state of board
    private void drawBoard(GraphicsContext gc) {
        gc.clearRect(0, 0, CELL_SIZE * BOARD_SIZE, CELL_SIZE * BOARD_SIZE + TOP_ROW);
        gc.setStroke(Color.BLACK);
        int halfcell = CELL_SIZE / 2;
        for (int i = 0; i < BOARD_SIZE; i++) {
            // add vertical lines
            gc.strokeLine(i * CELL_SIZE + halfcell, TOP_ROW, i * CELL_SIZE + halfcell, CELL_SIZE * BOARD_SIZE + TOP_ROW);
            // add horizontal lines
            gc.strokeLine(0, i * CELL_SIZE + TOP_ROW + halfcell, CELL_SIZE * BOARD_SIZE, i * CELL_SIZE + TOP_ROW + halfcell);
        }
        int[][] board = game.getBoard();

        // add stones (black and white)
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                double x = (i + 0.1) * CELL_SIZE;
                double y = (j + 0.1) * CELL_SIZE;
                double w = CELL_SIZE * 0.8;
                double h = CELL_SIZE * 0.8;
                if (board[i][j] == 1) {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(x, y + TOP_ROW, w, h);
                    gc.strokeOval(x, y + TOP_ROW, w, h);
                } else if (board[i][j] == 2) {
                    gc.setFill(Color.WHITE);
                    gc.fillOval(x, y + TOP_ROW, w, h);
                    gc.strokeOval(x, y + TOP_ROW, w, h);
                }
            }
        }


        // show whose turn is it
        if (!game.isGameOver()) {
            int curplayer = game.getCurrentPlayer();
            turnlabel.setTextFill(Color.BLACK);
            turnlabel.setFont(new Font(FONTSIZE));
            if (curplayer == 1) turnlabel.setText("Black's Turn");
            else turnlabel.setText("White's Turn");
            turnlabel.setTranslateY(-(BOARD_HEIGHT / 2) + TOP_ROW - PADDING);
            turnlabel.setTranslateX(-(BOARD_WIDTH / 2) + turnlabel.getWidth() / 2 + PADDING);
        }

        updatestats();
    }

    private void updatestats(){
        int[] stats = game.getstats();
        player1stats.setText("Player 1 moves : " + stats[0] + ", maximum length: " + stats[2]);
        player2stats.setText("Player 2 moves : " + stats[1] + ", maximum length: " + stats[3]);
    }

    // change time left back to time limit
    private void resetTimer() {
        if (timer != null) timer.stop();
        timeleft = TURNTIMER;
        timerlabel.setTextFill(Color.BLACK);
        timerlabel.setText("Time: " + TURNTIMER);
        timerlabel.setTranslateX((BOARD_WIDTH / 2) - timerlabel.getBoundsInLocal().getWidth() / 2 - PADDING);
        timerlabel.setTranslateY( -(BOARD_HEIGHT / 2) + TOP_ROW - PADDING);
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    // update every second
    private void updateTimer() {
        if (timeleft > 0) {
            timeleft -= 1;
            timerlabel.setText("Time: " + timeleft);
            if (timeleft <= 5) timerlabel.setTextFill(Color.RED);
        }
        else
        {
            // swap players if time runs out
            displayMsg("Time's up", 2);
            game.switchPlayers();
            resetTimer();
            drawBoard(canvas.getGraphicsContext2D());

            // let AI move if human runs out of time
            if (FIGHTINGAI) {
                int[] pickmove = game.findmove(lastx, lasty);
                int aix = pickmove[0];
                int aiy = pickmove[1];
                displayMsg("AI makes a move!", 1);
                game.move(aix, aiy);
                System.out.println("AI : " + aix + " " + aiy);
                drawBoard(canvas.getGraphicsContext2D());
                resetTimer();
                if (game.isGameOver()) endgame();
            }

        }
    }

    private void endgame() {
        // display winner
        updatestats();
        stopTimer();
        turnlabel.setTextFill(Color.DARKGREEN);
        turnlabel.setTranslateX(0);
        if (game.getWinner() == 0) turnlabel.setText("Draw!");
        else if (game.getWinner() == 2) turnlabel.setText("Game over, White wins!");
        else turnlabel.setText("Game over, Black wins!");
    }

    private void stopTimer() {
        timerlabel.setVisible(false);
        timer.stop();
    }

    // show a brief message (for example, warning if a player makes invalid move)
    private void displayMsg(String msg, int duration) {
        alertlabel.setText(msg);
        alertlabel.setVisible(true);
        alertlabel.setTranslateY( -(BOARD_HEIGHT / 2) + TOP_ROW - PADDING);
        Timeline onesec = new Timeline(new KeyFrame(Duration.seconds(duration), e -> {
            alertlabel.setVisible(false);
        }));
        onesec.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}