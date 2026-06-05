package com.toystore.servlet;

import com.toystore.dao.UserManager;
import com.toystore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserManager userManager;

    @Override
    public void init() throws ServletException {
        userManager = UserManager.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String redirect = request.getParameter("redirect");

        if (email == null || password == null ||
            email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Email and password are required");
            request.setAttribute("redirect", redirect);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if (userManager.authenticateUser(email, password)) {
            User user = userManager.findUserByEmail(email);
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getEmail());
            session.setAttribute("userType", "REGULAR");
            response.sendRedirect(resolveRedirect(request, redirect));
        } else {
            request.setAttribute("error", "Invalid email or password");
            request.setAttribute("redirect", redirect);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    static String resolveRedirect(HttpServletRequest request, String redirect) {
        if (redirect != null && redirect.startsWith("/") && !redirect.startsWith("//")) {
            return request.getContextPath() + redirect;
        }
        return request.getContextPath() + "/list-toys";
    }
}
