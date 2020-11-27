package com.callumbirks.gui;

import com.callumbirks.game.ChessPiece;
import com.callumbirks.game.Colour;
import com.callumbirks.game.Game;
import com.callumbirks.game.PieceType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button timerButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private static int WIDTH;
    private static int HEIGHT;
    private double xOffset;
    private double yOffset;

    private static final int PIXEL_SIZE = 100;

    private Game game;
    private Timeline timer;
    private int remainingSeconds;
    private boolean holdingPiece = false;
    private ChessPiece currentPiece;
    private List<ChessPiece> availableSquares;
    private Colour currentTurn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        WIDTH = (int) canvas.getWidth();
        HEIGHT = (int) canvas.getHeight();
        game = new Game();
        availableSquares = new ArrayList<>();
        currentTurn = Colour.WHITE;
        timer = new Timeline();
        timer.setCycleCount(60);
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            remainingSeconds -= 1;
            updateTimer();
        }));
        timer.setOnFinished(e -> {
            currentTurn = (currentTurn == Colour.WHITE ? Colour.BLACK : Colour.WHITE);
            availableSquares.clear();
            render();
        });
        render();
    }

    public void startTimer() {
        remainingSeconds = 60;
        timer.play();
    }

    private void render() {
        gc.clearRect(0,0,WIDTH, HEIGHT);
        drawBoard();
        drawPieces();
        drawAvailableSquares();
    }

    private void updateTimer() {
        if(timer.getStatus() == Animation.Status.RUNNING)
            progressBar.setProgress(((float) remainingSeconds / 60));
        else
            progressBar.setProgress(0);
    }

    private void drawAvailableSquares() {
        if(availableSquares.isEmpty())
            return;
        gc.setFill(Color.web("#83A3AA",0.9));
        for(ChessPiece piece : availableSquares) {
            gc.fillOval(piece.getX()*PIXEL_SIZE + 40, piece.getY()*PIXEL_SIZE + 40, 20, 20);
        }
    }

    private void drawPieces() {
        ChessPiece[][] pieces = game.getPieces();
        for(ChessPiece[] row : pieces) {
            for(ChessPiece piece : row) {
                if(piece.getPieceType() != PieceType.EMPTY) {
                    Image pieceImage = (SVGLoader.loadSVG(String.format("/svg/pieces/%s-%s.svg", piece.getColour(), piece.getPieceType().toString())));
                    gc.drawImage(pieceImage, piece.getX()*PIXEL_SIZE, piece.getY()*PIXEL_SIZE);
                }
            }
        }
    }

    private void drawBoard() {
        gc.setFill(Color.web("#488B99"));
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if((i % 2 == 0 && j % 2 == 1) || i % 2 == 1 && j % 2 == 0)
                    gc.fillRect(i*PIXEL_SIZE,j*PIXEL_SIZE,PIXEL_SIZE,PIXEL_SIZE);

            }
        }
    }

    public void closeWindow() {
        System.exit(0);
    }

    public void maxWindow(ActionEvent actionEvent) {
        Window stage = getStage(actionEvent);
        stage.centerOnScreen();
    }

    public void openSettings() {
    }

    public void clickTimer() {
        game.setTimerEnabled(!game.isTimerEnabled());
        if(game.isTimerEnabled()) {
            timerButton.getStyleClass().add("timer-on");
        }
        else {
            timerButton.getStyleClass().remove("timer-on");
        }
    }

    public void startGame() {
        if(game.isTimerEnabled())
            startTimer();
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
            if(piece.getPieceType() == PieceType.EMPTY) {
                currentPiece = null;
                availableSquares.clear();
                return;
            }
            else {
                if(currentTurn == piece.getColour()) {
                    holdingPiece = true;
                    currentPiece = piece;
                    availableSquares = game.getMoves(piece);
                    if (availableSquares.isEmpty())
                        holdingPiece = false;
                }
            }
        }
        else {
            if(piece.getColour() == currentTurn) {
                holdingPiece = false;
                gameMouseClick(mouseEvent);
            }
            if(availableSquares.contains(piece)) {
                game.movePiece(currentPiece, new int[]{piece.getX(), piece.getY()});
                availableSquares.clear();
                holdingPiece = false;
                currentTurn = (currentTurn == Colour.WHITE ? Colour.BLACK : Colour.WHITE);
                if (timer.getStatus() == Animation.Status.RUNNING)
                    timer.stop();
                if (game.isTimerEnabled())
                    startTimer();
            }
            else if(piece.getPieceType() == PieceType.EMPTY) {
                holdingPiece = false;
                gameMouseClick(mouseEvent);
            }
        }
        render();
    }
}
