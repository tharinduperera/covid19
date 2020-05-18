package lk.ijse.covid19.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.covid19.db.DBConnection;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class GlobalController {
    public AnchorPane root;
    public Button btnhome;
    public Label lbllastUpdated;
    public Label lblconfirmedCases;
    public Label lblrecovered;
    public Label lbldeaths;
    public DatePicker dtdate;
    public TextField txtconfirmCases;
    public TextField txtrecovered;
    public TextField txtdeaths;
    public Button btnupdate;
    public CheckBox chkupdate;
    public ImageView imgHome;
    public Label lblupdatedid;
    int total_confirmed;
    int total_recovered;
    int total_deaths;

    int confirm;
    int recoverd;
    int deaths;

    public void initialize() {
        loaddetail();
        disableAll();
        dtdate.requestFocus();
        chkupdate.setVisible(false);
        btnupdate.setDisable(true);
        dtdate.requestFocus();


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
                        Stage sb = (Stage)lblconfirmedCases.getScene().getWindow();//use any one object
                        sb.close();
                    }
                }
            }
        });

        dtdate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                Calendar c = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                c.set(Calendar.MONTH, 10);
                c.set(Calendar.DATE, 01);
                c.set(Calendar.YEAR, 2019);
                Date d = c.getTime();
                String d2 = sdf.format(d);
                LocalDate parse = LocalDate.parse(d2);
                LocalDate today = LocalDate.now();
                if (date.isBefore(parse)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }else if (date.isAfter(today)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });


    }




    public void btnhomeOnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/covid19/view/adminmain.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    public void btnupdateOnAction(ActionEvent actionEvent) {
        if(txtconfirmCases.getText().trim().isEmpty() | !txtconfirmCases.getText().matches("\\d+")){
            txtconfirmCases.requestFocus();
        }else if(txtrecovered.getText().trim().isEmpty() | !txtrecovered.getText().matches("\\d+")){
            txtrecovered.requestFocus();
        }else if(txtdeaths.getText().trim().isEmpty() | !txtdeaths.getText().matches("\\d+")){
            txtdeaths.requestFocus();
        }else {
            getTotal();
            generateID();
            int new_confirmed = Integer.parseInt(txtconfirmCases.getText());
            int new_recovered = Integer.parseInt(txtrecovered.getText());
            int new_deaths = Integer.parseInt(txtdeaths.getText());

            Connection connection = DBConnection.getInstance().getConnection();
            String date = dtdate.getValue().toString();
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from global where lastupdated = '" + date + "'");
                if (!resultSet.next()) {
                    PreparedStatement preparedStatement = connection.prepareStatement("Insert into global values(?,?,?,?,?,?,?,?,?)");
                    preparedStatement.setObject(1, date);
                    preparedStatement.setObject(2, new_confirmed);
                    preparedStatement.setObject(3, new_recovered);
                    preparedStatement.setObject(4, new_deaths);
                    preparedStatement.setObject(5, total_confirmed + new_confirmed);
                    preparedStatement.setObject(6, total_recovered + new_recovered);
                    preparedStatement.setObject(7, total_deaths + new_deaths);
                    preparedStatement.setObject(8, "Not Updated");
                    preparedStatement.setObject(9,Integer.parseInt(lbllastUpdated.getText()));
                    int affectedrow = preparedStatement.executeUpdate();

                    if (affectedrow > 0) {
                        new Alert(Alert.AlertType.INFORMATION, "Successfully Added!!").show();
                        clearAll();
                        disableAll();
                        loaddetail();
                        btnupdate.setDisable(true);
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "Something went wrong!!").show();
                    }

                }else {

                    confirm = Integer.parseInt(resultSet.getString(2));
                    recoverd = Integer.parseInt(resultSet.getString(3));
                    deaths = Integer.parseInt(resultSet.getString(4));

                    PreparedStatement preparedStatement1 = connection.prepareStatement("Update global set confirmcases = ? , recovered = ? , deathes = ? , total_confirmcases = ? , total_recovered = ? , total_deathes = ? , status = ? , updatedid =? where lastupdated = ?");
                    preparedStatement1.setObject(1, txtconfirmCases.getText());
                    preparedStatement1.setObject(2, txtrecovered.getText());
                    preparedStatement1.setObject(3, txtdeaths.getText());
                    preparedStatement1.setObject(4, (total_confirmed - confirm) + new_confirmed);
                    preparedStatement1.setObject(5, (total_recovered - recoverd) + new_recovered);
                    preparedStatement1.setObject(6, (total_deaths - deaths) + new_deaths);
                    preparedStatement1.setObject(7, "Updated");
                    preparedStatement1.setObject(8, Integer.parseInt(lbllastUpdated.getText()));
                    preparedStatement1.setObject(9,date);
                    int affectedrow1 = preparedStatement1.executeUpdate();

                    if (affectedrow1 > 0) {
                        new Alert(Alert.AlertType.INFORMATION, "Successfully Updated!!").show();
                        clearAll();
                        disableAll();
                        loaddetail();
                        btnupdate.setDisable(true);
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "Something went wrong!!").show();
                    }
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }

    }



    public void getTotal() {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from global order by updatedid desc limit 1");
            if (resultSet.next()) {
                total_confirmed = Integer.parseInt(resultSet.getString(5));
                total_recovered = Integer.parseInt(resultSet.getString(6));
                total_deaths = Integer.parseInt(resultSet.getString(7));

            }else {
                return;
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void generateID(){
        Connection connection = DBConnection.getInstance().getConnection();
        int id;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select updatedid from global order by updatedid desc limit 1");
            if(resultSet.next()) {
                int old = Integer.parseInt(resultSet.getString(1));
                id = old+1;
                lbllastUpdated.setText(id+"");
            }else {
                lbllastUpdated.setText("1");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    private void loaddetail(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            Statement statement1 = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select total_confirmcases,total_recovered,total_deathes from global order by updatedid desc limit 1");
            ResultSet resultSet1 = statement1.executeQuery("select lastupdated from global order by lastupdated desc limit 1");
            resultSet1.next();
            while (resultSet.next()) {
                lbllastUpdated.setText(resultSet1.getString(1));
                lblconfirmedCases.setText(resultSet.getString(1));
                lblrecovered.setText(resultSet.getString(2));
                lbldeaths.setText(resultSet.getString(3));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void disableAll(){
        txtconfirmCases.setDisable(true);
        txtrecovered.setDisable(true);
        txtdeaths.setDisable(true);
    }

    private void enableAll(){
        txtconfirmCases.setDisable(false);
        txtrecovered.setDisable(false);
        txtdeaths.setDisable(false);
    }

    private void clearAll() {
        txtconfirmCases.clear();
        txtrecovered.clear();
        txtdeaths.clear();
        dtdate.getEditor().clear();
        dtdate.setValue(null);
        chkupdate.setSelected(false);
        chkupdate.setVisible(false);

    }

    public void dtdateOnAction(ActionEvent actionEvent) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        if(dtdate.getValue()==null){
            return;
        }

        try {
            String date = dtdate.getValue().toString();

            System.out.println(date);
            PreparedStatement preparedStatement = connection.prepareStatement("select * from global where lastupdated = ?");
            preparedStatement.setObject(1,date);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                disableAll();
                txtconfirmCases.setText(resultSet.getString(2));
                txtrecovered.setText(resultSet.getString(3));
                txtdeaths.setText(resultSet.getString(4));
                chkupdate.setVisible(true);
                chkupdate.setSelected(false);
            }else{
                chkupdate.setVisible(false);
                chkupdate.setSelected(false);
                enableAll();
                txtconfirmCases.clear();
                txtrecovered.clear();
                txtdeaths.clear();
                btnupdate.setDisable(false);
                txtconfirmCases.requestFocus();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    public void txtconfirmCasesOnAction(ActionEvent actionEvent) {
        txtrecovered.requestFocus();
    }

    public void txtrecoveredOnAction(ActionEvent actionEvent) {
        txtdeaths.requestFocus();
    }

    public void txtdeathsOnAction(ActionEvent actionEvent) {
        btnupdate.requestFocus();
    }

    public void chkupdateOnAction(ActionEvent actionEvent) {
        enableAll();
        txtconfirmCases.requestFocus();
        btnupdate.setDisable(false);
    }

    public void btnupdateOnMouseEntered(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1.05);
        s.setFromY(1.05);
        s.setToX(1.05);
        s.setToY(1.05);
        s.play();
    }

    public void btnupdateOnMouseExited(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1);
        s.setFromY(1);
        s.setToX(1);
        s.setToY(1);
        s.play();
    }

    public void imgHomeOnMouseEntered(MouseEvent mouseEvent) {
        ImageView imageView = (ImageView) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), imageView);
        s.setFromX(1.1);
        s.setFromY(1.1);
        s.setToX(1.1);
        s.setToY(1.1);
        s.play();
    }

    public void imgHomeOnMouseExited(MouseEvent mouseEvent) {
        ImageView imageView = (ImageView) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), imageView);
        s.setFromX(1);
        s.setFromY(1);
        s.setToX(1);
        s.setToY(1);
        s.play();
    }

    public void btnhomeOnMouseEntered(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1.1);
        s.setFromY(1.1);
        s.setToX(1.1);
        s.setToY(1.1);
        s.play();
    }

    public void btnhomeOnMouseExited(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1);
        s.setFromY(1);
        s.setToX(1);
        s.setToY(1);
        s.play();
    }

    public void imgHomeOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/covid19/view/adminmain.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }
}
