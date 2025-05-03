package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.model.Payment;
import com.myairline.airline_reservation.model.user.User;
import com.myairline.airline_reservation.service.PaymentService;
import com.myairline.airline_reservation.service.UserService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.List;

public class PaymentController {
    @FXML
    private HBox adminFilterBox;
    @FXML
    private ComboBox<User> userCombo;
    @FXML
    private Button payAllBtn;
    @FXML
    private TableView<Payment> paymentTable;
    @FXML
    private TableColumn<Payment, Long> colPayId;
    @FXML
    private TableColumn<Payment, String> colPayBook;
    @FXML
    private TableColumn<Payment, Number> colPayAmount;
    @FXML
    private TableColumn<Payment, Payment.PaymentStatus> colPayStatus;
    @FXML
    private TableColumn<Payment, String> colPayDate;
    @FXML
    private TableColumn<Payment, Void> colPayAction;

    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(PaymentService ps, UserService us) {
        this.paymentService = ps;
        this.userService = us;
    }

    @FXML
    public void initialize() {
        User current = AppSession.get().getCurrentUser();
        boolean isAdmin = current.isAdmin();

        adminFilterBox.setVisible(isAdmin);
        payAllBtn.setVisible(!isAdmin);

        if (isAdmin) {
            userCombo.getItems().setAll(userService.findAll());
            userCombo.getSelectionModel().selectedItemProperty()
                    .addListener((o, a, b) -> refresh());
            userCombo.getSelectionModel().selectFirst();
        } else {
            payAllBtn.setOnAction(e -> {
                // оплачиваем все PENDING
                paymentService.getFor(current, current)
                        .stream()
                        .filter(p -> p.getStatus() == Payment.PaymentStatus.PENDING)
                        .forEach(paymentService::confirmPayment);
                refresh();
            });
        }

        colPayId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colPayBook.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getBooking().getId().toString()));
        colPayAmount.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getAmount()));
        colPayStatus.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStatus()));
        colPayDate.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getPaidAt() == null ? "-" : c.getValue().getPaidAt().toString()
        ));
        colPayAction.setCellFactory(makeActionCellFactory());

        refresh();
    }

    private Callback<TableColumn<Payment, Void>, TableCell<Payment, Void>> makeActionCellFactory() {
        return col -> new TableCell<>() {
            private final Button btn = new Button("Удалить");

            {
                btn.setOnAction(e -> {
                    Payment p = getTableView().getItems().get(getIndex());
                    paymentService.deletePayment(p);
                    refresh();
                });
            }

            @Override
            protected void updateItem(Void it, boolean emp) {
                super.updateItem(it, emp);
                setGraphic(emp ? null : btn);
            }
        };
    }

    private void refresh() {
        User current = AppSession.get().getCurrentUser();
        List<Payment> data;
        if (current.isAdmin()) {
            User sel = userCombo.getValue();
            data = paymentService.getFor(sel, current);
        } else {
            data = paymentService.getFor(current, current);
        }
        paymentTable.getItems().setAll(data);
    }

}
