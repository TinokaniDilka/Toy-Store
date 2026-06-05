package com.toystore.admin.servlet;

import com.toystore.admin.model.Admin;
import com.toystore.admin.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/resetpassword")
public class AdminResetPasswordServlet extends HttpServlet {
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = new AdminService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String newPassword = request.getParameter("password");

        if (email == null || email.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            request.setAttribute("error", "Please fill in all fields");
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            return;
        }

        try {
            Admin admin = findAdminByEmail(email);
            if (admin == null) {
                request.setAttribute("error", "Email address not found in our system");
                request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
                return;
            }

            admin.setPassword(newPassword);
            adminService.updateAdmin(admin);
            request.setAttribute("success", "Password has been reset successfully. Please login with your new password.");
        } catch (Exception e) {
            request.setAttribute("error", "Error resetting password: " + e.getMessage());
        }

        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/admin/forgetpassword.jsp").forward(request, response);
    }

    private Admin findAdminByEmail(String email) throws IOException {
        List<Admin> admins = adminService.getAllAdmins();
        for (Admin admin : admins) {
            if (email.equalsIgnoreCase(admin.getEmail())) {
                return admin;
            }
        }
        return null;
    }
}
