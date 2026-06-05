package com.toystore.admin.service;

import com.toystore.admin.model.Admin;
import com.toystore.admin.util.AdminFileUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminService {

    public List<Admin> getAllAdmins() throws IOException {
        return AdminFileUtil.getAllAdmins();
    }

    public List<Admin> getActiveAdmins() throws IOException {
        return AdminFileUtil.getAllAdmins().stream()
                .filter(Admin::isActive)
                .collect(Collectors.toList());
    }

    public Admin getAdminByUsername(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return AdminFileUtil.getAdminByUsername(username);
    }

    public void createAdmin(Admin admin) throws IOException {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        validateAdminFields(admin);
        if (isUsernameTaken(admin.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        admin.setActive(true);
        AdminFileUtil.saveAdmin(admin);
    }

    public void updateAdmin(Admin admin) throws IOException {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        validateAdminFields(admin);
        Admin existingAdmin = getAdminByUsername(admin.getUsername());
        if (existingAdmin == null) {
            throw new IllegalArgumentException("Admin not found");
        }
        AdminFileUtil.saveAdmin(admin);
    }

    public boolean deleteAdmin(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        Admin adminToDelete = getAdminByUsername(username);
        if (adminToDelete != null && "SUPER_ADMIN".equals(adminToDelete.getRole())) {
            long superAdminCount = AdminFileUtil.getAllAdmins().stream()
                    .filter(a -> "SUPER_ADMIN".equals(a.getRole()))
                    .count();
            if (superAdminCount <= 1) {
                throw new IllegalStateException("Cannot delete the last super admin");
            }
        }
        return AdminFileUtil.deleteAdmin(username);
    }

    public void toggleAdminStatus(String username) throws IOException {
        Admin admin = getAdminByUsername(username);
        if (admin != null) {
            if ("SUPER_ADMIN".equals(admin.getRole()) && admin.isActive()) {
                long activeSuperAdminCount = AdminFileUtil.getAllAdmins().stream()
                        .filter(a -> "SUPER_ADMIN".equals(a.getRole()) && a.isActive())
                        .count();
                if (activeSuperAdminCount <= 1) {
                    throw new IllegalStateException("Cannot deactivate the last active super admin");
                }
            }
            admin.setActive(!admin.isActive());
            updateAdmin(admin);
        }
    }

    public boolean validateCredentials(String username, String password) throws IOException {
        if (username == null || password == null) {
            return false;
        }
        Admin admin = getAdminByUsername(username);
        return admin != null
            && admin.getPassword().equals(password)
            && admin.isActive();
    }

    public boolean isUsernameTaken(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return AdminFileUtil.getAllAdmins().stream()
                .anyMatch(admin -> admin.getUsername().equalsIgnoreCase(username));
    }

    public void changePassword(String username, String currentPassword, String newPassword) throws IOException {
        if (username == null || currentPassword == null || newPassword == null) {
            throw new IllegalArgumentException("All parameters are required");
        }
        Admin admin = getAdminByUsername(username);
        if (admin == null) {
            throw new IllegalArgumentException("Admin not found");
        }
        if (!admin.getPassword().equals(currentPassword)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        admin.setPassword(newPassword);
        updateAdmin(admin);
    }

    public Map<String, Long> getAdminStats() throws IOException {
        List<Admin> admins = AdminFileUtil.getAllAdmins();
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalAdmins", (long) admins.size());
        stats.put("activeAdmins", admins.stream().filter(Admin::isActive).count());
        stats.put("superAdmins", admins.stream().filter(a -> "SUPER_ADMIN".equals(a.getRole())).count());
        stats.put("regularAdmins", admins.stream().filter(a -> "ADMIN".equals(a.getRole())).count());
        return stats;
    }

    private void validateAdminFields(Admin admin) {
        if (admin.getFullName() == null || admin.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (admin.getUsername() == null || admin.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (admin.getRole() == null || admin.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }
        if (!admin.getRole().equals("SUPER_ADMIN") && !admin.getRole().equals("ADMIN")) {
            throw new IllegalArgumentException("Invalid role. Must be either SUPER_ADMIN or ADMIN");
        }
    }
}
