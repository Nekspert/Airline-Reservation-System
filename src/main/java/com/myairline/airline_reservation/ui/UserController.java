package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.model.user.Role;
import com.myairline.airline_reservation.model.user.User;
import com.myairline.airline_reservation.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserController {
    @FXML
    private ListView<User> userList;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<Role> roleCombo;

    private final UserService userService;
    private final User current;

    {
        current = AppSession.get().getCurrentUser();
    }

    public UserController(UserService userService) {
        this.userService = userService;
        if (!current.isAdmin()) throw new SecurityException();
    }

    @FXML
    public void initialize() {
        roleCombo.getItems().setAll(Role.values());
        roleCombo.getSelectionModel().select(Role.PASSENGER);
        refreshList();
    }

    private void refreshList() {
        userList.getItems().setAll(userService.findAll());
    }

    @FXML
    public void onAddUser() {
        String login = usernameField.getText(), pass = passwordField.getText();
        Role r = roleCombo.getValue();
        userService.register(login, pass, r);
        refreshList();
    }

    @FXML
    public void onDeleteUser() {
        User sel = userList.getSelectionModel().getSelectedItem();
        if (sel != null) {
            userService.deleteUser(sel.getId());
            refreshList();
        }
    }
}