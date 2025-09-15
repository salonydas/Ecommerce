package model;

import java.util.List;

public class Cart {
    private int id;
    private int userId;
    private List<CartItem> items;  // List of cart items

    public Cart() {}

    public Cart(int id, int userId, List<CartItem> items) {
        this.id = id;
        this.userId = userId;
        this.items = items;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // Calculate total price
    public double getTotal() {
        double total = 0;
        if (items != null) {
            for (CartItem item : items) {
                total += item.getProduct().getPrice() * item.getQuantity();
            }
        }
        return total;
    }
}
