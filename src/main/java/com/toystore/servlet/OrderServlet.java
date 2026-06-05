package com.toystore.servlet;

import com.toystore.model.CartLineItem;
import com.toystore.model.Order;
import com.toystore.model.OrderItem;
import com.toystore.model.User;
import com.toystore.util.DiscountCalculator;
import com.toystore.util.FileHandler;
import com.toy.dao.ToyDAO;
import com.toy.model.Toy;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/order/*")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action) {
            case "/place":
                placeOrder(request, response);
                break;
            case "/update":
                updateOrder(request, response);
                break;
            case "/cancel":
                cancelOrder(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/list-toys");
            return;
        }

        switch (action) {
            case "/checkout":
                showCheckout(request, response);
                break;
            case "/history":
                viewOrderHistory(request, response);
                break;
            case "/details":
                viewOrderDetails(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void showCheckout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") == null) {
            String target = "/order/checkout";
            String toyId = request.getParameter("toyId");
            if (toyId != null && !toyId.isEmpty()) {
                target += "?toyId=" + URLEncoder.encode(toyId, StandardCharsets.UTF_8);
                String qty = request.getParameter("qty");
                if (qty != null && !qty.isEmpty()) {
                    target += "&qty=" + URLEncoder.encode(qty, StandardCharsets.UTF_8);
                }
            }
            response.sendRedirect(request.getContextPath() + "/login?redirect="
                    + URLEncoder.encode(target, StandardCharsets.UTF_8));
            return;
        }

        String toyId = request.getParameter("toyId");
        if (toyId == null || toyId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/list-toys");
            return;
        }

        int qty = 1;
        try {
            if (request.getParameter("qty") != null) {
                qty = Integer.parseInt(request.getParameter("qty"));
            }
        } catch (NumberFormatException ignored) {
            qty = 1;
        }
        if (qty < 1) {
            qty = 1;
        }

        Toy toy = new ToyDAO().getToyByImageName(toyId);
        if (toy == null) {
            response.sendRedirect(request.getContextPath() + "/list-toys");
            return;
        }

        List<CartLineItem> cartItems = new ArrayList<>();
        cartItems.add(new CartLineItem(toy.getImageName(), toy.getName(), toy.getPrice(), qty));

        double subtotal = toy.getPrice() * qty;
        String userType = (String) session.getAttribute("userType");
        DiscountCalculator calculator = DiscountCalculator.getDiscountCalculator(userType);
        double discount = calculator.calculateDiscount(subtotal);
        double total = subtotal - discount;

        User user = (User) session.getAttribute("user");
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("discount", discount);
        request.setAttribute("total", total);
        if (user != null && user.getAddress() != null) {
            request.setAttribute("userAddress", user.getAddress());
        }

        request.getRequestDispatcher("/OrderPlacement.jsp").forward(request, response);
    }

    private void placeOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect="
                    + URLEncoder.encode("/order/checkout", StandardCharsets.UTF_8));
            return;
        }

        Order order = new Order();
        order.setOrderId("ORD" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setDeliveryAddress(request.getParameter("deliveryAddress"));
        order.setPaymentMethod(request.getParameter("paymentMethod"));

        String[] toyIds = request.getParameterValues("toyId");
        String[] quantities = request.getParameterValues("quantity");

        if (toyIds == null || quantities == null) {
            response.sendRedirect(request.getContextPath() + "/list-toys");
            return;
        }

        for (int i = 0; i < toyIds.length; i++) {
            OrderItem item = new OrderItem(
                toyIds[i],
                request.getParameter("toyName_" + toyIds[i]),
                Double.parseDouble(request.getParameter("price_" + toyIds[i])),
                Integer.parseInt(quantities[i])
            );
            order.addItem(item);
        }

        DiscountCalculator calculator = DiscountCalculator.getDiscountCalculator(userType);
        double discount = calculator.calculateDiscount(order.getTotalAmount());
        order.setTotalAmount(order.getTotalAmount() - discount);

        FileHandler.saveOrder(order);
        session.setAttribute("lastOrderId", order.getOrderId());
        session.setAttribute("lastOrderTotal", order.getTotalAmount());
        response.sendRedirect(request.getContextPath() + "/add-payment");
    }

    private void updateOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderId = request.getParameter("orderId");
        List<Order> orders = FileHandler.loadOrders();

        Order order = orders.stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if (order != null && "PENDING".equals(order.getStatus())) {
            order.setDeliveryAddress(request.getParameter("deliveryAddress"));
            order.setPaymentMethod(request.getParameter("paymentMethod"));

            order.getItems().clear();
            String[] toyIds = request.getParameterValues("toyId");
            String[] quantities = request.getParameterValues("quantity");

            for (int i = 0; i < toyIds.length; i++) {
                OrderItem item = new OrderItem(
                    toyIds[i],
                    request.getParameter("toyName_" + toyIds[i]),
                    Double.parseDouble(request.getParameter("price_" + toyIds[i])),
                    Integer.parseInt(quantities[i])
                );
                order.addItem(item);
            }

            FileHandler.updateOrder(order);
        }

        response.sendRedirect(request.getContextPath() + "/order/history");
    }

    private void cancelOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderId = request.getParameter("orderId");
        List<Order> orders = FileHandler.loadOrders();

        Order order = orders.stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if (order != null && "PENDING".equals(order.getStatus())) {
            order.setStatus("CANCELLED");
            FileHandler.updateOrder(order);
        }

        response.sendRedirect(request.getContextPath() + "/order/history");
    }

    private void viewOrderHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect="
                    + URLEncoder.encode("/order/history", StandardCharsets.UTF_8));
            return;
        }

        List<Order> orders = FileHandler.getOrdersByUser(userId);
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/OrderHistory.jsp").forward(request, response);
    }

    private void viewOrderDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderId = request.getParameter("orderId");
        List<Order> orders = FileHandler.loadOrders();

        Order order = orders.stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if (order != null) {
            request.setAttribute("order", order);
            request.getRequestDispatcher("/EditOrder.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/order/history");
        }
    }
}
