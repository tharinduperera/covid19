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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.covid19.db.DBConnection;
import lk.ijse.covid19.util.UserTM;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Optional;
import java.util.Random;

public class UserController {
    public AnchorPane root;
    public Button btnhome;
    public Button btnnewUser;
    public TextField txtname;
    public TextField txtcontact;
    public TextField txtemail;
    public TextField txtusername;
    public TextField txtfilter;
    public ComboBox cmbrole;
    public TableView<UserTM> tbluser;
    public Button btnsave;
    public ComboBox cmbhospital;
    public PasswordField txtpassword;
    public ComboBox cmbquarantineCenter;
    public ImageView imgEyeopen;
    public TextField txtpassword1;
    public ImageView imgEyeclose;
    public Label lblName;
    public Label lblContact;
    public Label lblEmail;
    public Label lblUsername;
    public Label lblPassword;
    public Label lblfilter;
    public ImageView imgHome;
    private String id;
    private String hqid;
    private String updateid;


    public void initialize(){

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
                        Stage sb = (Stage)lblPassword.getScene().getWindow();//use any one object
                        sb.close();
                    }
                }
            }
        });


        loadCombo();
        setDisableAll();
        loadHospitals();
        loadQuarantineCenters();
        btnsave.setDisable(true);
        cmbquarantineCenter.setVisible(false);
        cmbhospital.setVisible(false);
        txtpassword1.setVisible(false);
        imgEyeclose.setVisible(false);
        setTooltip_enableLabels();
        btnnewUser.requestFocus();



        tbluser.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("username"));
        tbluser.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tbluser.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("role"));
        tbluser.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("removebtn"));

        loadTable();

        tbluser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<UserTM>() {
            @Override
            public void changed(ObservableValue<? extends UserTM> observable, UserTM oldValue, UserTM newValue) {
                if(tbluser.getSelectionModel().getSelectedItem() == null){
                    return;
                }

                Connection connection = DBConnection.getInstance().getConnection();
                UserTM selectedItem = tbluser.getSelectionModel().getSelectedItem();
                btnsave.setText("Update");

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("select * from users where username = ? and name = ? and role = ?");
                    preparedStatement.setObject(1,selectedItem.getUsername());
                    preparedStatement.setObject(2,selectedItem.getName());
                    preparedStatement.setObject(3,selectedItem.getRole());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    id = resultSet.getString(1);
                    System.out.println(id+"idthariya");
                    txtname.setText(resultSet.getString(2));
                    txtcontact.setText(resultSet.getString(3));
                    txtemail.setText(resultSet.getString(4));
                    txtusername.setText(resultSet.getString(5));
                    txtpassword.setText(resultSet.getString(6));
                    String role = resultSet.getString(7);
                    cmbrole.getSelectionModel().select(role);
                    if(role.equals("Hospital IT")){
                        cmbhospital.setVisible(true);
                        cmbquarantineCenter.setVisible(false);
                        preparedStatement = connection.prepareStatement("select hqid from users where id = ?");
                        preparedStatement.setObject(1,id);
                        resultSet = preparedStatement.executeQuery();
                        resultSet.next();
                        hqid = resultSet.getString(1);
                        updateid = hqid;
                        preparedStatement = connection.prepareStatement("select hname from hospital where hid = ?");
                        preparedStatement.setObject(1,hqid);
                        resultSet = preparedStatement.executeQuery();
                        resultSet.next();
                        String hname = resultSet.getString(1);
                        loadHospitals();
                        cmbhospital.getSelectionModel().select(hname);
                    }else if(role.equals("Quarantine Center IT")){
                        cmbquarantineCenter.setVisible(true);
                        cmbhospital.setVisible(false);
                        preparedStatement = connection.prepareStatement("select hqid from users where id = ?");
                        preparedStatement.setObject(1,id);
                        resultSet = preparedStatement.executeQuery();
                        resultSet.next();
                        hqid = resultSet.getString(1);
                        updateid = hqid;
                        preparedStatement = connection.prepareStatement("select qname from quarantine_center where qid = ?");
                        preparedStatement.setObject(1,hqid);
                        resultSet = preparedStatement.executeQuery();
                        resultSet.next();
                        String qname = resultSet.getString(1);
                        cmbquarantineCenter.getSelectionModel().select(qname);
                    }
                    setEnableAll();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                ObservableList<UserTM> items = tbluser.getItems();
                Button button = selectedItem.getRemovebtn();
                String id = null;
                button.setOnAction(event ->
                        deleteUser()
                );


                btnsave.setDisable(false);
                btnsave.setText("Update");

            }
        });

    }

    private void deleteUser() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to Remove this Member!!!", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if(buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBConnection.getInstance().getConnection();

            String role = cmbrole.getSelectionModel().getSelectedItem().toString();

            switch(role){
                case "Hospital IT":
                    try {
                        PreparedStatement preparedStatement = connection.prepareStatement("update hospital set status = 'Not Reserved' where hid = ?");
                        preparedStatement.setObject(1,hqid);
                        preparedStatement.executeUpdate();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;
                case "Quarantine Center IT":
                    try {
                        PreparedStatement preparedStatement = connection.prepareStatement("update quarantine_center set status = 'Not Reserved' where qid = ?");
                        preparedStatement.setObject(1,hqid);
                        preparedStatement.executeUpdate();

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;
                default:
                    break;
            }


            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from users where id = ?");
                preparedStatement.setObject(1,id);
                preparedStatement.executeUpdate();
                loadTable();
                btnnewUser.setDisable(false);
                btnnewUser.requestFocus();
                clearAll();
                setDisableAll();
                btnsave.setText("Save");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    private void loadTable() {
        ObservableList<UserTM> userTable = tbluser.getItems();
        userTable.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select username,name,role from users");
            while(resultSet.next()){
                String username = resultSet.getString(1);
                String name = resultSet.getString(2);
                String role = resultSet.getString(3);
                Button button = new Button("Delete");
                button.setStyle("-fx-background-color:#d12449");
                button.setMaxWidth(180);
                userTable.add(new UserTM(username,name,role,button));
            }
            tbluser.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnhomeOnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/covid19/view/adminmain.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    public void btnnewUserOnAction(ActionEvent actionEvent) {
        generateID();
        setEnableAll();
        setTooltip_enableLabels();
        clearAll();
        loadQuarantineCenters();
        loadHospitals();
        txtpassword.setText(randomNumbers());
        txtusername.setText(randomNumbers());
        btnsave.setDisable(false);
        btnsave.setText("Save");
        txtname.requestFocus();

//        if(txtname.isFocused()){
//            txtname.setTooltip(new ToolBar("dv"));
//        }

    }

    public void txtnameOnAction(ActionEvent actionEvent) {
        txtcontact.requestFocus();
    }

    public void txtcontactOnAction(ActionEvent actionEvent) {
        txtemail.requestFocus();
    }

    public void txtemailOnAction(ActionEvent actionEvent) {
        txtusername.requestFocus();
    }

    public void txtusernameOnAction(ActionEvent actionEvent) {
        txtpassword.requestFocus();
    }

    public void txtfilterOnKeyReleased(KeyEvent keyEvent) {
        String text = txtfilter.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select username,name,role from users where name like '%"+text+"%' or username like '%"+text+"%'");
            ObservableList<UserTM> items = tbluser.getItems();
            items.clear();

            while(resultSet.next()){
                String username = resultSet.getString(1);
                String name = resultSet.getString(2);
                String role = resultSet.getString(3);
                Button button = new Button("Delete");
                button.setStyle("-fx-background-color: #d10f38");
                button.setMaxWidth(180);
                items.add(new UserTM(username,name,role,button));
            }

            tbluser.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void cmbroleOnAction(ActionEvent actionEvent) {
        if(cmbrole.getSelectionModel().isEmpty()){
            return;
        }
        String role = cmbrole.getSelectionModel().getSelectedItem().toString();
        System.out.println(role+"role");
        if(role.equals("Hospital IT")){
            cmbhospital.setVisible(true);
            cmbquarantineCenter.setVisible(false);
        }else if(role.equals("Quarantine Center IT")){
            cmbquarantineCenter.setVisible(true);
            cmbhospital.setVisible(false);
        }else {
            cmbquarantineCenter.setVisible(false);
            cmbhospital.setVisible(false);
        }


    }

    public void btnsaveOnAction(ActionEvent actionEvent) {

        Connection connection = DBConnection.getInstance().getConnection();

        if(txtname.getText().trim().isEmpty()){
            txtname.requestFocus();
        }else if(txtcontact.getText().trim().isEmpty() | !txtcontact.getText().matches("\\d{3}[-]\\d{7}")){
            txtcontact.requestFocus();
        }else if(txtemail.getText().trim().isEmpty() | !txtemail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            txtemail.requestFocus();
        }else{
            String name = txtname.getText();
            String contact = txtcontact.getText();
            String email = txtemail.getText();
            String username = txtusername.getText();
            String password = txtpassword.getText();
            String role = cmbrole.getSelectionModel().getSelectedItem().toString();

            try {
                if(cmbrole.getSelectionModel().getSelectedItem().equals("Hospital IT")) {

                    PreparedStatement ps0 = connection.prepareStatement("update hospital set status = 'Not Reserved' where hid = ?");
                    ps0.setObject(1,updateid);
                    int res0 = ps0.executeUpdate();
                    if(res0<0){
                        System.out.println("hospital updated");
                    }

                    PreparedStatement pss = connection.prepareStatement("update quarantine_center set status = 'Not Reserved' where qid = ?");
                    pss.setObject(1,updateid);
                    int ress = pss.executeUpdate();
                    if(ress<0){
                        System.out.println("quarantine updated");
                    }

                    String hospital = cmbhospital.getSelectionModel().getSelectedItem().toString();
                    PreparedStatement preparedStatement = connection.prepareStatement("select hid from hospital where hname = ?");
                    preparedStatement.setObject(1, hospital);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    hqid = resultSet.getString(1);

                    PreparedStatement ps = connection.prepareStatement("update hospital set status = 'Reserved' where hid = ?");
                    ps.setObject(1,hqid);
                    int res = ps.executeUpdate();
                    if(res<0){
                        System.out.println("hospital updated");
                    }

                }else if(cmbrole.getSelectionModel().getSelectedItem().equals("Quarantine Center IT")) {

                    PreparedStatement ps0 = connection.prepareStatement("update quarantine_center set status = 'Not Reserved' where qid = ?");
                    ps0.setObject(1,updateid);
                    int res0 = ps0.executeUpdate();
                    if(res0<0){
                        System.out.println("quarantine updated");
                    }

                    PreparedStatement pse = connection.prepareStatement("update hospital set status = 'Not Reserved' where hid = ?");
                    pse.setObject(1,updateid);
                    int rese = pse.executeUpdate();
                    if(rese<0){
                        System.out.println("hospital updated");
                    }

                    String quarantine = cmbquarantineCenter.getSelectionModel().getSelectedItem().toString();
                    PreparedStatement preparedStatement1 = connection.prepareStatement("select qid from quarantine_center where qname = ?");
                    preparedStatement1.setObject(1, quarantine);
                    ResultSet resultSet1 = preparedStatement1.executeQuery();
                    resultSet1.next();
                    hqid = resultSet1.getString(1);

                    PreparedStatement ps1 = connection.prepareStatement("update quarantine_center set status = 'Reserved' where qid = ?");
                    ps1.setObject(1,hqid);
                    int res = ps1.executeUpdate();
                    if(res<0){
                        System.out.println("quarantine updated");
                    }

                }else{
                    hqid = "All";

                }

            if(btnsave.getText().equals("Save")){
                PreparedStatement preparedStatement3 = connection.prepareStatement("Insert into users values(?,?,?,?,?,?,?,?)");
                preparedStatement3.setObject(1,id);
                preparedStatement3.setObject(2,name);
                preparedStatement3.setObject(3,contact);
                preparedStatement3.setObject(4,email);
                preparedStatement3.setObject(5,username);
                preparedStatement3.setObject(6,password);
                preparedStatement3.setObject(7,role);
                preparedStatement3.setObject(8,hqid);
                int affectedrows = preparedStatement3.executeUpdate();

                if (affectedrows>0){
                    new Alert(Alert.AlertType.INFORMATION,"Successfully Saved!!").show();
                    clearAll();
                    setDisableAll();
                    loadTable();
                    btnsave.setText("Save");
                    btnsave.setDisable(true);
                    btnnewUser.requestFocus();
                }else{
                    new Alert(Alert.AlertType.INFORMATION,"Something went wrong!!").show();
                }

            }else{

                PreparedStatement preparedStatement4 = connection.prepareStatement("update users set name = ? , contact = ? , email = ? , username = ? , password = ? , role = ? , hqid = ? where id = ?");
                preparedStatement4.setObject(1,name);
                preparedStatement4.setObject(2,contact);
                preparedStatement4.setObject(3,email);
                preparedStatement4.setObject(4,username);
                preparedStatement4.setObject(5,password);
                preparedStatement4.setObject(6,role);
                preparedStatement4.setObject(7,hqid);
                preparedStatement4.setObject(8,id);

                int affectedrow = preparedStatement4.executeUpdate();

                if (affectedrow>0){
                    new Alert(Alert.AlertType.INFORMATION,"Successfully Updated!!").showAndWait();
                    clearAll();
                    setDisableAll();
                    loadTable();
                    btnsave.setText("Save");
                    btnsave.setDisable(true);
                    btnnewUser.requestFocus();
                }else{
                    new Alert(Alert.AlertType.INFORMATION,"Something went wrong!!").show();
                }
            }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }


    }

    public void cmbhospitalOnAction(ActionEvent actionEvent) {
        btnsave.requestFocus();
    }

    public void txtpasswordOnAction(ActionEvent actionEvent) {
        cmbrole.requestFocus();
    }

    public void cmbquarantineCenterOnAction(ActionEvent actionEvent) {
        btnsave.requestFocus();
    }

    public void generateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from users order by id desc limit 1");
            if(resultSet.next()){
                String oldID = resultSet.getString(1);
                int newID = Integer.parseInt(oldID.substring(1, 4)) + 1;
                if(newID < 10){
                    id = "U00"+newID;
                }else if(newID < 100){
                    id = "U0"+newID;
                }else{
                    id = "U"+newID;
                }
            }else{
                id = "U001";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadCombo(){
        ObservableList items = cmbrole.getItems();
        items.clear();
        items.add("Admin");
        items.add("PSTF Member");
        items.add("Hospital IT");
        items.add("Quarantine Center IT");
    }

    public void loadHospitals(){
        Connection connection = DBConnection.getInstance().getConnection();
        ObservableList items = cmbhospital.getItems();
        items.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select hname from hospital where status = 'Not Reserved'");
            while(resultSet.next()){
                items.add(resultSet.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadQuarantineCenters(){
        Connection connection = DBConnection.getInstance().getConnection();
        ObservableList items = cmbquarantineCenter.getItems();
        items.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select qname from quarantine_center where status = 'Not Reserved'");
            while(resultSet.next()){
                items.add(resultSet.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setDisableAll(){
        lblName.setVisible(false);
        lblContact.setVisible(false);
        lblEmail.setVisible(false);
        lblUsername.setVisible(false);
        lblPassword.setVisible(false);
        lblfilter.setVisible(false);
        txtname.setDisable(true);
        txtcontact.setDisable(true);
        txtemail.setDisable(true);
        txtusername.setDisable(true);
        txtpassword.setDisable(true);
        cmbrole.setDisable(true);
        imgEyeopen.setDisable(true);
        imgEyeclose.setDisable(true);

    }
    public void setEnableAll(){
        txtname.setDisable(false);
        txtcontact.setDisable(false);
        txtemail.setDisable(false);
        txtusername.setDisable(false);
        txtpassword.setDisable(false);
        cmbrole.setDisable(false);
        imgEyeopen.setDisable(false);
        imgEyeclose.setDisable(false);
    }

    public void clearAll(){
        txtname.clear();
        txtcontact.clear();
        txtemail.clear();
        txtusername.clear();
        txtpassword.clear();
        cmbrole.getSelectionModel().clearSelection();
        cmbhospital.getSelectionModel().clearSelection();
        cmbhospital.setVisible(false);
        cmbquarantineCenter.getSelectionModel().clearSelection();
        cmbquarantineCenter.setVisible(false);
    }


    public void imgEyeopenOnMouseClicked(MouseEvent mouseEvent) {
        String text = txtpassword.getText();
        txtpassword1.setText(text);
        txtpassword.setVisible(false);
        txtpassword1.setVisible(true);
        imgEyeopen.setVisible(false);
        imgEyeclose.setVisible(true);
    }

    public void txtpassword1OnAction(ActionEvent actionEvent) {
        cmbrole.requestFocus();
    }

    public void imgEyecloseOnMouseClicked(MouseEvent mouseEvent) {
        String text1 = txtpassword1.getText();
        txtpassword.setText(text1);
        txtpassword1.setVisible(false);
        txtpassword.setVisible(true);
        imgEyeclose.setVisible(false);
        imgEyeopen.setVisible(true);
    }

    public String randomNumbers(){
        byte[] randomBytes = new byte[10];
        Random rnd = new Random();
        for (int i = 0; i < randomBytes.length; i++) {
            randomBytes[i] = (byte) (rnd.nextInt(122 - 65) + 65);
        }
        return new String(randomBytes, StandardCharsets.US_ASCII);
    }

    private void setTooltip_enableLabels() {

        txtname.setTooltip(new Tooltip("Name"));
        txtcontact.setTooltip(new Tooltip("Contact"));
        txtemail.setTooltip(new Tooltip("E-mail"));
        txtusername.setTooltip(new Tooltip("Username"));
        txtpassword.setTooltip(new Tooltip("Password"));
        txtpassword1.setTooltip(new Tooltip("Password"));
        txtfilter.setTooltip(new Tooltip("Filter by Username or Password"));

        txtname.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblName.setVisible(true);
                } else {
                    lblName.setVisible(false);
                }
            }
        });

        txtcontact.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblContact.setVisible(true);
                } else {
                    lblContact.setVisible(false);
                }
            }
        });

        txtemail.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblEmail.setVisible(true);
                } else {
                    lblEmail.setVisible(false);
                }
            }
        });

        txtusername.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblUsername.setVisible(true);
                } else {
                    lblUsername.setVisible(false);
                }
            }
        });

        txtpassword.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblPassword.setVisible(true);
                } else {
                    lblPassword.setVisible(false);
                }
            }
        });

        txtpassword1.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lblPassword.setVisible(true);
                } else {
                    lblPassword.setVisible(false);
                }
            }
        });

        txtfilter.focusedProperty().addListener(new ChangeListener<Boolean>() {
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

    public void btnnewUserOnMouseEntered(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1.1);
        s.setFromY(1.1);
        s.setToX(1.1);
        s.setToY(1.1);
        s.play();
    }

    public void btnsaveOnMouseEntered(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1.1);
        s.setFromY(1.1);
        s.setToX(1.1);
        s.setToY(1.1);
        s.play();
    }

    public void btnnewUserOnMouseExited(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        ScaleTransition s = new ScaleTransition(Duration.millis(100), button);
        s.setFromX(1);
        s.setFromY(1);
        s.setToX(1);
        s.setToY(1);
        s.play();
    }

    public void btnsaveOnMouseExited(MouseEvent mouseEvent) {
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
