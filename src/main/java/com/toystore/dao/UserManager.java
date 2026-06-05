package com.toystore.dao;

import com.toystore.model.User;
import com.toystore.util.DataPaths;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class UserManager {
    private static UserManager instance;
    private List<User> users;

    private UserManager() {
        users = new LinkedList<>();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void reload() {
        loadUsers();
    }

    private String usersFile() {
        return DataPaths.get("users.txt");
    }

    private void loadUsers() {
        File file = new File(usersFile());
        users.clear();
        if (!file.exists()) {
            System.err.println("Users file not found at: " + usersFile());
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    User user = new User(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4],
                        parts[5]
                    );
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try {
            File file = new File(usersFile());
            if (!file.exists()) {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                file.createNewFile();
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (User user : users) {
                    writer.println(user.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean registerUser(User user) {
        if (findUserByEmail(user.getEmail()) != null) {
            return false;
        }
        users.add(user);
        saveUsers();
        return true;
    }

    public User findUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public boolean updateUser(User updatedUser) {
        User existingUser = findUserByEmail(updatedUser.getEmail());
        if (existingUser == null) {
            return false;
        }
        users.remove(existingUser);
        users.add(updatedUser);
        saveUsers();
        return true;
    }

    public boolean deleteUser(String username) {
        User user = findUserByEmail(username);
        if (user == null) {
            return false;
        }
        users.remove(user);
        saveUsers();
        return true;
    }

    public List<User> getAllUsers() {
        return new LinkedList<>(users);
    }

    public boolean authenticateUser(String email, String password) {
        User user = findUserByEmail(email);
        return user != null && user.getPassword().equals(password);
    }
}
