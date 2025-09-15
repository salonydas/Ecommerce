package servlet;

import DB.CartDB;
import DB.ProductDB;
import model.CartItem;
import model.Product;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {

    // Handle GET (view) and also support POST by delegating to doGet (common pattern)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser"); // use same session key as LoginServlet

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (action == null || action.equals("view")) {
            // show cart
            List<CartItem> cartItems = CartDB.getCartItems(user.getId());
            request.setAttribute("cartItems", cartItems);
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }

        switch (action) {
            case "add":
                handleAdd(request, response, user);
                response.sendRedirect("CartServlet"); // show cart after change
                break;

            case "remove":
                handleRemove(request, response, user);
                response.sendRedirect("CartServlet");
                break;

            case "update":
                handleUpdate(request, response, user);
                response.sendRedirect("CartServlet");
                break;

            default:
                // unknown action -> show cart
                List<CartItem> cartItems = CartDB.getCartItems(user.getId());
                request.setAttribute("cartItems", cartItems);
                request.getRequestDispatcher("cart.jsp").forward(request, response);
                break;
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response, User user) {
        try {
            String productParam = request.getParameter("productId");
            if (productParam == null) productParam = request.getParameter("id"); // fallback
            if (productParam == null) return;

            int productId = Integer.parseInt(productParam);

            String qtyParam = request.getParameter("quantity");
            int quantity = 1;
            if (qtyParam != null && !qtyParam.isEmpty()) {
                quantity = Math.max(1, Integer.parseInt(qtyParam));
            }

            // Use ProductDB to verify product exists (optional)
            Product product = ProductDB.getProductById(productId);
            if (product == null) return;

            // Call CartDB method available in this project
            CartDB.addToCart(user.getId(), productId, quantity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRemove(HttpServletRequest request, HttpServletResponse response, User user) {
        try {
            String productParam = request.getParameter("productId");
            if (productParam == null) productParam = request.getParameter("id"); // fallback
            if (productParam == null) return;

            int productId = Integer.parseInt(productParam);
            CartDB.removeFromCart(user.getId(), productId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, User user) {
        try {
            String productParam = request.getParameter("productId");
            if (productParam == null) productParam = request.getParameter("id"); // fallback
            if (productParam == null) return;

            int productId = Integer.parseInt(productParam);
            String qtyParam = request.getParameter("quantity");
            if (qtyParam == null) return;

            int quantity = Math.max(1, Integer.parseInt(qtyParam));
            CartDB.updateQuantity(user.getId(), productId, quantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
