package com.toystore.admin.servlet;

import com.toystore.admin.model.Admin;
import com.toystore.admin.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/register")
public class AdminRegisterServlet extends HttpServlet {
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        Admin loggedIn = (Admin) session.getAttribute("admin");
        if (!"SUPER_ADMIN".equals(loggedIn.getRole())) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        request.getRequestDispatcher("/admin/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        Admin loggedIn = (Admin) session.getAttribute("admin");
        if (!"SUPER_ADMIN".equals(loggedIn.getRole())) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        String fullName = request.getParameter("fullName");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role");

        try {
            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Passwords do not match");
                request.getRequestDispatcher("/admin/register.jsp").forward(request, response);
                return;
            }

            if (adminService.isUsernameTaken(username)) {
                request.setAttribute("error", "Username already exists");
                request.getRequestDispatcher("/admin/register.jsp").forward(request, response);
                return;
            }

            Admin admin = new Admin(fullName, username, email, password, role);
            admin.setActive(true);
            adminService.createAdmin(admin);

            response.sendRedirect(request.getContextPath() + "/admin/list");
        } catch (Exception e) {
            request.setAttribute("error", "An error occurred during registration");
            request.getRequestDispatcher("/admin/register.jsp").forward(request, response);
        }
    }
}