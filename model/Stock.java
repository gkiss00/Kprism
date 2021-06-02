package model;

public class Stock {
    public String name;
    public int qty;

    public Stock(String name, int qty){
        this.name = name;
        this.qty = qty;
    }

    public Stock(Stock stock){
        this.name = stock.name;
        this.qty = stock.qty;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Stock))
            return false;
        Stock stock = (Stock)obj;
        return (stock.name.compareTo(name) == 0);
    }

    @Override
    public String toString(){
        String str = name + " " + qty;
        return str;
    }
}
