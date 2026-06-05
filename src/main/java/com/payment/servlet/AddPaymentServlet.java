package com.payment.servlet;

import com.payment.dao.PaymentDAO;
import com.payment.model.Payment;
import com.toystore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/add-payment")
public class AddPaymentServlet extends HttpServlet {
    private PaymentDAO paymentDAO;

    @Override
    public void init() {
        paymentDAO = new PaymentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect="
                    + URLEncoder.encode("/add-payment", StandardCharsets.UTF_8));
            return;
        }

        request.setAttribute("lastOrderId", session.getAttribute("lastOrderId"));
        request.setAttribute("lastOrderTotal", session.getAttribute("lastOrderTotal"));

        User user = (User) session.getAttribute("user");
        if (user != null) {
            request.setAttribute("cardHolderName", user.getFullName());
        }

        request.getRequestDispatcher("/add-payment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Payment payment = new Payment();
        payment.setType(request.getParameter("type"));
        payment.setCardNumber(request.getParameter("cardNumber"));
        payment.setCardHolderName(request.getParameter("cardHolderName"));
        payment.setExpiryDate(request.getParameter("expiryDate"));

        paymentDAO.addPayment(payment);
        session.removeAttribute("lastOrderId");
        session.removeAttribute("lastOrderTotal");
        response.sendRedirect(request.getContextPath() + "/payment-history?paid=true");
    }
}
