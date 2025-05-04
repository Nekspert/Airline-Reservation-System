package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.model.user.Role;
import com.myairline.airline_reservation.model.user.User;
import com.myairline.airline_reservation.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UserController {
    @FXML
    private ListView<User> userList;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<Role> roleCombo;
    @FXML
    private ComboBox<Role> roleSelectCombo;
    @FXML
    private Button changeRoleBtn;

    private final UserService userService;
    private final User current;

    {
        current = AppSession.get().getCurrentUser();
    }

    public UserController(UserService userService) {
        this.userService = userService;
        if (!current.isAdmin()) throw new SecurityException("Нет прав");
    }

    @FXML
    public void initialize() {
        roleCombo.getItems().setAll(Role.values());
        roleCombo.getSelectionModel().select(Role.PASSENGER);
        roleSelectCombo.getItems().setAll(Role.values());
        roleSelectCombo.getSelectionModel().select(Role.PASSENGER);
        changeRoleBtn.setDisable(true);
        userList.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> {
            if (newUser != null) {
                boolean isSelf = newUser.getId().equals(current.getId());
                roleSelectCombo.setValue(newUser.getRole());
                changeRoleBtn.setDisable(isSelf);
                roleSelectCombo.setDisable(isSelf);
            } else {
                changeRoleBtn.setDisable(true);
                roleSelectCombo.setDisable(true);
            }
        });
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
        if (sel != null && !sel.getId().equals(current.getId())) {
            userService.deleteUser(sel.getId());
            refreshList();
        }
    }

    @FXML
    public void onChangeRole() {
        User sel = userList.getSelectionModel().getSelectedItem();
        Role newRole = roleSelectCombo.getValue();
        if (sel != null && newRole != null && !sel.getId().equals(current.getId())) {
            userService.changeRole(sel.getId(), newRole);
            refreshList();
        }
    }
}