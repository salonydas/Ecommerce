package servlet;

import DB.CartDB;
import DB.OrderDB;
import model.CartItem;
import model.OrderItem;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/CheckoutServlet")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Load cart items to display in checkout.jsp
        List<CartItem> cartItems = CartDB.getCartItems(user.getId());
        request.setAttribute("cartItems", cartItems);
        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("placeOrder".equals(action)) {
            List<CartItem> cartItems = CartDB.getCartItems(user.getId());

            if (cartItems == null || cartItems.isEmpty()) {
                request.setAttribute("error", "Your cart is empty.");
                request.getRequestDispatcher("checkout.jsp").forward(request, response);
                return;
            }

            double total = 0;
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem ci : cartItems) {
                double price = ci.getProduct().getPrice();
                int qty = ci.getQuantity();
                total += price * qty;

                OrderItem oi = new OrderItem();
                oi.setProductId(ci.getProduct().getId());
                oi.setProductName(ci.getProduct().getName());
                oi.setQuantity(qty);
                oi.setPriceAtPurchase(price);

                orderItems.add(oi);
            }

            // Place order in DB
            int orderId = OrderDB.placeOrder(user.getId(), total, orderItems);

            if (orderId > 0) {
                // Clear cart after placing order
                CartDB.clearCart(user.getId());
                request.setAttribute("success", "Order placed successfully! Order ID: " + orderId);
                response.sendRedirect("orders.jsp");
            } else {
                request.setAttribute("error", "Something went wrong while placing your order.");
                request.getRequestDispatcher("checkout.jsp").forward(request, response);
            }
        }
    }
}
