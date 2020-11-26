package com.callumbirks.gui;

import com.callumbirks.game.ChessPiece;
import com.callumbirks.game.Game;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.stage.Window;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private static int WIDTH;
    private static int HEIGHT;
    private double xOffset;
    private double yOffset;

    private static final int PIXEL_SIZE = 100;

    private Game game;
    private boolean holdingPiece = false;
    private List<int[]> availableSquares;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        WIDTH = (int) canvas.getWidth();
        HEIGHT = (int) canvas.getHeight();
        game = new Game();
        availableSquares = new ArrayList<>();
        render();
    }

    private void render() {
        gc.clearRect(0,0,WIDTH, HEIGHT);
        drawBoard();
        drawPieces();
    }

    private void drawPieces() {
        ChessPiece[][] pieces = game.getPieces();
        for(ChessPiece[] row : pieces) {
            for(ChessPiece piece : row) {
                if(piece != null) {
                    Image pieceImage = (SVGLoader.loadSVG(String.format("/svg/pieces/%s-%s.svg", piece.getColour(), piece.getPieceType().toString())));
                    gc.drawImage(pieceImage, piece.getX()*PIXEL_SIZE, piece.getY()*PIXEL_SIZE);
                }
            }
        }
    }

    private void drawBoard() {
        gc.setFill(Paint.valueOf("#488B99"));
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if((i % 2 == 0 && j % 2 == 1) || i % 2 == 1 && j % 2 == 0)
                    gc.fillRect(i*PIXEL_SIZE,j*PIXEL_SIZE,PIXEL_SIZE,PIXEL_SIZE);

            }
        }
    }

    public void closeWindow(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void maxWindow(ActionEvent actionEvent) {
        Window stage = getStage(actionEvent);
        stage.centerOnScreen();
    }

    public void openSettings(ActionEvent actionEvent) {
    }

    public void clickTimer(ActionEvent actionEvent) {
        game.setTimerEnabled(!game.isTimerEnabled());
        System.out.println(game.isTimerEnabled());
    }

    public void startGame(ActionEvent actionEvent) {
    }

    public void clickWindow(MouseEvent mouseEvent) {
        Window stage = getStage(mouseEvent);
        xOffset = stage.getX() - mouseEvent.getScreenX();
        yOffset = stage.getY() - mouseEvent.getScreenY();
    }

    public void dragWindow(MouseEvent mouseEvent) {
        Window stage = getStage(mouseEvent);
        stage.setX(mouseEvent.getScreenX() + xOffset);
        stage.setY(mouseEvent.getScreenY() + yOffset);
    }

    private Window getStage(Event event) {
        return ((Node) event.getSource()).getScene().getWindow();
    }

    public void gameMouseClick(MouseEvent mouseEvent) {
        ChessPiece piece = game.getPiece((int) mouseEvent.getX() / PIXEL_SIZE, (int) mouseEvent.getY() / PIXEL_SIZE);
        if(!holdingPiece) {
            if(piece == null)
                return;
            else {
                holdingPiece = true;
                availableSquares = game.getMoves(piece);
            }
        }
    }
}
