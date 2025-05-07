package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.model.tariff.Tariff;
import com.myairline.airline_reservation.service.TariffService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class PassengerTariffController {
    @FXML private TableView<Tariff> tariffTable;
    @FXML private TableColumn<Tariff, Number> colIndex;
    @FXML private TableColumn<Tariff, String> colName;
    @FXML private TableColumn<Tariff, String> colPrice;
    @FXML private TableColumn<Tariff, String> colDesc;

    private final TariffService tariffService;

    public PassengerTariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @FXML
    public void initialize() {
        // Нумерация строк
        colIndex.setCellValueFactory(cd ->
                new ReadOnlyObjectWrapper<>(
                        tariffTable.getItems().indexOf(cd.getValue()) + 1
                )
        );
        colName.setCellValueFactory(cd ->
                new ReadOnlyObjectWrapper<>(cd.getValue().getName())
        );
        colPrice.setCellValueFactory(cd ->
                new ReadOnlyObjectWrapper<>(cd.getValue().getBasePrice().toString() + " руб.")
        );
        colDesc.setCellValueFactory(cd ->
                new ReadOnlyObjectWrapper<>(cd.getValue().getDescription())
        );

        // Добавляем данные и авто-разделители строк
        tariffTable.getItems().setAll(tariffService.getAll());
        tariffTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tariffTable.getStyleClass().add("striped-table");
    }
}
