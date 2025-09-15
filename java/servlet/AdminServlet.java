package servlet;

import DB.ProductDB;
import DB.UserDB;
import DB.OrderDB;
import model.Product;
import model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String page = request.getParameter("page");

        if (page == null || page.equals("dashboard")) {
            // Dashboard statistics
            int totalUsers = UserDB.getAllUsers().size();
            int totalProducts = ProductDB.getAllProducts().size();
            int totalOrders = OrderDB.getAllOrders().size();
            double totalRevenue = OrderDB.getTotalRevenue();

            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("totalRevenue", totalRevenue);
            request.getRequestDispatcher("admin/dashboard.jsp").forward(request, response);

        } else if (page.equals("manageProducts")) {
            request.setAttribute("products", ProductDB.getAllProducts());
            request.getRequestDispatcher("admin/manageProducts.jsp").forward(request, response);

        } else if (page.equals("manageUsers")) {
            request.setAttribute("users", UserDB.getAllUsers());
            request.getRequestDispatcher("admin/manageUsers.jsp").forward(request, response);

        } else if (page.equals("manageOrders")) {
            request.setAttribute("orders", OrderDB.getAllOrders());
            request.getRequestDispatcher("admin/manageOrders.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("AdminServlet?page=dashboard");
            return;
        }

        switch (action) {
            case "addProduct":
                addProduct(request);
                response.sendRedirect("AdminServlet?page=manageProducts");
                break;

            case "deleteProduct":
                int productId = Integer.parseInt(request.getParameter("id"));
                ProductDB.deleteProduct(productId);
                response.sendRedirect("AdminServlet?page=manageProducts");
                break;

            case "deleteUser":
                int userId = Integer.parseInt(request.getParameter("id"));
                UserDB.deleteUser(userId);
                response.sendRedirect("AdminServlet?page=manageUsers");
                break;

            case "updateOrderStatus":
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String status = request.getParameter("status");
                OrderDB.updateOrderStatus(orderId, status);
                response.sendRedirect("AdminServlet?page=manageOrders");
                break;

            default:
                response.sendRedirect("AdminServlet?page=dashboard");
                break;
        }
    }

    // Helper: Add product from form
    private void addProduct(HttpServletRequest request) {
        try {
            Product p = new Product();
            p.setName(request.getParameter("name"));
            p.setDescription(request.getParameter("description"));
            p.setPrice(Double.parseDouble(request.getParameter("price")));
            p.setStock(Integer.parseInt(request.getParameter("stock")));
            p.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            p.setImageUrl(request.getParameter("imageUrl"));

            ProductDB.addProduct(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
