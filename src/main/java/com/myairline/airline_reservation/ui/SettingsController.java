package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.app.MainApp;
import com.myairline.airline_reservation.init.AppSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Popup;

import java.io.IOException;

public class SettingsController {
    @FXML
    private Button logoutBtn;
    @FXML
    private Button cancelBtn;
    private Popup popup;

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    @FXML
    public void initialize() {
        logoutBtn.setOnAction(e -> {
            // прячем панель
            if (popup != null) popup.hide();
            // сброс сессии
            AppSession.get().setCurrentUser(null);
            try {
                MainApp.getInstance().showLoginWindow();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Не удалось вернуться на экран логина").showAndWait();
            }
        });
        cancelBtn.setOnAction(e -> {
            if (popup != null) popup.hide();
        });
    }
}