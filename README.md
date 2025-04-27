# ✈️ Airline Reservation System

A Java OOP coursework project for managing airline tickets, bookings, flights, and user roles. Built with JavaFX, Hibernate, and PostgreSQL.

![Java](https://img.shields.io/badge/Java-21-red)
![Hibernate](https://img.shields.io/badge/Hibernate-5.6.15.Final-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-10-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-17-orange)

## 🌟 Features
- **Flight Management**: Create flights, routes
- **Ticket Booking**: Flexible booking system with multiple tariff options (Business/Flexible)
- **Role-Based Access**: Admin and Agent roles with different privileges
- **Payment Integration**: Track payments associated with bookings
- **Advanced Search**: Find flights by route, or availability

## 🛠️ Technologies
- **Frontend**: JavaFX + FXML
- **Backend**: Java 21
- **ORM**: Hibernate 5.6
- **Database**: PostgreSQL 10
- **Build Tool**: Maven
- **Architecture**: Layered (UI → Service → DAO → DB)

## 🗄️ Database Schema
```plaintext
[Flight] 1→ [Route]
               ↑1     
[Booking] ↔1─ [Ticket] ↔1─ [Tariff]
  ↑1          ↑1
[Payment]  [Passenger]
```
## 📁 Project Structure

```plaintext
com.myairline.airline_reservation
├── app/          # Main application class
├── ui/           # JavaFX controllers + FXML
├── model/        # JPA Entities
│   ├── Flight.java
│   ├── Ticket.java
│   └── ... 
├── dao/          # Data Access Objects
├── service/      # Business logic layer
└── utils/    # JPAUtil
