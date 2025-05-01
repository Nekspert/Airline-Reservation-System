package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.model.user.User;
import com.myairline.airline_reservation.service.UserService;
import com.myairline.airline_reservation.app.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserService userService;
    private final AppSession session;

    public LoginController(UserService userService, AppSession session) {
        this.userService = userService;
        this.session     = session;
    }

    @FXML
    private void onLogin() {
        User user = userService.authenticate(usernameField.getText().trim(), passwordField.getText());
        if (user != null) {
            session.setCurrentUser(user);
            try {
                MainApp.getInstance().showMainWindow();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Не удалось открыть главное окно").showAndWait();
            }
        }
    }


    @FXML
    private void onRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/register.fxml"));
            loader.setControllerFactory(clazz -> {
                if (clazz == RegisterController.class)
                    return new RegisterController(userService);
                throw new IllegalStateException("Unexpected controller: " + clazz);
            });

            Parent root = loader.load();
            Stage regStage = new Stage();
            regStage.setTitle("Регистрация");
            regStage.initOwner(usernameField.getScene().getWindow());
            regStage.setScene(new Scene(root));
            regStage.show();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Не удалось открыть окно регистрации").showAndWait();
        }
    }
}
