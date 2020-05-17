package lk.ijse.covid19.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.covid19.db.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {
    public TextField txtusername;
    public PasswordField txtpassword;
    public Button btnlogin;
    public static String userid;
    public AnchorPane root;

    public void initialize(){

        txtusername.requestFocus();

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
                if(t.getCode()== KeyCode.ESCAPE)
                {
                    Optional<ButtonType> alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to Exit!!", ButtonType.YES, ButtonType.NO).showAndWait();
                    if (alert.get().equals(ButtonType.YES)) {
                        Stage sb = (Stage)txtusername.getScene().getWindow();//use any one object
                        sb.close();
                    }
                }
            }
        });
    }

    public void txtusernameOnAction(ActionEvent actionEvent) {
        txtpassword.requestFocus();
    }

    public void btnloginOnAction(ActionEvent actionEvent) {
        login();
    }

    public void txtpasswordOnAction(ActionEvent actionEvent) {
        login();
    }

    public void login(){
        String username = txtusername.getText();
        String password = txtpassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select id , role from users where username = ? and password = ? ");
            preparedStatement.setObject(1,username);
            preparedStatement.setObject(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                userid = resultSet.getString(1);
                String role = resultSet.getString(2);
                if(role.equals("Admin")){
                    loadpage();
                }
            }else{
                new Alert(Alert.AlertType.ERROR,"Invalid Username or Password!!!").showAndWait();
                txtusername.clear();
                txtpassword.clear();
                txtusername.requestFocus();
            }

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

    }

    private void loadpage() throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/covid19/view/adminmain.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    public static void setUserID(String id){
        userid = id;
    }

    public void btnloginOnMouseEntered(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1.03);
        s.setFromY(1.03);
        s.setToX(1.03);
        s.setToY(1.03);
        s.play();
    }

    public void btnloginOnMouseExited(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1);
        s.setFromY(1);
        s.setToX(1);
        s.setToY(1);
        s.play();
    }

}
