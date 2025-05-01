package com.myairline.airline_reservation.service;

import com.myairline.airline_reservation.dao.UserDAO;
import com.myairline.airline_reservation.model.user.Role;
import com.myairline.airline_reservation.model.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserService {
    private final UserDAO dao;

    public UserService(UserDAO dao) {
        this.dao = dao;
    }

    /**
     * Регистрация нового пользователя с ролью.
     * Хешируем пароль с BCrypt (лучше, чем plain MD5).
     */
    public User register(String username, String plainPassword, Role role) {
        // Проверим, что такого username ещё нет
        if (dao.findByUsername(username) != null) throw new IllegalArgumentException("Username уже занят");
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        User user = new User(username, hashed, role);
        return dao.save(user);
    }

    /**
     * Аутентификация: проверяем, что username существует
     * и пароль подходит под хеш.
     */
    public User authenticate(String username, String plainPassword) {
        User u = dao.findByUsername(username);
        if (u == null || !BCrypt.checkpw(plainPassword, u.getPasswordHash())) {
            return null;
        }
        return u;
    }


    public User addUser(User user) {
        return dao.save(user);
    }

    public User findById(Long id) {
        return dao.findById(id);
    }

    public User findByUsername(String username) {
        return dao.findByUsername(username);
    }

    /**
     * Удаление пользователя (ADMINS only)
     */
    public void deleteUser(Long id) {
        dao.deleteById(id);
    }

    /**
     * Список всех пользователей (только ADMIN)
     */
    public List<User> findAll() {
        return dao.findAll();
    }

    /**
     * Сменить роль пользователя
     */
    public User changeRole(Long id, Role role) {
        User u = dao.findById(id);
        if (u == null) throw new IllegalArgumentException("Нет такого пользователя");
        u.setRole(role);
        return dao.save(u);
    }
}
