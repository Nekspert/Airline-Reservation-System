package com.myairline.airline_reservation.ui;

import com.myairline.airline_reservation.model.Route;
import com.myairline.airline_reservation.service.RouteService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.List;
import java.util.stream.Collectors;

public class PassengerRouteController {
    @FXML
    private ListView<Route> routeList;
    @FXML
    private WebView mapView;

    private final RouteService routeService;

    public PassengerRouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @FXML
    public void initialize() {
        // 1) Заполнить список маршрутов
        routeList.getItems().setAll(routeService.getAll());

        // 2) Построить HTML для карты
        String html = buildLeafletHtml(routeService.getAll());

        // 3) Загрузить в WebView
        WebEngine engine = mapView.getEngine();
        engine.loadContent(html);
    }

    /**
     * Генерирует простую HTML-страницу с Leaflet.js,
     * на которой помечены все точки отправления/прибытия.
     */
    private String buildLeafletHtml(List<Route> routes) {
        // Преобразуем в JS-массив меток: [ [lat, lon, "Откуда→Куда"], … ]
        // Для простоты возьмём нулевую точку для всех (можете заменить на реальные координаты)
        String markers = routes.stream()
                .map(r -> String.format("[51.505, -0.09, \"%s → %s\"]",
                        r.getOrigin(), r.getDestination()))
                .collect(Collectors.joining(",\n    "));

        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8" />
                  <title>Карта маршрутов</title>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <link 
                    rel="stylesheet" 
                    href="https://unpkg.com/leaflet/dist/leaflet.css" 
                  />
                  <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
                  <style>
                    /* make every ancestor fill its container */
                    html, body, #map {
                      width: 100%%;
                      height: 100%%;
                      margin: 0;
                      padding: 0;
                    }
                  </style>
                </head>
                <body>
                  <div id="map"></div>
                  <script>
                    var map = L.map('map').setView([51.505, -0.09], 6);
                    L.tileLayer(
                      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                      { maxZoom: 19 }
                    ).addTo(map);
                
                    var markers = [
                      %s
                    ];
                    markers.forEach(function(m) {
                      L.marker([m[0], m[1]])
                       .bindPopup(m[2])
                       .addTo(map);
                    });
                  </script>
                </body>
                </html>
                """.formatted(markers);
    }
}
