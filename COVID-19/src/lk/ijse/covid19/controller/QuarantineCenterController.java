package lk.ijse.covid19.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import java.util.Optional;

public class QuarantineCenterController {
    public AnchorPane root;
    public Button btnhome;
    public ListView lstQuarantine;
    public TextField txtsearchquarantine;
    public Button btnnewQuarantine;
    public TextField txtqid;
    public TextField txtqname;
    public TextField txtqcity;
    public ComboBox cmbdistrict;
    public TextField txtcapacity;
    public TextField txthead;
    public TextField txtheadcontact;
    public TextField txtqcontact1;
    public TextField txtqcontact2;
    public Button btnsave;
    public Button btnremove;
    public Label lblfilter;
    public Label lblname;
    public Label lblcity;
    public Label lblcapacity;
    public Label lblhead;
    public Label lblhContact;
    public Label lblContact1;
    public Label lblContact2;
    public ImageView imgHome;

    public void initialize(){
        txtqid.setDisable(true);
        setDisableAll();
        setTooltip_enableLabels();
        btnsave.setDisable(true);
        btnremove.setDisable(true);
        btnnewQuarantine.requestFocus();
        loadCombo();
        loadQuarantineCenters();

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
                        Stage sb = (Stage)lblcapacity.getScene().getWindow();//use any one object
                        sb.close();
                    }
                }
            }
        });

        lstQuarantine.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(lstQuarantine.getSelectionModel().getSelectedItems().isEmpty()){
                    clearAll();
                    return;
                }

                String name = lstQuarantine.getSelectionModel().getSelectedItem().toString();

                Connection connection = DBConnection.getInstance().getConnection();
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("select * from quarantine_center where qname = ?");
                    preparedStatement.setObject(1,name);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet.next()){
                        txtqid.setText(resultSet.getString(1));
                        txtqname.setText(resultSet.getString(2));
                        txtqcity.setText(resultSet.getString(3));
                        cmbdistrict.getSelectionModel().select(resultSet.getString(4));
                        txtcapacity.setText(resultSet.getString(5));
                        txthead.setText(resultSet.getString(6));
                        txtheadcontact.setText(resultSet.getString(7));
                        txtqcontact1.setText(resultSet.getString(8));
                        txtqcontact2.setText(resultSet.getString(9));

                        setEnableAll();
                        btnsave.setText("Update");
                        btnsave.setDisable(false);
                        btnremove.setDisable(false);

                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
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

    public void lstQuarantineOnKeyReleased(KeyEvent keyEvent) {
    }

    public void btnnewQuarantineOnAction(ActionEvent actionEvent) {
        clearAll();
        generateID();
        setEnableAll();
        setTooltip_enableLabels();
        btnsave.setDisable(false);
        btnremove.setDisable(true);
        btnsave.setText("Save");
        txtqname.requestFocus();
        lstQuarantine.getSelectionModel().clearSelection();
    }

    public void txtqidOnAction(ActionEvent actionEvent) {
        txtqname.requestFocus();
    }

    public void txtqnameOnAction(ActionEvent actionEvent) {
        txtqcity.requestFocus();
    }

    public void txtqcityOnAction(ActionEvent actionEvent) {
        cmbdistrict.requestFocus();
    }

    public void cmbdistrictOnAction(ActionEvent actionEvent) {
        txtcapacity.requestFocus();
    }

    public void txtcapacityOnAction(ActionEvent actionEvent) {
        txthead.requestFocus();
    }

    public void txtheadOnAction(ActionEvent actionEvent) {
        txtheadcontact.requestFocus();
    }

    public void txtheadcontactOnAction(ActionEvent actionEvent) {
        txtqcontact1.requestFocus();
    }

    public void txtqcontact1OnAction(ActionEvent actionEvent) {
        txtqcontact2.requestFocus();
    }

    public void txtqcontact2OnAction(ActionEvent actionEvent) {
        btnsave.requestFocus();
    }

    public void txtsearchquarantineOnKeyReleased(KeyEvent keyEvent) {
        String qname = txtsearchquarantine.getText();
        ObservableList items = lstQuarantine.getItems();
        items.clear();
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select qname from quarantine_center where qname like '%" + qname + "%'");
            while(resultSet.next()){
                items.add(resultSet.getString(1));
            }
            lstQuarantine.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnsaveOnAction(ActionEvent actionEvent) {

        if(txtqname.getText().trim().isEmpty()){
            txtqname.requestFocus();
        }else if(txtqcity.getText().trim().isEmpty()){
            txtqcity.requestFocus();
        }else if(cmbdistrict.getSelectionModel().isEmpty()){
            cmbdistrict.requestFocus();
        }else if(txtcapacity.getText().trim().isEmpty() | !txtcapacity.getText().matches("\\d+")){
            txtcapacity.requestFocus();
        }else if(txthead.getText().trim().isEmpty()){
            txthead.requestFocus();
        }else if(txtheadcontact.getText().trim().isEmpty() | !txtheadcontact.getText().matches("\\d{3}[-]\\d{7}")){
            txtheadcontact.requestFocus();
        }else if(txtqcontact1.getText().trim().isEmpty() | !txtqcontact1.getText().matches("\\d{3}[-]\\d{7}")){
            txtqcontact1.requestFocus();
        }else if(txtqcontact2.getText().trim().isEmpty() | !txtqcontact2.getText().matches("\\d{3}[-]\\d{7}")){
            txtqcontact2.requestFocus();
        }else{


            if(btnsave.getText().equals("Save")){
                String qid = txtqid.getText();
                String qname = txtqname.getText();
                String qcity = txtqcity.getText();
                String district = cmbdistrict.getSelectionModel().getSelectedItem().toString();
                int capacity = Integer.parseInt(txtcapacity.getText());
                String head = txthead.getText();
                String headcontact = txtheadcontact.getText();
                String qcontact1 = txtqcontact1.getText();
                String qcontact2 = txtqcontact2.getText();

                Connection connection = DBConnection.getInstance().getConnection();

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("Insert into quarantine_center values(?,?,?,?,?,?,?,?,?,?,?)");
                    preparedStatement.setObject(1,qid);
                    preparedStatement.setObject(2,qname);
                    preparedStatement.setObject(3,qcity);
                    preparedStatement.setObject(4,district);
                    preparedStatement.setObject(5,capacity);
                    preparedStatement.setObject(6,head);
                    preparedStatement.setObject(7,headcontact);
                    preparedStatement.setObject(8,qcontact1);
                    preparedStatement.setObject(9,qcontact2);
                    preparedStatement.setObject(10,LoginController.userid);
                    preparedStatement.setObject(11,"Not Reserved");

                    int affectedrows = preparedStatement.executeUpdate();

                    if (affectedrows>0){
                        new Alert(Alert.AlertType.INFORMATION,"Successfully Saved!!").show();
                        clearAll();
                        setDisableAll();
                        btnsave.setText("Save");
                        btnremove.setDisable(true);
                        btnsave.setDisable(true);
                        btnnewQuarantine.requestFocus();
                        loadQuarantineCenters();
                    }else{
                        new Alert(Alert.AlertType.INFORMATION,"Something went wrong!!").show();
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }else{

                Connection connection1 = DBConnection.getInstance().getConnection();

                String qid = txtqid.getText();
                String qname = txtqname.getText();
                String qcity = txtqcity.getText();
                String district = cmbdistrict.getSelectionModel().getSelectedItem().toString();
                int capacity = Integer.parseInt(txtcapacity.getText());
                String head = txthead.getText();
                String headcontact = txtheadcontact.getText();
                String qcontact1 = txtqcontact1.getText();
                String qcontact2 = txtqcontact2.getText();

                try {
                    PreparedStatement preparedStatement1 = connection1.prepareStatement("update quarantine_center set qname = ? , qcity = ? , qdistrict = ? , qcapacity = ? , qhead = ? , qhead_contact = ? , qcontact1 = ? , qcontact2 = ? where qid = ?");
                    preparedStatement1.setObject(1,qname);
                    preparedStatement1.setObject(2,qcity);
                    preparedStatement1.setObject(3,district);
                    preparedStatement1.setObject(4,capacity);
                    preparedStatement1.setObject(5,head);
                    preparedStatement1.setObject(6,headcontact);
                    preparedStatement1.setObject(7,qcontact1);
                    preparedStatement1.setObject(8,qcontact2);
                    preparedStatement1.setObject(9,qid);

                    int affectedrow = preparedStatement1.executeUpdate();

                    if (affectedrow>0){
                        new Alert(Alert.AlertType.INFORMATION,"Successfully Updated!!").show();
                        clearAll();
                        setDisableAll();
                        btnsave.setText("Save");
                        btnremove.setDisable(true);
                        btnsave.setDisable(true);
                        btnnewQuarantine.requestFocus();
                        loadQuarantineCenters();
                    }else{
                        new Alert(Alert.AlertType.INFORMATION,"Something went wrong!!").show();
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        }
    }

    public void btnremoveOnAction(ActionEvent actionEvent) {
        String qid = txtqid.getText();
        Connection connection = DBConnection.getInstance().getConnection();

        try {

            Statement ps = connection.createStatement();
            ResultSet resultSet = ps.executeQuery("select status from quarantine_center where qid = '"+qid+"'");
            resultSet.next();
            String status = resultSet.getString(1);

            if (status.equals("Reserved")) {
                new Alert(Alert.AlertType.ERROR, "This Quarantine Center is Reserved by a Quarantine Center IT person. If You want to remove this Quarantine Center you should remove the user first !!", ButtonType.OK).showAndWait();

            }else {

                PreparedStatement preparedStatement = connection.prepareStatement("delete from quarantine_center where qid = ?");
                preparedStatement.setObject(1, qid);

                Optional<ButtonType> alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to remove this quarantine center!!", ButtonType.YES, ButtonType.NO).showAndWait();
                if (alert.get().equals(ButtonType.YES)) {
                    preparedStatement.executeUpdate();
                    clearAll();
                    setDisableAll();
                    btnsave.setText("Save");
                    btnremove.setDisable(true);
                    btnsave.setDisable(true);
                    btnnewQuarantine.requestFocus();
                    loadQuarantineCenters();
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void generateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select qid from quarantine_center order by qid desc limit 1");
            if(resultSet.next()){
                String oldID = resultSet.getString(1);
                int newID = Integer.parseInt(oldID.substring(1, 4)) + 1;
                if(newID < 10){
                    txtqid.setText("Q00"+newID);
                }else if(newID < 100){
                    txtqid.setText("Q0"+newID);
                }else{
                    txtqid.setText("Q"+newID);
                }
            }else{
                txtqid.setText("Q001");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadCombo(){
        ObservableList items = cmbdistrict.getItems();
        items.clear();
        items.add("Jaffna");
        items.add("Kilinochchi");
        items.add("Mannar");
        items.add("Mullaitivu");
        items.add("Vavuniya");
        items.add("Puttalam");
        items.add("Kurunegala");
        items.add("Gampaha");
        items.add("Colombo");
        items.add("Kalutara");
        items.add("Anuradhapura");
        items.add("Polonnaruwa");
        items.add("Matale");
        items.add("Kandy");
        items.add("Nuwara Eliya");
        items.add("Kegalle");
        items.add("Ratnapura");
        items.add("Trincomalee");
        items.add("Batticaloa");
        items.add("Ampara");
        items.add("Badulla");
        items.add("Monaragala");
        items.add("Hambantota");
        items.add("Matara");
        items.add("Galle");
    }

    public void loadQuarantineCenters(){
        Connection connection = DBConnection.getInstance().getConnection();
        ObservableList items = lstQuarantine.getItems();
        items.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select qname from quarantine_center");
            while(resultSet.next()){
                items.add(resultSet.getString(1));
            }
            lstQuarantine.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setDisableAll(){
        lblfilter.setVisible(false);
        lblname.setVisible(false);
        lblcity.setVisible(false);
        lblcapacity.setVisible(false);
        lblhContact.setVisible(false);
        lblhead.setVisible(false);
        lblContact1.setVisible(false);
        lblContact2.setVisible(false);
        txtqname.setDisable(true);
        txtqcity.setDisable(true);
        cmbdistrict.setDisable(true);
        txtcapacity.setDisable(true);
        txthead.setDisable(true);
        txtheadcontact.setDisable(true);
        txtqcontact1.setDisable(true);
        txtqcontact2.setDisable(true);
    }
    public void setEnableAll(){
        txtqname.setDisable(false);
        txtqcity.setDisable(false);
        cmbdistrict.setDisable(false);
        txtcapacity.setDisable(false);
        txthead.setDisable(false);
        txtheadcontact.setDisable(false);
        txtqcontact1.setDisable(false);
        txtqcontact2.setDisable(false);
    }

    public void clearAll(){
        txtqid.clear();
        txtqname.clear();
        txtqcity.clear();
        cmbdistrict.getSelectionModel().clearSelection();
        txtcapacity.clear();
        txthead.clear();
        txtheadcontact.clear();
        txtqcontact1.clear();
        txtqcontact2.clear();
        txtsearchquarantine.clear();
    }

    private void setTooltip_enableLabels() {

        txtqname.setTooltip(new Tooltip("Quarantine Center Name"));
        txtqcity.setTooltip(new Tooltip("City"));
        txtcapacity.setTooltip(new Tooltip("Capacity"));
        txthead.setTooltip(new Tooltip("Head Name"));
        txtheadcontact.setTooltip(new Tooltip("Head Contact"));
        txtqcontact1.setTooltip(new Tooltip("Quarantine Center Contact No 1"));
        txtqcontact2.setTooltip(new Tooltip("Quarantine Center Contact No 2"));
        txtsearchquarantine.setTooltip(new Tooltip("Filter By Name"));

        txtqname.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblname.setVisible(true);
                } else {
                    lblname.setVisible(false);
                }
            }
        });

        txtqcity.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblcity.setVisible(true);
                } else {
                    lblcity.setVisible(false);
                }
            }
        });

        txtcapacity.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblcapacity.setVisible(true);
                } else {
                    lblcapacity.setVisible(false);
                }
            }
        });

        txthead.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblhead.setVisible(true);
                } else {
                    lblhead.setVisible(false);
                }
            }
        });

        txtheadcontact.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblhContact.setVisible(true);
                } else {
                    lblhContact.setVisible(false);
                }
            }
        });

        txtqcontact1.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblContact1.setVisible(true);
                } else {
                    lblContact1.setVisible(false);
                }
            }
        });

        txtqcontact2.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblContact2.setVisible(true);
                } else {
                    lblContact2.setVisible(false);
                }
            }
        });

        txtsearchquarantine.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblfilter.setVisible(true);
                } else {
                    lblfilter.setVisible(false);
                }
            }
        });

    }

    public void btnhomeOnMouseEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
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

    public void btnnewQuarantineOnMouseEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
    }

    public void btnsaveOnMouseEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
    }

    public void btnremoveOnMouseEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
    }

    public void btnremoveOnMouseExited(MouseEvent mouseEvent) {
        exited(mouseEvent);
    }

    public void btnsaveOnMouseExited(MouseEvent mouseEvent) {
        exited(mouseEvent);
    }

    public void btnnewQuarantineOnMouseExited(MouseEvent mouseEvent) {
        exited(mouseEvent);
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

    public void btnhomeOnMouseExited(MouseEvent mouseEvent) {
        exited(mouseEvent);
    }

    private void entered(MouseEvent mouseEvent){
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1.05);
        s.setFromY(1.05);
        s.setToX(1.05);
        s.setToY(1.05);
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

    public void imgHomeOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/covid19/view/adminmain.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }
}
