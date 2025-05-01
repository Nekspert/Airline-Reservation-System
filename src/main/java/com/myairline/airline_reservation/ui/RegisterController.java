package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.service.UserService;
import com.myairline.airline_reservation.model.user.Role;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @FXML
    private void onRegister() {
        String username = usernameField.getText().trim();
        String pass     = passwordField.getText();
        String conf     = confirmPasswordField.getText();

        if (username.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Все поля обязательны").showAndWait();
            return;
        }
        if (!pass.equals(conf)) {
            new Alert(Alert.AlertType.WARNING, "Пароли не совпадают").showAndWait();
            return;
        }

        try {
            userService.register(username, pass, Role.PASSENGER);
            new Alert(Alert.AlertType.INFORMATION, "Пользователь зарегистрирован").showAndWait();
            closeWindow();
        } catch (IllegalArgumentException ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
