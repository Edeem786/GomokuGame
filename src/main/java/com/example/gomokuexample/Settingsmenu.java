package com.example.gomokuexample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Settingsmenu extends Application {
    private static int selectboard = 20; // board size
    private static int selectime = 30; // time limit for each turn
    private static int selectwinlen = 5;  // length of stones in a row required to win
    private static boolean aimode = false;
    private Label curboardsize;
    private Label curtimelim;
    private Label curwinlen;
    private final int MAXWIDTH = 150;
    public void start(Stage primaryStage) {
        Button menubutton = new Button("Back to Main Menu");
        menubutton.setTranslateY(80);

        Slider boardsize = new Slider(5, 20, selectboard);
        Slider timelimit = new Slider(5, 60, selectime);
        Slider winlen = new Slider(3, 5, selectwinlen);

        boardsize.setTranslateY(-40);
        boardsize.setMaxWidth(MAXWIDTH);
        curboardsize = new Label("Board Size: " + selectboard);
        curboardsize.setTranslateY(-60);

        timelimit.setTranslateY(0);
        timelimit.setMaxWidth(MAXWIDTH);
        curtimelim = new Label("Time limit: " + selectime);
        curtimelim.setTranslateY(-20);

        winlen.setTranslateY(40);
        winlen.setMaxWidth(MAXWIDTH / 2);
        curwinlen = new Label("Length needed to win : " + selectwinlen);
        curwinlen.setTranslateY(20);

        CheckBox aipick = new CheckBox("Fight AI?");
        aipick.setTranslateY(-90);
        aipick.setSelected(aimode);
        aipick.selectedProperty().addListener((observable, oldValue, newValue) -> {
            aimode = newValue;
        });

        menubutton.setOnAction(e -> {
            Mainmenu menu = new Mainmenu();
            try {
                menu.start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        boardsize.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectboard = newVal.intValue();
            curboardsize.setText("Board Size: " + selectboard);
        });

        timelimit.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectime = newVal.intValue();
            curtimelim.setText("Time limit: " + selectime);
        });

        winlen.valueProperty().addListener((obs, oldVal, newVal) -> {
            selectwinlen = newVal.intValue();
            curwinlen.setText("Length needed to win : " + selectwinlen);
        });

        StackPane root = new StackPane();
        root.getChildren().addAll(menubutton, boardsize, timelimit, curboardsize, curtimelim, curwinlen, winlen, aipick);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Gomoku Game - Settings");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static int getboardsize() {return selectboard;}
    public static int gettimelimit() {return selectime;}
    public static int getwinlen() {return selectwinlen;}
    public static boolean getaipick() {return aimode;}

}
