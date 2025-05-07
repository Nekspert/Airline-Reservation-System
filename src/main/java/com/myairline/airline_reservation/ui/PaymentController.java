package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.model.Booking;
import com.myairline.airline_reservation.model.Payment;
import com.myairline.airline_reservation.model.user.User;
import com.myairline.airline_reservation.service.PaymentService;
import com.myairline.airline_reservation.service.UserService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class PaymentController {
    @FXML
    private VBox adminFilterBox;
    @FXML
    private ComboBox<User> userCombo;

    @FXML
    private TableView<Payment> paymentTable;
    @FXML
    private TableColumn<Payment, Long> colId;
    @FXML
    private TableColumn<Payment, Payment.Type> colType;
    @FXML
    private TableColumn<Payment, Number> colAmount;
    @FXML
    private TableColumn<Payment, String> colBooking;
    @FXML
    private TableColumn<Payment, String> colDate;

    @FXML
    private Label balanceLabel;
    @FXML
    private TextField amountField;

    private final PaymentService paymentService;
    private final UserService userService;
    private User current;

    public PaymentController(PaymentService ps, UserService us) {
        this.paymentService = ps;
        this.userService = us;
    }

    @FXML
    public void initialize() {
        current = AppSession.get().getCurrentUser();
        boolean isAdmin = current.isAdmin();

        adminFilterBox.setVisible(isAdmin);
        if (isAdmin) {
            userCombo.getItems().setAll(userService.findAll());
            userCombo.getSelectionModel().selectedItemProperty()
                    .addListener((obs, oldU, newU) -> refreshAll());
            userCombo.getSelectionModel().selectFirst();
        }

        // --- настраиваем колонки истории ---
        colAmount.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getAmount()));
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colType.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getType()));
        colBooking.setCellValueFactory(c -> {
            Booking b = c.getValue().getBooking();
            return new ReadOnlyObjectWrapper<>(b == null ? "-" : b.getId().toString());
        });
        colDate.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getAt().toString())
        );
        // оформить суммы цветом и знаком
        colAmount.setCellFactory(col -> new TableCell<Payment, Number>() {
            @Override
            protected void updateItem(Number amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                    getStyleClass().removeAll("debit-cell", "credit-cell");
                } else {
                    Payment p = getTableView().getItems().get(getIndex());
                    getStyleClass().removeAll("debit-cell", "credit-cell");
                    if (p.getType() == Payment.Type.PURCHASE) {
                        setText("-" + amount);
                        getStyleClass().add("debit-cell");
                    } else {
                        setText("+" + amount);
                        getStyleClass().add("credit-cell");
                    }
                }
            }
        });

        // первая загрузка
        refreshAll();
    }

    private void refreshAll() {
        User filter = current.isAdmin() ? userCombo.getValue() : current;
        // баланс всегда свой
        balanceLabel.setText(filter.getBalance().toString());

        // для админа — фильтруем по выбранному, иначе — свой
        paymentTable.getItems().setAll(paymentService.history(filter));
    }

    @FXML
    public void onRecharge() {
        try {
            User filter = current.isAdmin() ? userCombo.getValue() : current;
            BigDecimal amt = new BigDecimal(amountField.getText());
            if (amt.compareTo(BigDecimal.ZERO) < 1) {
                new Alert(Alert.AlertType.WARNING, "Некорректная сумма").showAndWait();
                return;
            }
            paymentService.recharge(filter, amt);
            amountField.clear();
            refreshAll();
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING, "Некорректная сумма").showAndWait();
        }
    }

    @FXML
    public void onCancel() {
        amountField.clear();
    }
}
