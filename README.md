# Airline-Reservation-System
Курсовая работа по JAVA OOP: Приложение для учёта проданных билетов авиакомпании

Планируемые сущности и связи:
classDiagram
    Flight "1" o-- "1" Airplane
    Flight "1" o-- "2..*" CrewMember
    Flight "1" o-- "2..*" Seat
    Flight "1" o-- "1" Route
    Booking "1" -- "1" Flight
    Booking "1" -- "1" Passenger
    Booking "1" -- "1" Ticket
    Ticket "1" -- "1" Tariff
    Payment "1" -- "1" Booking
    User <|-- Agent
    User <|-- Admin
•	Flight: привязка к Airplane, Route, списки Seat и CrewMember.
•	Route: «откуда → куда» с длительностью и промежуточными точками.
•	Booking: оформленная бронь, связывает Passenger, Flight, Ticket.
•	Tariff: базовый класс для тарифов; потомки TariffFlexible, TariffBusiness и т. д.
•	Payment: связывает бронь и итоговый платёж.
•	User: роли (Agent, Admin) для контроля доступа.


Слои приложения
┌────────────────────┐
│      UI Layer      │   ← JavaFX
└────────────────────┘
          ↓
┌────────────────────┐
│ Service Layer      │   ← бизнес-логика, паттерны (Strategy, Factory)
└────────────────────┘
          ↓
┌────────────────────┐
│ Repository/DAO     │   ← JPA/Hibernate-репозитории
└────────────────────┘
          ↓
┌────────────────────┐
│   Database Layer   │   ← PostgreSQL.
└────────────────────┘


Настройка среды и пустого проекта:
1.	IDE и инструменты
o	Установите IntelliJ IDEA Community или Eclipse.
o	JDK 11+ (рекомендуется JDK 17).

2.	Сборщик и зависимости
o	Создайте Maven.
o	Добавьте в pom.xml/build.gradle:
o	javafx-controls, javafx-fxml
o	hibernate-core, hibernate-entitymanager
o	JDBC-драйвер для вашей СУБД.

3.	Структура пакетов
com.myairline.airline_reservation
├── app          // точка входа в приложение (MainApp)
├── ui           // контроллеры JavaFX + FXML
├── model        // JPA-сущности (Entity)
├── dao          // репозитории / DAO (если без Spring Data — тут реализации)
├── repository   // интерфейсы Spring Data JPA (если с Spring)
├── service      // бизнес-логика, сервисы, паттерны
└── utils         // утилиты (HibernateUtil, AppConfig, константы)

Общая схема работы:
JavaFX UI (FXML) → MainController → FlightService → FlightDAO → JPA/Hibernate → База данных

