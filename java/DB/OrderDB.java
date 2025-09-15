package DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import model.OrderItem;
import model.Product;

public class OrderDB {

    // Place a new order
    public static int placeOrder(int userId, double totalAmount, List<OrderItem> items) {
        int orderId = -1;
        String orderSql = "INSERT INTO orders(user_id, total_amount, status) VALUES (?, ?, 'PLACED')";
        String itemSql = "INSERT INTO order_items(order_id, product_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";
        String stockSql = "UPDATE products SET stock = stock - ? WHERE id=?";

        try (Connection con = DBconnection.getConnection()) {
            con.setAutoCommit(false); // Start transaction

            // Insert order
            try (PreparedStatement ps = con.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setDouble(2, totalAmount);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
            }

            // Insert order items & reduce stock
            try (PreparedStatement psItem = con.prepareStatement(itemSql);
                 PreparedStatement psStock = con.prepareStatement(stockSql)) {
                for (OrderItem item : items) {
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, item.getProductId());
                    psItem.setInt(3, item.getQuantity());
                    psItem.setDouble(4, item.getPriceAtPurchase());
                    psItem.addBatch();

                    psStock.setInt(1, item.getQuantity());
                    psStock.setInt(2, item.getProductId());
                    psStock.addBatch();
                }
                psItem.executeBatch();
                psStock.executeBatch();
            }

            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderId;
    }

    // Get all orders for a specific user
    public static List<Order> getOrdersByUser(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id=? ORDER BY created_at DESC";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Get all orders (for admin)
    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Get order items for an order
    public static List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id WHERE order_id=?";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPriceAtPurchase(rs.getDouble("price_at_purchase"));
                items.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    // Update order status (for admin)
    public static void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status=? WHERE id=?";
        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get total revenue (for admin dashboard)
    public static double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM orders";
        double total = 0;
        try (Connection con = DBconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
}
