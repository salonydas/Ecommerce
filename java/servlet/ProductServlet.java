package servlet;

import DB.ProductDB;
import model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/ProductServlet")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String categoryIdParam = request.getParameter("category");
        List<Product> products;

        if (categoryIdParam != null) {
            try {
                int categoryId = Integer.parseInt(categoryIdParam);
                products = ProductDB.getProductsByCategory(categoryId);
            } catch (NumberFormatException e) {
                products = ProductDB.getAllProducts();
            }
        } else {
            products = ProductDB.getAllProducts();
        }

        request.setAttribute("products", products);
        request.getRequestDispatcher("products.jsp").forward(request, response);
    }
}
