package DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.CartItem;
import model.Product;

public class CartDB {

    // Get all cart items for a user
    public static List<CartItem> getCartItems(int userId) {
        List<CartItem> items = new ArrayList<>();
        String query = "SELECT ci.product_id, ci.quantity, p.name, p.price, p.stock, p.description " +
                       "FROM cart_items ci " +
                       "JOIN carts c ON ci.cart_id = c.id " +
                       "JOIN products p ON ci.product_id = p.id " +
                       "WHERE c.user_id = ?";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                product.setStock(rs.getInt("stock"));

                CartItem item = new CartItem();
                item.setProduct(product);
                item.setQuantity(rs.getInt("quantity"));

                items.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    // Ensure user has a cart, return cart_id
    private static int getOrCreateCartId(int userId) throws SQLException {
        Connection con = DBconnection.getConnection();

        // Check for existing cart
        String selectCart = "SELECT id FROM carts WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(selectCart)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        // Create new cart if none exists
        String insertCart = "INSERT INTO carts(user_id) VALUES (?)";
        try (PreparedStatement ps = con.prepareStatement(insertCart, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }

        return -1; // error fallback
    }

    // Add item to cart (or update quantity if exists)
    public static void addToCart(int userId, int productId, int quantity) {
        try (Connection con = DBconnection.getConnection()) {
            int cartId = getOrCreateCartId(userId);

            // Check if product already in cart
            String check = "SELECT id, quantity FROM cart_items WHERE cart_id=? AND product_id=?";
            try (PreparedStatement ps = con.prepareStatement(check)) {
                ps.setInt(1, cartId);
                ps.setInt(2, productId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    // Update quantity
                    int newQty = rs.getInt("quantity") + quantity;
                    String update = "UPDATE cart_items SET quantity=? WHERE id=?";
                    try (PreparedStatement ps2 = con.prepareStatement(update)) {
                        ps2.setInt(1, newQty);
                        ps2.setInt(2, rs.getInt("id"));
                        ps2.executeUpdate();
                    }
                } else {
                    // Insert new
                    String insert = "INSERT INTO cart_items(cart_id, product_id, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement ps2 = con.prepareStatement(insert)) {
                        ps2.setInt(1, cartId);
                        ps2.setInt(2, productId);
                        ps2.setInt(3, quantity);
                        ps2.executeUpdate();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update quantity of an item
    public static void updateQuantity(int userId, int productId, int quantity) {
        try (Connection con = DBconnection.getConnection()) {
            int cartId = getOrCreateCartId(userId);

            String query = "UPDATE cart_items SET quantity=? WHERE cart_id=? AND product_id=?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, quantity);
                ps.setInt(2, cartId);
                ps.setInt(3, productId);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Remove item from cart
    public static void removeFromCart(int userId, int productId) {
        try (Connection con = DBconnection.getConnection()) {
            int cartId = getOrCreateCartId(userId);

            String query = "DELETE FROM cart_items WHERE cart_id=? AND product_id=?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, cartId);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clear cart after order placed
    public static void clearCart(int userId) {
        try (Connection con = DBconnection.getConnection()) {
            int cartId = getOrCreateCartId(userId);

            String query = "DELETE FROM cart_items WHERE cart_id=?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, cartId);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
