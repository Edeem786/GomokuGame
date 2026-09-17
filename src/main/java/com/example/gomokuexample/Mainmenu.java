package com.example.gomokuexample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Mainmenu extends Application {
    private static int TEXTSIZE = 30;

    public void start(Stage primaryStage) {
        Button startbutton = new Button("Start");
        Button settingbutton = new Button("Settings");
        startbutton.setFont(new Font(TEXTSIZE));
        settingbutton.setFont(new Font(TEXTSIZE));

        startbutton.setTranslateY(-50);
        settingbutton.setTranslateY(50);

        // event listener for pressing start button
        startbutton.setOnAction(e -> {
            int boardsize = Settingsmenu.getboardsize();
            int timelimit = Settingsmenu.gettimelimit();
            int winlen = Settingsmenu.getwinlen();
            boolean willfightai = Settingsmenu.getaipick();
            GomokuGameFX game = new GomokuGameFX(boardsize, timelimit, winlen, willfightai);
            try {
                // send player to the game
                game.start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // event listener for pressing settings button
        settingbutton.setOnAction(e -> {
            Settingsmenu settingmenu = new Settingsmenu();
            try {
                settingmenu.start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(startbutton);
        root.getChildren().add(settingbutton);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Gomoku Game - Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
