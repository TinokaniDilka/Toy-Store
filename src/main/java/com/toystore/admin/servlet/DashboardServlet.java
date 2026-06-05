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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
            return;
        }

        try {
            AdminService adminService = new AdminService();
            List<Admin> allAdmins = adminService.getAllAdmins();

            int superAdmins = 0;
            int activeAdmins = 0;
            for (Admin a : allAdmins) {
                if ("SUPER_ADMIN".equals(a.getRole())) {
                    superAdmins++;
                }
                if (a.isActive()) {
                    activeAdmins++;
                }
            }

            List<Admin> recentAdmins = new ArrayList<>();
            int limit = Math.min(5, allAdmins.size());
            for (int i = 0; i < limit; i++) {
                recentAdmins.add(allAdmins.get(i));
            }

            request.setAttribute("totalAdmins", allAdmins.size());
            request.setAttribute("superAdmins", superAdmins);
            request.setAttribute("activeAdmins", activeAdmins);
            request.setAttribute("recentAdmins", recentAdmins);
        } catch (IOException e) {
            request.setAttribute("error", "Failed to load dashboard data");
        }

        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}
