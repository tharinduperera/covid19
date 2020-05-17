package lk.ijse.covid19.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;

public class MainController {
    public AnchorPane root;
    public Button btnglobal;
    public Button btnquarantine;
    public Button btnusers;
    public Button btnhospital;
    public Label lblTips;

    public void initialize() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000));
                fadeTransition.setNode(root);
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);
                fadeTransition.play();
            }
        });

        root.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>
                () {
            @Override
            public void handle(KeyEvent t) {
                if(t.getCode()==KeyCode.ESCAPE)
                {
                    Optional<ButtonType> alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to Exit!!", ButtonType.YES, ButtonType.NO).showAndWait();
                    if (alert.get().equals(ButtonType.YES)) {
                        Stage sb = (Stage)btnglobal.getScene().getWindow();//use any one object
                        sb.close();
                    }
                }
            }
        });
    }

    public void btnglobalOnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/covid19/view/global.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    public void btnquarantineOnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/covid19/view/quarantinecenter.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    public void btnusersOnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/covid19/view/user.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    public void btnhospitalOnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/covid19/view/hospital.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    public void btnglobalOnMouseEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
    }

    public void btnquarantineOnMouseEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
    }

    public void btnusersOnMouseEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
    }

    public void btnhospitalOnMouseEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
    }

    public void btnglobalOnMouseExited(MouseEvent mouseEvent) {
        exited(mouseEvent);
    }

    public void btnquarantineOnMouseExited(MouseEvent mouseEvent) {
        exited(mouseEvent);
    }

    public void btnusersOnMouseExited(MouseEvent mouseEvent) {
        exited(mouseEvent);
    }

    public void btnhospitalOnMouseEixted(MouseEvent mouseEvent) {
        exited(mouseEvent);
    }

    private void entered(MouseEvent mouseEvent){
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1.1);
        s.setFromY(1.1);
        s.setToX(1.1);
        s.setToY(1.1);
        s.play();

    }
    private void exited(MouseEvent mouseEvent){
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1);
        s.setFromY(1);
        s.setToX(1);
        s.setToY(1);
        s.play();

    }
}
