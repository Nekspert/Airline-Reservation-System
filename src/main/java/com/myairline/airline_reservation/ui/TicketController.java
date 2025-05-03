package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.app.MainApp;
import com.myairline.airline_reservation.init.AppSession;
import com.myairline.airline_reservation.model.Ticket;
import com.myairline.airline_reservation.model.user.User;
import com.myairline.airline_reservation.service.TicketService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class TicketController {
    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, Long> colId;
    @FXML
    private TableColumn<Ticket, String> colFlight;
    @FXML
    private TableColumn<Ticket, String> colTariff;
    @FXML
    private TableColumn<Ticket, String> colPrice;

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @FXML
    public void initialize() {
        // Настраиваем колонки
        colId.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getId()));
        colFlight.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getFlight().toString()));
        colTariff.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getTariff().getName()));
        colPrice.setCellValueFactory(cd ->
                new ReadOnlyObjectWrapper<>(cd.getValue().getPrice().toString())
        );
        refreshTable();
    }

    /**
     * Кнопка «Забронировать билет» — переходим в раздел бронирования
     */
    @FXML
    public void onGoToBooking() {
        // В MainController переключаемся на вкладку «Бронирования»
        MainApp.getInstance().getMainController().selectSection("Мои бронирования");
    }


    /** Перезаполнить таблицу */
    private void refreshTable() {
        User u = AppSession.get().getCurrentUser();
        List<Ticket> list = ticketService.getFor(u);
        ticketTable.getItems().setAll(list);
    }
}
