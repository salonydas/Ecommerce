package model;

public class CartItem {
    private int id;
    private Product product;
    private int quantity;

    public CartItem() {}

    public CartItem(int id, Product product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Calculate subtotal for this item
    public double getSubtotal() {
        if (product != null) {
            return product.getPrice() * quantity;
        }
        return 0;
    }
}
