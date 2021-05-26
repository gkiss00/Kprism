package model;

public class Product {
    public String name;
    public int quantity;
    
    public Product(String name, int quantity){
        this.name = name;
        this.quantity = quantity;
    }

    public Product(Product product){
        this.name = product.name;
        this.quantity = product.quantity;
    }

    @Override
    public boolean equals(Object o){
        if (o == null)
            return false;
        if (o instanceof Product) {
            Product product = (Product)o;
            if(name.compareTo(product.name) == 0 && quantity == product.quantity)
                return true;
            else
                return false;
        } else {
            return false;
        }
    }
}