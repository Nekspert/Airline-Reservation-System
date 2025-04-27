package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.service.TariffService;
import com.myairline.airline_reservation.model.tariff.Tariff;
import com.myairline.airline_reservation.model.tariff.TariffType;
import com.myairline.airline_reservation.model.tariff.TariffFactory;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class TariffController {
    @FXML
    private ComboBox<TariffType> typeCombo;
    @FXML
    private TextField nameField;
    @FXML
    private TextField basePriceField;
    @FXML
    private TextArea descriptionField;

    @FXML
    private ListView<Tariff> tariffList;

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @FXML
    private void initialize() {
        // 1) Наполняем выпадающий список типами тарифов
        typeCombo.getItems().setAll(TariffType.values());

        // 2) Выбираем по умолчанию ECONOMY
        typeCombo.getSelectionModel().select(TariffType.ECONOMY);

        // 3) Загружаем все существующие тарифы в список
        tariffList.getItems().setAll(tariffService.getAll());
    }

    @FXML
    public void onDeleteTariff() {
        // Получаем выбранный тариф
        Tariff selected = tariffList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Выберите тариф для удаления");
            alert.showAndWait();
            return;
        }

        // Удаляем из базы данных
        tariffService.removeTariff(selected);

        // Удаляем из списка UI
        tariffList.getItems().remove(selected);
    }

    @FXML
    public void onAddTariff() {
        TariffType type = typeCombo.getValue();
        String name = nameField.getText().trim();
        BigDecimal basePrice = new BigDecimal(basePriceField.getText().trim());
        String description = descriptionField.getText().trim();

        // Создаём объект нужного подкласса через фабрику
        Tariff t = TariffFactory.create(type);
        t.setName(name);
        t.setBasePrice(basePrice);
        t.setDescription(description);

        // Сохраняем и добавляем в список
        Tariff saved = tariffService.addTariff(t);
        tariffList.getItems().add(saved);

        // Очищаем форму
        typeCombo.getSelectionModel().clearSelection();
        nameField.clear();
        basePriceField.clear();
        descriptionField.clear();
    }
}
