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

public class HospitalController {
    public AnchorPane root;
    public Button btnhome;
    public ListView listHospitals;
    public TextField txtsearchHospital;
    public Button btnnewHospital;
    public TextField txthid;
    public TextField txthname;
    public TextField txthcity;
    public ComboBox cmbdistrict;
    public TextField txtcapacity;
    public TextField txtdirector;
    public TextField txtdirectorContact;
    public TextField txthcontact1;
    public TextField txthcontact2;
    public TextField txthfax;
    public TextField txthemail;
    public Button btnSave;
    public Button btnremove;
    public Label lblHname;
    public Label lblCity;
    public Label lblCapacity;
    public Label lblDirector;
    public Label lblDirectorContact;
    public Label lblContact1;
    public Label lblContact2;
    public Label lblFax;
    public Label lblEmail;
    public Label lblfilter;

    public void initialize(){
        txthid.setDisable(true);
        setDisableAll();
        setTooltip_enableLabels();
        btnnewHospital.requestFocus();
        btnSave.setDisable(true);
        btnremove.setDisable(true);
        loadCombo();
        loadHospitals();

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
                        Stage sb = (Stage)lblCapacity.getScene().getWindow();//use any one object
                        sb.close();
                    }
                }
            }
        });


        listHospitals.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(listHospitals.getSelectionModel().getSelectedItems().isEmpty()){
                    clearAll();
                    return;
                }

                String name = listHospitals.getSelectionModel().getSelectedItem().toString();

                Connection connection = DBConnection.getInstance().getConnection();
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("select * from hospital where hname = ?");
                    preparedStatement.setObject(1,name);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet.next()){
                        txthid.setText(resultSet.getString(1));
                        txthname.setText(resultSet.getString(2));
                        txthcity.setText(resultSet.getString(3));
                        cmbdistrict.getSelectionModel().select(resultSet.getString(4));
                        txtcapacity.setText(resultSet.getString(5));
                        txtdirector.setText(resultSet.getString(6));
                        txtdirectorContact.setText(resultSet.getString(7));
                        txthcontact1.setText(resultSet.getString(8));
                        txthcontact2.setText(resultSet.getString(9));
                        txthfax.setText(resultSet.getString(10));
                        txthemail.setText(resultSet.getString(11));

                        setEnableAll();
                        btnSave.setText("Update");
                        btnSave.setDisable(false);
                        btnremove.setDisable(false);
                        txthname.requestFocus();

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

    public void txtsearchHospitalOnKeyReleased(KeyEvent keyEvent) {
        String hname = txtsearchHospital.getText();
        ObservableList items = listHospitals.getItems();
        items.clear();
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select hname from hospital where hname like '%" + hname + "%'");
            while(resultSet.next()){
                items.add(resultSet.getString(1));
            }
            listHospitals.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnnewHospitalOnAction(ActionEvent actionEvent) {
        clearAll();
        setEnableAll();
        generateID();
        setTooltip_enableLabels();
        btnSave.setDisable(false);
        btnremove.setDisable(true);
        btnSave.setText("Save");
        txthname.requestFocus();
        listHospitals.getSelectionModel().clearSelection();
    }

    public void txthidOnAction(ActionEvent actionEvent) {
        txthname.requestFocus();
    }

    public void txthnameOnAction(ActionEvent actionEvent) {
        txthcity.requestFocus();
    }

    public void txthcityOnAction(ActionEvent actionEvent) {
        cmbdistrict.requestFocus();
    }

    public void cmbdistrictOnAction(ActionEvent actionEvent) {
        txtcapacity.requestFocus();
    }

    public void txtcapacityOnAction(ActionEvent actionEvent) {
        txtdirector.requestFocus();
    }

    public void txtdirectorOnAction(ActionEvent actionEvent) {
        txtdirectorContact.requestFocus();
    }

    public void txtdirectorContactOnAction(ActionEvent actionEvent) {
        txthcontact1.requestFocus();
    }

    public void txthcontact1OnAction(ActionEvent actionEvent) {
        txthcontact2.requestFocus();
    }

    public void txthcontact2OnAction(ActionEvent actionEvent) {
        txthfax.requestFocus();
    }

    public void txthfaxOnAction(ActionEvent actionEvent) {
        txthemail.requestFocus();
    }

    public void txthemailOnAction(ActionEvent actionEvent) {
        btnSave.requestFocus();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {

        if(txthname.getText().trim().isEmpty()){
            txthname.requestFocus();
        }else if(txthcity.getText().trim().isEmpty()){
            txthcity.requestFocus();
        }else if(cmbdistrict.getSelectionModel().isEmpty()){
            cmbdistrict.requestFocus();
        }else if(txtcapacity.getText().trim().isEmpty() | !txtcapacity.getText().matches("\\d+")){
            txtcapacity.requestFocus();
        }else if(txtdirector.getText().trim().isEmpty()){
            txtdirector.requestFocus();
        }else if(txtdirectorContact.getText().trim().isEmpty() | !txtdirectorContact.getText().matches("\\d{3}[-]\\d{7}")){
            txtdirectorContact.requestFocus();
        }else if(txthcontact1.getText().trim().isEmpty() | !txthcontact1.getText().matches("\\d{3}[-]\\d{7}")){
            txthcontact1.requestFocus();
        }else if(txthcontact2.getText().trim().isEmpty() | !txthcontact2.getText().matches("\\d{3}[-]\\d{7}")) {
            txthcontact2.requestFocus();
        }else if(txthfax.getText().trim().isEmpty() | !txthfax.getText().matches("\\d{3}[-]\\d{7}")) {
            txthfax.requestFocus();
        }else if(txthemail.getText().trim().isEmpty() | !txthemail.getText().matches( "^[A-Za-z0-9+_.-]+@(.+)$")) {
            txthemail.requestFocus();

        }else{
            if(btnSave.getText().equals("Save")){
                String hid = txthid.getText();
                String hname = txthname.getText();
                String hcity = txthcity.getText();
                String district = cmbdistrict.getSelectionModel().getSelectedItem().toString();
                int capacity = Integer.parseInt(txtcapacity.getText());
                String director = txtdirector.getText();
                String director_contact = txtdirectorContact.getText();
                String hcontact1 = txthcontact1.getText();
                String hcontact2 = txthcontact2.getText();
                String hfax = txthfax.getText();
                String hemail = txthemail.getText();

                Connection connection = DBConnection.getInstance().getConnection();

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("Insert into hospital values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    preparedStatement.setObject(1,hid);
                    preparedStatement.setObject(2,hname);
                    preparedStatement.setObject(3,hcity);
                    preparedStatement.setObject(4,district);
                    preparedStatement.setObject(5,capacity);
                    preparedStatement.setObject(6,director);
                    preparedStatement.setObject(7,director_contact);
                    preparedStatement.setObject(8,hcontact1);
                    preparedStatement.setObject(9,hcontact2);
                    preparedStatement.setObject(10,hfax);
                    preparedStatement.setObject(11,hemail);
                    preparedStatement.setObject(12,LoginController.userid);
                    preparedStatement.setObject(13,"Not Reserved");

                    int affectedrows = preparedStatement.executeUpdate();

                    if (affectedrows>0){
                        new Alert(Alert.AlertType.INFORMATION,"Successfully Saved!!").show();
                        clearAll();
                        setDisableAll();
                        btnSave.setText("Save");
                        btnremove.setDisable(true);
                        btnSave.setDisable(true);
                        btnnewHospital.requestFocus();
                        loadHospitals();
                    }else{
                        new Alert(Alert.AlertType.INFORMATION,"Something went wrong!!").show();
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }else{

                Connection connection1 = DBConnection.getInstance().getConnection();

                String hid = txthid.getText();
                String hname = txthname.getText();
                String hcity = txthcity.getText();
                String district = cmbdistrict.getSelectionModel().getSelectedItem().toString();
                int capacity = Integer.parseInt(txtcapacity.getText());
                String director = txtdirector.getText();
                String director_contact = txtdirectorContact.getText();
                String hcontact1 = txthcontact1.getText();
                String hcontact2 = txthcontact2.getText();
                String hfax = txthfax.getText();
                String hemail = txthemail.getText();

                try {
                    PreparedStatement preparedStatement1 = connection1.prepareStatement("update hospital set hname = ? , hcity = ? , hdistrict = ? , hcapacity = ? , director = ? , director_contact = ? , hcontact1 = ? , hcontact2 = ? , hfax = ? , hemail = ? where hid = ?");
                    preparedStatement1.setObject(1,hname);
                    preparedStatement1.setObject(2,hcity);
                    preparedStatement1.setObject(3,district);
                    preparedStatement1.setObject(4,capacity);
                    preparedStatement1.setObject(5,director);
                    preparedStatement1.setObject(6,director_contact);
                    preparedStatement1.setObject(7,hcontact1);
                    preparedStatement1.setObject(8,hcontact2);
                    preparedStatement1.setObject(9,hfax);
                    preparedStatement1.setObject(10,hemail);
                    preparedStatement1.setObject(11,hid);

                    int affectedrow = preparedStatement1.executeUpdate();

                    if (affectedrow>0){
                        new Alert(Alert.AlertType.INFORMATION,"Successfully Updated!!").show();
                        clearAll();
                        setDisableAll();
                        btnSave.setText("Save");
                        btnremove.setDisable(true);
                        btnSave.setDisable(true);
                        btnnewHospital.requestFocus();
                        loadHospitals();
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
        String hid = txthid.getText();
        Connection connection = DBConnection.getInstance().getConnection();

        try {

            Statement ps = connection.createStatement();
            ResultSet resultSet = ps.executeQuery("select status from hospital where hid = '"+hid+"'");
            resultSet.next();
            String status = resultSet.getString(1);
            if (status.equals("Reserved")) {
                new Alert(Alert.AlertType.ERROR, "This Hospital is Reserved by a Hospital IT person. If You want to remove this hospital you should remove the user first !!", ButtonType.OK).showAndWait();
            }else {

            PreparedStatement preparedStatement = connection.prepareStatement("delete from hospital where hid = ?");
            preparedStatement.setObject(1, hid);

            Optional<ButtonType> alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to remove this Hospital!!", ButtonType.YES, ButtonType.NO).showAndWait();
            if (alert.get().equals(ButtonType.YES)) {
                preparedStatement.executeUpdate();
                clearAll();
                setDisableAll();
                btnSave.setText("Save");
                btnremove.setDisable(true);
                btnSave.setDisable(true);
                btnnewHospital.requestFocus();
                loadHospitals();
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
            ResultSet resultSet = statement.executeQuery("select hid from hospital order by hid desc limit 1");
            if(resultSet.next()){
                String oldID = resultSet.getString(1);
                int newID = Integer.parseInt(oldID.substring(1, 4)) + 1;
                if(newID < 10){
                    txthid.setText("H00"+newID);
                }else if(newID < 100){
                    txthid.setText("H0"+newID);
                }else{
                    txthid.setText("H"+newID);
                }
            }else{
                txthid.setText("H001");
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

    public void loadHospitals(){
        Connection connection = DBConnection.getInstance().getConnection();
        ObservableList items = listHospitals.getItems();
        items.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select hname from hospital");
            while(resultSet.next()){
                items.add(resultSet.getString(1));
            }
            listHospitals.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setDisableAll(){
        txthname.setDisable(true);
        txthcity.setDisable(true);
        cmbdistrict.setDisable(true);
        txtcapacity.setDisable(true);
        txtdirector.setDisable(true);
        txtdirectorContact.setDisable(true);
        txthcontact1.setDisable(true);
        txthcontact2.setDisable(true);
        txthfax.setDisable(true);
        txthemail.setDisable(true);
        lblfilter.setVisible(false);
        lblHname.setVisible(false);
        lblCity.setVisible(false);
        lblCapacity.setVisible(false);
        lblDirector.setVisible(false);
        lblDirectorContact.setVisible(false);
        lblContact1.setVisible(false);
        lblContact2.setVisible(false);
        lblFax.setVisible(false);
        lblEmail.setVisible(false);
    }
    public void setEnableAll(){
        txthname.setDisable(false);
        txthcity.setDisable(false);
        cmbdistrict.setDisable(false);
        txtcapacity.setDisable(false);
        txtdirector.setDisable(false);
        txtdirectorContact.setDisable(false);
        txthcontact1.setDisable(false);
        txthcontact2.setDisable(false);
        txthfax.setDisable(false);
        txthemail.setDisable(false);
    }

    public void clearAll(){
        txthid.clear();
        txthname.clear();
        txthcity.clear();
        cmbdistrict.getSelectionModel().clearSelection();
        txtcapacity.clear();
        txtdirector.clear();
        txtdirectorContact.clear();
        txthcontact1.clear();
        txthcontact2.clear();
        txthfax.clear();
        txthemail.clear();
        txtsearchHospital.clear();
    }

    private void setTooltip_enableLabels() {

        txthname.setTooltip(new Tooltip("Hospital Name"));
        txthcity.setTooltip(new Tooltip("City"));
        txtcapacity.setTooltip(new Tooltip("Capacity"));
        txtdirector.setTooltip(new Tooltip("Director Name"));
        txtdirectorContact.setTooltip(new Tooltip("Director Contact"));
        txthcontact1.setTooltip(new Tooltip("Hospital Contact No 1"));
        txthcontact2.setTooltip(new Tooltip("Hospital Contact No 2"));
        txthfax.setTooltip(new Tooltip("Fax Number"));
        txthemail.setTooltip(new Tooltip("E-mail"));

        txthname.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblHname.setVisible(true);
                } else {
                    lblHname.setVisible(false);
                }
            }
        });

        txthcity.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblCity.setVisible(true);
                } else {
                    lblCity.setVisible(false);
                }
            }
        });

        txtcapacity.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblCapacity.setVisible(true);
                } else {
                    lblCapacity.setVisible(false);
                }
            }
        });

        txtdirector.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblDirector.setVisible(true);
                } else {
                    lblDirector.setVisible(false);
                }
            }
        });

        txtdirectorContact.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblDirectorContact.setVisible(true);
                } else {
                    lblDirectorContact.setVisible(false);
                }
            }
        });

        txthcontact1.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblContact1.setVisible(true);
                } else {
                    lblContact1.setVisible(false);
                }
            }
        });

        txthcontact2.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblContact2.setVisible(true);
                } else {
                    lblContact2.setVisible(false);
                }
            }
        });

        txthfax.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblFax.setVisible(true);
                } else {
                    lblFax.setVisible(false);
                }
            }
        });

        txthemail.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblEmail.setVisible(true);
                } else {
                    lblEmail.setVisible(false);
                }
            }
        });

        txtsearchHospital.focusedProperty().addListener(new ChangeListener<Boolean>() {
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

    public void btnnewHospitalOnMouseEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
    }

    public void btnSaveOnEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
    }

    public void btnremoveOnMouseEntered(MouseEvent mouseEvent) {
        entered(mouseEvent);
    }

    public void btnremoveOnMouseExited(MouseEvent mouseEvent) {
        exited(mouseEvent);
    }

    public void btnSaveOnExited(MouseEvent mouseEvent) {
        exited(mouseEvent);
    }

    public void btnnewHospitalOnMouseExited(MouseEvent mouseEvent) {
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
