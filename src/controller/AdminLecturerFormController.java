package controller;

import business.BOFactory;
import business.BOType;
import business.custom.CourseBO;
import business.custom.FacultyBO;
import business.custom.LecturerBO;
import business.custom.StudentBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.CourseTM;
import util.FacultyTM;
import util.LectureTM;
import util.StudentTM;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AdminLecturerFormController {
    public AnchorPane root;
    public JFXButton btnDashboard;
    public JFXButton btnCourses;
    public JFXButton btnModules;
    public JFXButton btnAccount;
    public JFXButton btnStudent;
    public JFXButton btnLecturers;
    public JFXComboBox<CourseTM> cmbCourseId;
    public TableView<LectureTM> tblAdminLecturer;
    public JFXButton btnAdd;
    public JFXTextField txtId;
    public JFXTextField txtTel;
    public JFXTextField txtAddress;
    public JFXTextField txtName;
    public JFXTextField txtPassword;
    public JFXTextField txtUsername;
    public JFXButton btnDelete;
    public JFXButton btnSave;
    public JFXTextField txtEmail;
    public JFXTextField txtCourseName;
    public JFXTextField txtNIC;
    private boolean readOnly;

    private LecturerBO lecturerBO = BOFactory.getInstance().getBO(BOType.LECTURER);
    private CourseBO courseBO = BOFactory.getInstance().getBO(BOType.COURSE);
    
    public void initialize(){
        //basic initialization
        disableFields();

        //load all lecturers
        loadAllLectures();

        //load all courses
        loadAllCourses();

        //when admin select courseId
        cmbCourseId.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CourseTM>() {
            @Override
            public void changed(ObservableValue<? extends CourseTM> observable, CourseTM oldValue, CourseTM newValue) {
                if(newValue == null){
                    txtCourseName.clear();
                    return;
                }
                txtCourseName.setText(newValue.getTitle());
                btnSave.setDisable(false);
            }
        });

        tblAdminLecturer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblAdminLecturer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblAdminLecturer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        tblAdminLecturer.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("contact"));
        tblAdminLecturer.getColumns().get(0).setStyle("-fx-alignment: center");
        tblAdminLecturer.getColumns().get(1).setStyle("-fx-alignment: center");
        tblAdminLecturer.getColumns().get(2).setStyle("-fx-alignment: center");
        tblAdminLecturer.getColumns().get(3).setStyle("-fx-alignment: center");

        btnSave.setDisable(true);

        tblAdminLecturer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LectureTM>() {
            @Override
            public void changed(ObservableValue<? extends LectureTM> observable, LectureTM oldValue, LectureTM newValue) {
                LectureTM selectedLecturerDetails = tblAdminLecturer.getSelectionModel().getSelectedItem();

                if(selectedLecturerDetails == null){
                    btnSave.setText("Save");
                    btnDelete.setDisable(true);
                    txtId.clear();
                    txtName.clear();
                    txtAddress.clear();
                    txtTel.clear();
                    txtEmail.clear();
                    txtUsername.clear();
                    txtPassword.clear();
                    txtNIC.clear();
                    cmbCourseId.getSelectionModel().clearSelection();
                    return;
                }
                btnSave.setText("Update");
                btnSave.setDisable(false);
                btnDelete.setDisable(false);
                cmbCourseId.setDisable(false);
                txtCourseName.setDisable(false);
                txtName.setDisable(false);
                txtAddress.setDisable(false);
                txtNIC.setDisable(false);
                txtTel.setDisable(false);
                txtEmail.setDisable(false);
                txtUsername.setDisable(false);
                txtPassword.setDisable(true);
                txtId.setText(selectedLecturerDetails.getId());
                txtName.setText(selectedLecturerDetails.getName());
                txtAddress.setText(selectedLecturerDetails.getAddress());
                txtNIC.setText(selectedLecturerDetails.getNic());
                txtTel.setText(selectedLecturerDetails.getContact());
                txtEmail.setText(selectedLecturerDetails.getEmail());
                txtPassword.setText(selectedLecturerDetails.getPassword());
                txtUsername.setText(selectedLecturerDetails.getUsername());

                String selectedCourseId = selectedLecturerDetails.getCourseId();
                ObservableList<CourseTM> courses = cmbCourseId.getItems();
                for (CourseTM course :courses) {
                    if(course.getId().equals(selectedCourseId)){
                        cmbCourseId.getSelectionModel().select(course);
                       txtCourseName.setText(course.getTitle());
                        if(!readOnly){
                            btnSave.setText("Update");
                        }
                        if(readOnly){
                            btnSave.setDisable(true);
                        }
                        cmbCourseId.setDisable(false);
                        Platform.runLater(()->{
                            txtName.requestFocus();
                        });
                        break;
                    }
                }
            }
        });

        txtUsername.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                txtPassword.setText(generatePassword(8));
            }
        });
    }

    private String generatePassword(int length) {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for (int i = 4; i < length; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return String.valueOf(password);
    }

    private void loadAllCourses() {
        cmbCourseId.getItems().clear();
        try {
            cmbCourseId.setItems(FXCollections.observableArrayList(courseBO.getAllCourses()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAllLectures() {
        tblAdminLecturer.getItems().clear();
        List<LectureTM> allLecturers = null;
        try {
            allLecturers = lecturerBO.getAllLecturers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObservableList<LectureTM>lecturers = FXCollections.observableArrayList(allLecturers);
        tblAdminLecturer.setItems(lecturers);
    }

    private void disableFields() {
        txtId.setDisable(true);
        cmbCourseId.setDisable(true);
        txtName.setDisable(true);
        txtAddress.setDisable(true);
        txtTel.setDisable(true);
        txtEmail.setDisable(true);
        txtUsername.setDisable(true);
        txtPassword.setDisable(true);
        txtNIC.setDisable(true);
        txtCourseName.setDisable(true);
    }

    public void btnDashboard_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/AdminDashboard.fxml"));
        Scene mainScene =  new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }

    public void btnCourses_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/AdminCoursesForm.fxml"));
        Scene mainScene =  new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }

    public void btnModules_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/AdminModuleForm.fxml"));
        Scene mainScene =  new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }

    public void btnAccount_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/AdminAccountForm.fxml"));
        Scene mainScene =  new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }

    public void btnStudent_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/StudentMainForm.fxml"));
        Scene mainScene =  new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }

    public void btnLecturers_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/LecturerAccountForm.fxml"));
        Scene mainScene =  new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {
        clearFields();
        enableFields();
        cmbCourseId.getSelectionModel().clearSelection();
        tblAdminLecturer.getSelectionModel().clearSelection();

        // Generate a new id
        try {
            txtId.setText(lecturerBO.getNewLecturerId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableFields() {
        txtId.setDisable(false);
        txtName.setDisable(false);
        txtAddress.setDisable(false);
        cmbCourseId.setDisable(false);
        txtNIC.setDisable(false);
        txtEmail.setDisable(false);
        txtTel.setDisable(false);
        txtUsername.setDisable(false);
        txtPassword.setDisable(false);
        txtCourseName.setDisable(false);
        btnSave.setDisable(false);
    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtNIC.clear();
        txtEmail.clear();
        txtTel.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtCourseName.clear();

    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this lecturer?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {
            LectureTM selectedItem = tblAdminLecturer.getSelectionModel().getSelectedItem();

            boolean result = false;
            try {
                result = lecturerBO.deleteLecturer(selectedItem.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!result){
                new Alert(Alert.AlertType.ERROR, "Failed to delete the lecturer", ButtonType.OK).show();
            }else{
                tblAdminLecturer.getItems().remove(selectedItem);
                tblAdminLecturer.getSelectionModel().clearSelection();
            }
        }
    }

    public void btnSave_OnAction(ActionEvent event) {
        String courseId = String.valueOf(cmbCourseId.getSelectionModel().getSelectedItem());
        String name = txtName.getText();
        String address = txtAddress.getText();
        String contact = txtTel.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String nic = txtNIC.getText();
        String email = txtEmail.getText();

        //validation
        if(courseId.trim().length()==0 ||
                name.trim().length()==0 || address.trim().length()==0 ||
                contact.trim().length()==0|| username.trim().length()==0 ||
                password.trim().length()==0 || nic.trim().length()==0 || email.trim().length()==0){
            new Alert(Alert.AlertType.ERROR,"Can not be empty values", ButtonType.OK).show();
            return;
        }

        if(btnSave.getText().equals("Save")){
            try {
                lecturerBO.saveLecturer(txtId.getText(),
                        String.valueOf(cmbCourseId.getSelectionModel().getSelectedItem()),
                        txtName.getText(),txtAddress.getText(),
                        txtTel.getText(),txtUsername.getText(),
                        txtPassword.getText(),txtNIC.getText(),txtEmail.getText());
                new Alert(Alert.AlertType.INFORMATION,"Lecturer saved successfully",ButtonType.OK).showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
            btnAdd_OnAction(event);
        }else{
            LectureTM selectedLecturer = tblAdminLecturer.getSelectionModel().getSelectedItem();
            boolean result = false;
            try {
                result = lecturerBO.updateLecturer(String.valueOf(cmbCourseId.getSelectionModel().getSelectedItem()),
                        txtName.getText(),txtAddress.getText(),txtTel.getText(),txtUsername.getText(),
                        txtPassword.getText(),txtNIC.getText(),txtEmail.getText(),selectedLecturer.getId());

                new Alert(Alert.AlertType.INFORMATION,"Lecturer updated successfully",ButtonType.OK).showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!result){
                new Alert(Alert.AlertType.ERROR, "Failed to update the Lecturer form", ButtonType.OK).show();
            }
            tblAdminLecturer.refresh();
            btnAdd_OnAction(event);
        }
        loadAllLectures();

    }
}
